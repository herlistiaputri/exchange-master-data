package com.learning.transaction.masterdata.module.base.auditing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.learning.transaction.masterdata.module.currency.model.Currency;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MasterDataLoggingService {

    private final MasterDataLoggingRepository repository;
    private final EntityManager entityManager;

    public void create(MasterDataLoggingRequest request){
        MasterDataLogging logging = new MasterDataLogging();
        logging.setAction(request.getAction().name());
        logging.setFeatureName(request.getFeatureName().name());
        logging.setValueBefore(request.getBefore());
        logging.setValueAfter(request.getAfter());
        repository.save(logging);
    }

    public List<MasterDataLogging> getLogFeature(FeatureEnum featureName){
        return repository.findAllByFeatureName(featureName);
    }

    public MasterDataLoggingRequest loggingRequest(FeatureEnum feature, ActionEnum action, Object before, Object after) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Disable timestamps
            objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

            MasterDataLoggingRequest loggingRequest = new MasterDataLoggingRequest();
            loggingRequest.setAction(action);
            loggingRequest.setFeatureName(feature);
            loggingRequest.setBefore(before == null ? null : objectMapper.writeValueAsString(before));
            loggingRequest.setAfter(after == null ? null : objectMapper.writeValueAsString(after));
            return loggingRequest;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public Page<MasterDataLogging> getPage(int page, int size, String sortBy, String sortDir, String feature){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MasterDataLogging> cq = cb.createQuery(MasterDataLogging.class);
        Root<MasterDataLogging> root = cq.from(MasterDataLogging.class);

        // Apply search filter
        if (feature != null && !feature.isEmpty()) {
            Predicate filterFeature = cb.equal(root.get("featureName"), feature.toUpperCase());
            cq.where(cb.and(filterFeature));
        }

        // Apply sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            if(sortDir.equalsIgnoreCase("asc")) {
                cq.orderBy(cb.asc(root.get(sortBy)));
            } else {
                cq.orderBy(cb.desc(root.get(sortBy)));
            }
        }

        // Create Pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Get results with pagination
        List<MasterDataLogging> resultList = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Count total records for pagination purposes
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(Currency.class)));
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultList, pageable, count);
    }

}
