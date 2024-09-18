package com.learning.transaction.masterdata.module.stock.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class StockRequest {
    private String currencyId;
    private BigDecimal masterStock;
    private BigDecimal dynamicStock;
}
