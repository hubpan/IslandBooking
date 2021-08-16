package com.hubpan.IslandBooking.db;

import com.hubpan.IslandBooking.core.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Optional<Account> findByEmailIgnoreCase(String email);
}
