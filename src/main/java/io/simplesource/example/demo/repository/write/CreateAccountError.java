package io.simplesource.example.demo.repository.write;

import io.simplesource.api.CommandError;

public abstract class CreateAccountError extends CommandError {
    private CreateAccountError(String message) {
        super(message);
    }

    public static final class AccountAlreadyExists extends CreateAccountError {
        public AccountAlreadyExists() {
            super("Account already exists");
        }
    }

    public static final class AccountNameNotSet extends CreateAccountError {
        public AccountNameNotSet() {
            super("Account name is not set");
        }
    }

    public static final class OpeningBalanceLessThanZero extends CreateAccountError {
        public OpeningBalanceLessThanZero() {
            super("Opening balance cannot be negative");
        }
    }

}
