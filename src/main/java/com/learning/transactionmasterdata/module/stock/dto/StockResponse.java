package com.learning.transactionmasterdata.module.stock.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class StockResponse {
    private String id;
    private String currencyId;
    private String currencyName;
    private String currencyCode;
    private BigInteger masterStock;
    private BigInteger dynamicStock;
    private String createdAt;
    private String updatedAt;
}
