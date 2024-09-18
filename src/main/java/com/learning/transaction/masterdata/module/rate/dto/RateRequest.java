package com.learning.transaction.masterdata.module.rate.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RateRequest {
    private String currencyId;
    private BigDecimal baseRate;
}
