package com.learning.transactionmasterdata.module.currency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.learning.transactionmasterdata.configuration.exception.BaseException;
import com.learning.transactionmasterdata.module.base.auditing.ActionEnum;
import com.learning.transactionmasterdata.module.base.auditing.FeatureEnum;
import com.learning.transactionmasterdata.module.base.auditing.MasterDataLoggingRequest;
import com.learning.transactionmasterdata.module.base.auditing.MasterDataLoggingService;
import com.learning.transactionmasterdata.module.currency.dto.CurrencyRequest;
import com.learning.transactionmasterdata.module.currency.model.Currency;
import com.learning.transactionmasterdata.module.currency.repository.CurrencyRepository;
import com.learning.transactionmasterdata.module.currency.util.CurrencyMapper;
import com.learning.transactionmasterdata.module.stock.service.StockService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CurrencyService {

    private final CurrencyRepository repository;
    private final MasterDataLoggingService loggingService;
    private final EntityManager entityManager;

    public void create(CurrencyRequest request) {
        if(!repository.existsByCurrencyCodeOrCurrencyName(request.getCurrencyCode(), request.getCurrencyName())) {
            Currency currency = repository.save(CurrencyMapper.objectMapper(request, new Currency()));

            loggingService.create(loggingService.loggingRequest(FeatureEnum.CURRENCY, ActionEnum.CREATE, null, currency));

        } else {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), null, HttpStatus.BAD_REQUEST.getReasonPhrase());
        }
    }

    public Currency getById(String id){
        return repository.findById(id).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), null, HttpStatus.NOT_FOUND.name()));
    }

    public List<Currency> getList(){
        return repository.findAll();
    }

    public Page<Currency> getPage(int page, int size, String sortBy, String sortDir, String search) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Currency> cq = cb.createQuery(Currency.class);
        Root<Currency> root = cq.from(Currency.class);

        // Apply search filter
        if (search != null && !search.isEmpty()) {
            Predicate currencyNamePredicate = cb.like(root.get("currencyName"), "%" + search + "%");
            Predicate currencyCodePredicate = cb.like(root.get("currencyCode"), "%" + search + "%");
            cq.where(cb.or(currencyCodePredicate, currencyNamePredicate));
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
        List<Currency> resultList = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Count total records for pagination purposes
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(Currency.class)));
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultList, pageable, count);
    }

    public void update(String id, CurrencyRequest request) {
        Optional<Currency> currencyOptional = repository.findById(id);
        if(currencyOptional.isPresent()){
            Currency oldValue = new Currency(currencyOptional.get());
            Currency currency = repository.save(CurrencyMapper.objectMapper(request, currencyOptional.get()));

            loggingService.create(loggingService.loggingRequest(FeatureEnum.CURRENCY, ActionEnum.UPDATE, oldValue, currency));
        }
    }

//    private MasterDataLoggingRequest loggingRequest(ActionEnum action, Object before, Object after) {
//        try{
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.registerModule(new JavaTimeModule());
//            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Disable timestamps
//            objectMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
//
//            MasterDataLoggingRequest loggingRequest = new MasterDataLoggingRequest();
//            loggingRequest.setAction(action);
//            loggingRequest.setFeatureName(FeatureEnum.CURRENCY);
//            loggingRequest.setBefore(before == null ? null : objectMapper.writeValueAsString(before));
//            loggingRequest.setAfter(after == null ? null : objectMapper.writeValueAsString(after));
//            return loggingRequest;
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
}
