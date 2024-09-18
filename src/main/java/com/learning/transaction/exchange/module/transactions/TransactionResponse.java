package com.learning.transaction.exchange.module.transactions;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionResponse {

    private String id;
    private String trxCode;
    private String currencyCode;
    private String transactionType;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal totalIdr;
    private String createdAt;

}
