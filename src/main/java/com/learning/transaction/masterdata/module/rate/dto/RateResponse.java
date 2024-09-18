package com.learning.transaction.masterdata.module.rate.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class RateResponse {
    private String id;
    private String currencyId;
    private String currencyCode;
    private String currencyName;
    private BigDecimal baseRate;
    private BigDecimal rateRounding;
    private BigDecimal buyRounding;
    private BigDecimal sellRounding;
}
