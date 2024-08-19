package com.learning.transactionmasterdata.module.stock.model;

import com.learning.transactionmasterdata.module.base.BaseModel;
import com.learning.transactionmasterdata.module.currency.model.Currency;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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

    private BigInteger masterStock;

    private BigInteger dynamicStock;

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
