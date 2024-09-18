package com.learning.transaction.masterdata.module.stock.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class StockResponse {
    private String id;
    private String currencyId;
    private String currencyName;
    private String currencyCode;
    private BigDecimal masterStock;
    private BigDecimal dynamicStock;
    private String createdAt;
    private String updatedAt;
}
