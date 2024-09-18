package com.learning.transaction.exchange.module.transactions;

import com.learning.transaction.masterdata.module.stock.util.StockMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {

    public static TransactionResponse mapResponse(Transaction transaction){
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setTrxCode(transaction.getTrxCode());
        response.setCurrencyCode(transaction.getCurrency().getCurrencyCode());
        response.setTransactionType(transaction.getTransactionType());
        response.setRate(transaction.getTransactionType().equalsIgnoreCase("Buy") ? transaction.getRate().getBuyRate() : transaction.getRate().getSellRate());
        response.setAmount(transaction.getAmount());
        response.setTotalIdr(transaction.getTotalIdr());
        response.setCreatedAt(transaction.getCreatedAt().toString());
        return response;
    }

    public static List<TransactionResponse> mapResponses(List<Transaction> transactions){
        return transactions.stream().map(TransactionMapper::mapResponse).collect(Collectors.toList());
    }
}
