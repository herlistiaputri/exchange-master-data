package com.learning.transactionmasterdata.module.currency.repository;

import com.learning.transactionmasterdata.module.currency.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
    boolean existsByCurrencyCodeOrCurrencyName(String currencyCode, String currencyName);
}
