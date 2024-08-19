package com.learning.transactionmasterdata.module.base.auditing;

import lombok.Data;

@Data
public class MasterDataLoggingResponse {

    private String id;
    private String featureName;
    private String actionName;
    private Object valueBefore;
    private Object valueAfter;
    private String updatedAt;

}
