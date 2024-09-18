package com.learning.transaction.masterdata.module.currency.model;

import com.learning.transaction.masterdata.module.base.BaseModel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "currency")
@Data
public class Currency extends BaseModel {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id = UUID.randomUUID().toString();
    @Column
    private String currencyName;
    @Column
    private String currencyCode;

    public Currency(Currency currency) {
        this.id = currency.id;
        this.currencyName = currency.currencyName;
        this.currencyCode = currency.currencyCode;
    }

    public Currency() {
    }

}
