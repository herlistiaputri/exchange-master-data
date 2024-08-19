package com.learning.transactionmasterdata.module.rate.repository;

import com.learning.transactionmasterdata.module.currency.model.Currency;
import com.learning.transactionmasterdata.module.rate.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<Rate, String> {
    boolean existsByCurrency(Currency currency);
}
