package com.learning.transaction.exchange.module.transactions;

import com.learning.transaction.masterdata.module.base.BaseModel;
import com.learning.transaction.masterdata.module.currency.model.Currency;
import com.learning.transaction.masterdata.module.rate.model.Rate;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "transaction")
public class Transaction extends BaseModel {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String trxCode;
    @ManyToOne
    private Currency currency;
    private String transactionType;
    @ManyToOne
    private Rate rate;
    private BigDecimal amount;
    private BigDecimal totalIdr;

}
