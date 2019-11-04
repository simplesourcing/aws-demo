package io.simplesource.example.demo.repository.write;

import java.util.Optional;
import java.util.function.Function;

public abstract class WithdrawError {
    private WithdrawError() {}

    public abstract String message();

    public static final AccountNotFound ACCOUNT_NOT_FOUND = new AccountNotFound();
    public static final InsufficientFunds INSUFFICIENT_FUNDS = new InsufficientFunds();
    public static final SequenceNotMatch SEQUENCE_NOT_MATCH = new SequenceNotMatch();

    public abstract <T> T match(
            Function<AccountNotFound, T> f1,
            Function<InsufficientFunds, T> f2,
            Function<SequenceNotMatch, T> f3
    );

    public static final class AccountNotFound extends WithdrawError {
        private AccountNotFound() {}

        @Override
        public String message() {
            return "Account not found";
        }

        @Override
        public <T> T match(Function<AccountNotFound, T> f1, Function<InsufficientFunds, T> f2, Function<SequenceNotMatch, T> f3) {
            return f1.apply(this);
        }
    }

    public static final class InsufficientFunds extends WithdrawError {
        private InsufficientFunds() {}

        @Override
        public String message() {
            return "Account has insufficient funds";
        }

        @Override
        public <T> T match(Function<AccountNotFound, T> f1, Function<InsufficientFunds, T> f2, Function<SequenceNotMatch, T> f3) {
            return f2.apply(this);
        }
    }

    public static final class SequenceNotMatch extends WithdrawError {
        private SequenceNotMatch() {}

        @Override
        public String message() {
            return "Sequence mismatch error";
        }

        @Override
        public <T> T match(Function<AccountNotFound, T> f1, Function<InsufficientFunds, T> f2, Function<SequenceNotMatch, T> f3) {
            return f3.apply(this);
        }
    }

    public static Optional<WithdrawError> fromString(String s) {
        if(ACCOUNT_NOT_FOUND.message().equals(s)) {
            return Optional.of(ACCOUNT_NOT_FOUND);
        } else if (INSUFFICIENT_FUNDS.message().equals(s)) {
            return Optional.of(INSUFFICIENT_FUNDS);
        } else if (SEQUENCE_NOT_MATCH.message().equals(s)) {
            return Optional.of(SEQUENCE_NOT_MATCH);
        } else {
            return Optional.empty();
        }
    }

}
