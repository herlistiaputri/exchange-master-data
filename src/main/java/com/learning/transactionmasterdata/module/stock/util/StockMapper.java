package com.learning.transactionmasterdata.module.stock.util;

import com.learning.transactionmasterdata.module.currency.model.Currency;
import com.learning.transactionmasterdata.module.stock.dto.StockRequest;
import com.learning.transactionmasterdata.module.stock.dto.StockResponse;
import com.learning.transactionmasterdata.module.stock.model.Stock;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockMapper {

    public static Stock mapObject(StockRequest request, Stock stock, Currency currency) {
        stock.setCurrency(currency);
        stock.setMasterStock(request.getMasterStock());
        stock.setDynamicStock(request.getDynamicStock());
        stock.setEditable(true);
        return stock;
    }

    public static StockResponse mapResponse(Stock stock){
        StockResponse response = new StockResponse();
        response.setId(stock.getId());
        response.setCurrencyId(stock.getCurrency().getId());
        response.setCurrencyName(stock.getCurrency().getCurrencyName());
        response.setCurrencyCode(stock.getCurrency().getCurrencyCode());
        response.setMasterStock(stock.getMasterStock());
        response.setDynamicStock(stock.getDynamicStock());
        response.setCreatedAt(stock.getCreatedAt().toString());
        response.setUpdatedAt(stock.getUpdatedAt().toString());
        return response;
    }

    public static List<StockResponse> mapResponses(List<Stock> stocks) {
        return stocks.stream().map(StockMapper::mapResponse).collect(Collectors.toList());
    }
}
