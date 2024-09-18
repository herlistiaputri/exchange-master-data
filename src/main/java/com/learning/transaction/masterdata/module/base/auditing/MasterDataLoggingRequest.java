package com.learning.transaction.masterdata.module.base.auditing;

import lombok.Data;

@Data
public class MasterDataLoggingRequest {
    private ActionEnum action;
    private FeatureEnum featureName;
    private String before;
    private String after;
}
