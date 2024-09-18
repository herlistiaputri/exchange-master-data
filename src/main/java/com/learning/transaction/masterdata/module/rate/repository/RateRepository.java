package com.learning.transaction.masterdata.module.rate.repository;

import com.learning.transaction.masterdata.module.rate.model.Rate;
import com.learning.transaction.masterdata.module.currency.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, String> {
    boolean existsByCurrency(Currency currency);

    Optional<Rate> findByCurrency(Currency currency);
}
