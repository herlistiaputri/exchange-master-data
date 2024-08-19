package com.learning.transactionmasterdata.module.rate.service;

import com.learning.transactionmasterdata.configuration.exception.BaseException;
import com.learning.transactionmasterdata.module.base.auditing.ActionEnum;
import com.learning.transactionmasterdata.module.base.auditing.FeatureEnum;
import com.learning.transactionmasterdata.module.base.auditing.MasterDataLoggingService;
import com.learning.transactionmasterdata.module.currency.model.Currency;
import com.learning.transactionmasterdata.module.currency.repository.CurrencyRepository;
import com.learning.transactionmasterdata.module.rate.dto.RateRequest;
import com.learning.transactionmasterdata.module.rate.dto.RateRoundingRequest;
import com.learning.transactionmasterdata.module.rate.dto.TransactionRateRequest;
import com.learning.transactionmasterdata.module.rate.model.Rate;
import com.learning.transactionmasterdata.module.rate.repository.RateRepository;
import com.learning.transactionmasterdata.module.rate.util.RateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RateService {

    private final RateRepository repository;
    private final CurrencyRepository currencyRepository;
    private final EntityManager entityManager;
    private final MasterDataLoggingService loggingService;

    public void create(RateRequest request){
        Optional<Currency> currencyOptional = currencyRepository.findById(request.getCurrencyId());
        if(currencyOptional.isPresent() && !repository.existsByCurrency(currencyOptional.get())){
                Rate rate = repository.save(RateUtil.mapObject(request, currencyOptional.get()));
            loggingService.create(loggingService.loggingRequest(FeatureEnum.RATE, ActionEnum.CREATE, null, rate));
        }

    }

    public void updateRateRounding(String id, RateRoundingRequest request){
        Optional<Rate> rateOptional = repository.findById(id);
        if(rateOptional.isPresent()){
            Rate oldValue = new Rate(rateOptional.get());
            Rate rate = rateOptional.get();
            BigDecimal round = RateUtil.countRateRounding(request.getBaseRounding(), rate.getBaseRate());
            BigDecimal buyRate = rate.getBaseRate().subtract(round);
            BigDecimal sellRate = rate.getBaseRate().add(round);
            rate.setRateRounding(request.getBaseRounding());
            rate.setSuggestionBuyRate(buyRate);
            rate.setSuggestionSellRate(sellRate);
            rate.setBuyRate(buyRate);
            rate.setSellRate(sellRate);
            repository.save(rate);
            loggingService.create(loggingService.loggingRequest(FeatureEnum.RATE, ActionEnum.UPDATE, oldValue, rate));
        }
    }

    public void updateTransactionRate(String id, TransactionRateRequest request) {
        Optional<Rate> rateOptional = repository.findById(id);
        if(rateOptional.isPresent()){
            Rate rate = rateOptional.get();
            Rate oldValue = new Rate(rate);
            if(request.getSellRate().compareTo(rate.getSuggestionSellRate()) <= 0 && request.getSellRate().compareTo(rate.getBaseRate()) >= 0) {
                rate.setSellRate(request.getSellRate());
            }
            if(request.getBuyRate().compareTo(rate.getSuggestionBuyRate()) >= 0 && request.getBuyRate().compareTo(rate.getBaseRate()) <= 0) {
                rate.setBuyRate(request.getBuyRate());
            }
            repository.save(rate);
            loggingService.create(loggingService.loggingRequest(FeatureEnum.RATE, ActionEnum.UPDATE, oldValue, rate));
        }
    }


    public Rate getById(String id){
        return repository.findById(id).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), null, HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    public List<Rate> getAll(){
        return repository.findAll();
    }

    public Page<Rate> getPage(int page, int size, String sortBy, String sortDir, String search){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Rate> cq = cb.createQuery(Rate.class);
        Root<Rate> root = cq.from(Rate.class);

        // Apply search filter
        if (search != null && !search.isEmpty()) {
            Join<Rate, Currency> currencyJoin = root.join("currency");
            Predicate currencyNamePredicate = cb.like(currencyJoin.get("currencyName"), "%" + search + "%");
            Predicate currencyCodePredicate = cb.like(currencyJoin.get("currencyCode"), "%" + search + "%");
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
        List<Rate> resultList = entityManager.createQuery(cq)
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
