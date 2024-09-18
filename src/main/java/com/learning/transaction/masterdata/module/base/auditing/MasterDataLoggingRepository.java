package com.learning.transaction.masterdata.module.base.auditing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterDataLoggingRepository extends JpaRepository<MasterDataLogging, String> {

    List<MasterDataLogging> findAllByFeatureName(FeatureEnum featureName);
}
