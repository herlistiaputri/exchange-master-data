package com.learning.transactionmasterdata.module.stock.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class StockRequest {
    private String currencyId;
    private BigInteger masterStock;
    private BigInteger dynamicStock;
}
