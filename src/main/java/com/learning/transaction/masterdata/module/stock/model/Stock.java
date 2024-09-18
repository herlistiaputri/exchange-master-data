package com.learning.transaction.masterdata.module.stock.model;

import com.learning.transaction.masterdata.module.base.BaseModel;
import com.learning.transaction.masterdata.module.currency.model.Currency;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@Data
@Entity
@Table(name = "stock")
public class Stock extends BaseModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id = UUID.randomUUID().toString();

    @OneToOne
    private Currency currency;

    private BigDecimal masterStock;

    private BigDecimal dynamicStock;

    private boolean editable;

    public Stock(Stock stock) {
        this.id = stock.id;
        this.currency = stock.currency;
        this.masterStock = stock.masterStock;
        this.dynamicStock = stock.dynamicStock;
        this.editable = stock.editable;
    }

    public Stock() {
    }
}
