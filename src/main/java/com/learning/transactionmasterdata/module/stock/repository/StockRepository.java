package com.learning.transactionmasterdata.module.stock.repository;

import com.learning.transactionmasterdata.module.currency.model.Currency;
import com.learning.transactionmasterdata.module.stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
    boolean existsByCurrency(Currency currency);
}
