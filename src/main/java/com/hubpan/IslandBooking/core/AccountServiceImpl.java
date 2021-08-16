package com.hubpan.IslandBooking.core;

import com.hubpan.IslandBooking.core.model.Account;
import com.hubpan.IslandBooking.db.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account createOrUpdate(Account account) {
        Optional<Account> accountOptional = accountRepository.findByEmailIgnoreCase(account.getEmail());
        if (accountOptional.isEmpty()) {
            return accountRepository.save(account);
        }

        Account existing = accountOptional.get();
        existing.setFirstName(account.getFirstName());
        existing.setLastName(account.getLastName());

        return accountRepository.save(existing);
    }
}
