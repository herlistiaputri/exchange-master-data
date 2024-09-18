package com.learning.transaction.masterdata.module.rate.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RateRoundingRequest {
    private BigDecimal baseRounding;
}
