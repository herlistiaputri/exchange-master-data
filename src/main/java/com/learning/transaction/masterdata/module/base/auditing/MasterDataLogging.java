package com.learning.transaction.masterdata.module.base.auditing;

import com.learning.transaction.masterdata.module.base.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;


@Entity
@Data
@Table(name = "master_data_logging")
public class MasterDataLogging extends BaseModel {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id = UUID.randomUUID().toString();

    private String featureName;
    private String action;
    private String valueBefore;
    private String valueAfter;




}
