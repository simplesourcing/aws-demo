package io.simplesource.example.demo.repository.write;

import io.simplesource.api.CommandError;

public abstract class CreateAccountError extends CommandError {
    private CreateAccountError(String message) {
        super(message);
    }

    public static final class AccountAlreadyExists extends CreateAccountError {
        private static final long serialVersionUID = 8534432619596383268L;

        public AccountAlreadyExists() {
            super("Account already exists");
        }
    }

    public static final class AccountNameNotSet extends CreateAccountError {
        private static final long serialVersionUID = 3464543909320178223L;

        public AccountNameNotSet() {
            super("Account name is not set");
        }
    }

    public static final class OpeningBalanceLessThanZero extends CreateAccountError {
        private static final long serialVersionUID = -7686952430796247443L;

        public OpeningBalanceLessThanZero() {
            super("Opening balance cannot be negative");
        }
    }

}
