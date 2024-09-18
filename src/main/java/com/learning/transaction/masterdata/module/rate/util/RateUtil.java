package com.learning.transaction.masterdata.module.rate.util;

import com.learning.transaction.masterdata.module.currency.model.Currency;
import com.learning.transaction.masterdata.module.rate.dto.RateRequest;
import com.learning.transaction.masterdata.module.rate.dto.RateResponse;
import com.learning.transaction.masterdata.module.rate.model.Rate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RateUtil {

    public static Rate mapObject(RateRequest request, Currency currency){
        Rate rate = new Rate();
        rate.setCurrency(currency);
        rate.setBaseRate(request.getBaseRate());
        rate.setRateRounding(BigDecimal.ZERO);
        rate.setSuggestionBuyRate(rate.getBaseRate().subtract(countRateRounding(BigDecimal.ZERO, request.getBaseRate())));
        rate.setSuggestionSellRate(rate.getBaseRate().add(countRateRounding(BigDecimal.ZERO, request.getBaseRate())));
        rate.setBuyRate(rate.getBaseRate().subtract(countRateRounding(BigDecimal.ZERO, request.getBaseRate())));
        rate.setSellRate(rate.getBaseRate().add(countRateRounding(BigDecimal.ZERO, request.getBaseRate())));
        return rate;
    }

    public static RateResponse mapResponse(Rate rate){
        RateResponse response = new RateResponse();
        response.setId(rate.getId());
        response.setCurrencyId(rate.getCurrency().getId());
        response.setCurrencyCode(rate.getCurrency().getCurrencyCode());
        response.setCurrencyName(rate.getCurrency().getCurrencyName());
        response.setBaseRate(rate.getBaseRate());
        response.setRateRounding(rate.getRateRounding());
        response.setBuyRounding(rate.getBuyRate());
        response.setSellRounding(rate.getSellRate());
        return response;
    }

    public static List<RateResponse> mapResponses(List<Rate> rateList){
        return rateList.stream().map(RateUtil::mapResponse).collect(Collectors.toList());
    }


    public static BigDecimal countRateRounding(BigDecimal rateRounding, BigDecimal baseRate) {
        BigDecimal percentage = (rateRounding.divide(BigDecimal.valueOf(100),  2, BigDecimal.ROUND_HALF_UP));
       return baseRate.multiply(percentage);
    }
}
