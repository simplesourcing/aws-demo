package io.simplesource.example.demo.repository.write.simplesource;

import io.simplesource.api.CommandAPI;
import io.simplesource.api.CommandError;
import io.simplesource.api.CommandId;
import io.simplesource.data.FutureResult;
import io.simplesource.data.Result;
import io.simplesource.data.Sequence;
import io.simplesource.example.demo.repository.write.AccountWriteRepository;
import io.simplesource.example.demo.repository.write.CreateAccountError;
import io.simplesource.example.demo.repository.write.DepositError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Optional;

/**
 * Use Simplesourcing as the write store
 */
public class SimplesourceAccountWriteRepository implements AccountWriteRepository {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);
    private static final Logger log = LoggerFactory.getLogger(SimplesourceAccountWriteRepository.class);

    private CommandAPI<String, AccountCommand> commandApi;

    public SimplesourceAccountWriteRepository(@Autowired CommandAPI<String, AccountCommand> commandApi){
        this.commandApi = commandApi;
    }

    @Override
    public Optional<CreateAccountError> create(String accountName, double openingBalance) {
        FutureResult<CommandError, Sequence> result = commandApi.publishAndQueryCommand(new CommandAPI.Request<>(CommandId.random(), accountName, Sequence.first(), new AccountCommand.CreateAccount(accountName, openingBalance)), DEFAULT_TIMEOUT);

       //TODO handle future resolution and error handling properly, below is a quick hacky just do it implementation
        final Result<CommandError, Sequence> resolved = result.unsafePerform(e -> CommandError.of(CommandError.Reason.CommandHandlerFailed, e.getMessage()));


        if(resolved.failureReasons().isPresent()){
            if(resolved.failureReasons().get().head().getReason() == CommandError.Reason.InvalidReadSequence) {
                return Optional.of(CreateAccountError.ACCOUNT_ALREADY_EXISTS);
            }

            Optional<CreateAccountError> error = CreateAccountError.fromString(resolved.failureReasons().get().head().getMessage());

            if(error.isPresent()) {
                return error;
            } else {
                throw new RuntimeException(resolved.failureReasons().get().head().getMessage());
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<DepositError> deposit(String account, double amount, Sequence version) {
        FutureResult<CommandError, Sequence> result = commandApi.publishAndQueryCommand(new CommandAPI.Request<>(CommandId.random(), account, version, new AccountCommand.Deposit(amount)), DEFAULT_TIMEOUT);

        Result<CommandError, Sequence> resolved = result.unsafePerform(e -> CommandError.of(CommandError.Reason.InternalError, e.getMessage()));

        Optional<CommandError> commandError = resolved.failureReasons().flatMap(l -> Optional.of(l.head()));

        return commandError.<DepositError>flatMap(e -> {
            // TODO should be a better way to get AggregateNotFound without the and matching strings below
            if(e.getReason() == CommandError.Reason.AggregateNotFound) {
                return Optional.of(DepositError.ACCOUNT_NOT_FOUND);
            }

            if(e.getReason() == CommandError.Reason.InvalidReadSequence && e.getMessage().startsWith("Command received with read sequence") && e.getMessage().endsWith("expecting 0")) {
                return Optional.of(DepositError.ACCOUNT_NOT_FOUND);
            }

            // TODO shouldn't be throwing exceptions here
            return Optional.of(DepositError.fromString(e.getMessage()).orElseThrow(() -> new RuntimeException(e.getReason() + ": " + e.getMessage())));
        });
    }

    @Override
    public void withdraw(String account, double amount, Sequence position) {
        FutureResult<CommandError, Sequence> result = commandApi.publishAndQueryCommand(new CommandAPI.Request<>(CommandId.random(), account, position, new AccountCommand.Withdraw(amount)), DEFAULT_TIMEOUT);

        Result<CommandError, Sequence> commandErrorSequenceResult = result.unsafePerform(e -> CommandError.of(CommandError.Reason.InternalError, e.getMessage()));

        commandErrorSequenceResult.failureReasons()
                .map( errors -> (Runnable) () -> {
                    log.info("Failed depositing {} in account {} with seq {}", amount, account, position.getSeq());
                    errors.forEach(error -> {
                        log.error("  - {}", error.getMessage());
                    });
                    throw new RuntimeException("Withdraw failed"); // TODO should return a value
                })
                .orElse(() -> {})
                .run();
    }
}
