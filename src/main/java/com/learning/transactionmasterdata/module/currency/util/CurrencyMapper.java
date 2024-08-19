package com.learning.transactionmasterdata.module.currency.util;

import com.learning.transactionmasterdata.module.currency.dto.CurrencyRequest;
import com.learning.transactionmasterdata.module.currency.dto.CurrencyResponse;
import com.learning.transactionmasterdata.module.currency.model.Currency;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CurrencyMapper {

    public static Currency objectMapper(CurrencyRequest request, Currency object){
        object.setCurrencyCode(request.getCurrencyCode());
        object.setCurrencyName(request.getCurrencyName());
        return object;
    }

    public static CurrencyResponse responseMapper(Currency object){
        CurrencyResponse response = new CurrencyResponse();
        response.setId(object.getId());
        response.setCurrencyName(object.getCurrencyName());
        response.setCurrencyCode(object.getCurrencyCode());
        response.setCreatedAt(object.getCreatedAt().toString());
        response.setUpdatedAt(object.getUpdatedAt().toString());
        return response;
    }

    public static List<CurrencyResponse> responsesMapper(List<Currency> currencies) {
        return currencies.stream().map(CurrencyMapper::responseMapper).collect(Collectors.toList());
    }
}
