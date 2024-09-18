package com.learning.transaction.masterdata.module.rate.model;

import com.learning.transaction.masterdata.module.base.BaseModel;
import com.learning.transaction.masterdata.module.currency.model.Currency;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "rate")
public class Rate extends BaseModel {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id = UUID.randomUUID().toString();
    @OneToOne
    private Currency currency;
    private BigDecimal baseRate;
    private BigDecimal rateRounding;
    private BigDecimal suggestionBuyRate;
    private BigDecimal suggestionSellRate;
    private BigDecimal buyRate;
    private BigDecimal sellRate;

    public Rate(Rate rate) {
        this.id = rate.id;
        this.currency = rate.currency;
        this.baseRate = rate.baseRate;
        this.rateRounding = rate.rateRounding;
        this.suggestionBuyRate = rate.suggestionBuyRate;
        this.suggestionSellRate = rate.suggestionSellRate;
        this.buyRate = rate.buyRate;
        this.sellRate = rate.sellRate;
    }

    public Rate(){
    }
}
