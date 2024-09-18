package com.learning.transaction.masterdata.module.stock.repository;

import com.learning.transaction.masterdata.module.currency.model.Currency;
import com.learning.transaction.masterdata.module.stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
    boolean existsByCurrency(Currency currency);

    Optional<Stock> findByCurrency(Currency currency);
}
