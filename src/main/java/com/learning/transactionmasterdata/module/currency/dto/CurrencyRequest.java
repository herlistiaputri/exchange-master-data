package com.learning.transactionmasterdata.module.currency.dto;

import lombok.Data;

@Data
public class CurrencyRequest {
    private String currencyName;
    private String currencyCode;
}
