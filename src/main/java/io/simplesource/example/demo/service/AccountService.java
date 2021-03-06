package io.simplesource.example.demo.service;

import io.simplesource.example.demo.domain.AccountSummary;
import io.simplesource.example.demo.domain.AccountTransaction;
import io.simplesource.example.demo.repository.write.CreateAccountError;
import io.simplesource.example.demo.repository.write.DepositError;
import io.simplesource.example.demo.repository.write.WithdrawError;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    boolean accountExists(String accountName);

    Optional<AccountSummary> getAccountSummary(String accountName);

    Optional<CreateAccountError> createAccount(String name, double openingBalance);

    List<AccountSummary> list();

    Optional<DepositError> deposit(String account, double amount, long sequence);

    Optional<WithdrawError> withdraw(String account, double amount, long sequence);

    List<AccountTransaction> getTransactions(String account);

}
