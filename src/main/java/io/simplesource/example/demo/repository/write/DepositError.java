package io.simplesource.example.demo.repository.write;

import java.util.Optional;
import java.util.function.Function;

public abstract class DepositError {
    private DepositError() {}

    public abstract String message();

    public static final AccountNotFound ACCOUNT_NOT_FOUND = new AccountNotFound();
    public static final DepositNegativeNumber DEPOSIT_NEGATIVE_NUMBER = new DepositNegativeNumber();
    public static final SequenceNotMatch SEQUENCE_NOT_MATCH = new SequenceNotMatch();

    public abstract <T> T match(
            Function<AccountNotFound, T> f1,
            Function<DepositNegativeNumber, T> f2,
            Function<SequenceNotMatch, T> f3
    );


    public static final class AccountNotFound extends DepositError {
        private AccountNotFound() {}

        @Override
        public <T> T match(Function<AccountNotFound, T> f1, Function<DepositNegativeNumber, T> f2, Function<SequenceNotMatch, T> f3) {
            return f1.apply(this);
        }

        @Override
        public String message() {
            return "Account does not found";
        }
    }


    public static final class DepositNegativeNumber extends DepositError {
        private DepositNegativeNumber() {}

        @Override
        public <T> T match(Function<AccountNotFound, T> f1, Function<DepositNegativeNumber, T> f2, Function<SequenceNotMatch, T> f3) {
            return f2.apply(this);
        }

        @Override
        public String message() {
            return "Cannot deposit a negative value";
        }

    }

    public static final class SequenceNotMatch extends DepositError {
        private SequenceNotMatch() {}

        @Override
        public <T> T match(Function<AccountNotFound, T> f1, Function<DepositNegativeNumber, T> f2, Function<SequenceNotMatch, T> f3) {
            return f3.apply(this);
        }

        @Override
        public String message() {
            return "Account version is outdated";
        }
    }

    public static Optional<DepositError> fromString(String s) {
        if(s.equals(ACCOUNT_NOT_FOUND.message())) {
            return Optional.of(ACCOUNT_NOT_FOUND);
        } else if (s.equals(DEPOSIT_NEGATIVE_NUMBER.message())) {
            return Optional.of(DEPOSIT_NEGATIVE_NUMBER);
        } else if (s.equals(Optional.of(SEQUENCE_NOT_MATCH))) {
            return Optional.of(SEQUENCE_NOT_MATCH);
        } else {
            return Optional.empty();
        }
    }
}
