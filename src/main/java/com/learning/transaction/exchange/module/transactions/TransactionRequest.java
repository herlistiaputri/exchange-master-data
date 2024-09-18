package com.learning.transaction.exchange.module.transactions;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private TransactionTypeEnum type;
    private String rateId;
    private BigDecimal amount;
    private BigDecimal totalIdr;

}
