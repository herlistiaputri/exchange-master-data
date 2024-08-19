package com.learning.transactionmasterdata.module.rate.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRateRequest {
    private BigDecimal sellRate;
    private BigDecimal buyRate;
}
