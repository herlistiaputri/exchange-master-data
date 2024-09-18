package com.learning.transaction.exchange.module.journal;

import com.learning.transaction.exchange.module.transactions.Transaction;
import com.learning.transaction.exchange.module.transactions.TransactionTypeEnum;
import com.learning.transaction.masterdata.module.base.BaseModel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "journal")
public class Journal extends BaseModel {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String trxCode;
    private String journalType;
    private String currencyCode;
    private BigDecimal amount;
    private String journalSource;
}
