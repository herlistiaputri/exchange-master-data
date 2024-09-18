package com.learning.transaction.masterdata.module.currency.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class CurrencyResponse {
    private String id;
    private String currencyName;
    private String currencyCode;
    private String createdAt;
    private String updatedAt;
}
