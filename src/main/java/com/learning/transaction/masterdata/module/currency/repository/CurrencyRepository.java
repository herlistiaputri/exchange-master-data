package com.learning.transaction.masterdata.module.currency.repository;

import com.learning.transaction.masterdata.module.currency.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.format.number.money.CurrencyUnitFormatter;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
    boolean existsByCurrencyCodeOrCurrencyName(String currencyCode, String currencyName);
    Optional<Currency> findByCurrencyCode(String currencyCode);
}
