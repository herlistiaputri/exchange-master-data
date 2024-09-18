package com.learning.transaction.exchange.module.journal;

import com.learning.transaction.exchange.module.transactions.Transaction;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JournalRequest {
    private String trxCode;
    private JournalTypeEnum journalType;
    private BigDecimal amount;
    private String currencyCode;
    private String journalSource;
}
