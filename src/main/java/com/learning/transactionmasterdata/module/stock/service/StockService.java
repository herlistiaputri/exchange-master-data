package com.learning.transactionmasterdata.module.stock.service;

import com.learning.transactionmasterdata.configuration.exception.BaseException;
import com.learning.transactionmasterdata.module.base.auditing.ActionEnum;
import com.learning.transactionmasterdata.module.base.auditing.FeatureEnum;
import com.learning.transactionmasterdata.module.base.auditing.MasterDataLoggingService;
import com.learning.transactionmasterdata.module.currency.model.Currency;
import com.learning.transactionmasterdata.module.currency.repository.CurrencyRepository;
import com.learning.transactionmasterdata.module.stock.dto.StockRequest;
import com.learning.transactionmasterdata.module.stock.model.Stock;
import com.learning.transactionmasterdata.module.stock.repository.StockRepository;
import com.learning.transactionmasterdata.module.stock.util.StockMapper;
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

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {

    private final StockRepository repository;
    private final CurrencyRepository currencyRepository;
    private final MasterDataLoggingService loggingService;
    private final EntityManager entityManager;

    public void create(StockRequest request){
        Optional<Currency> currencyOptional = currencyRepository.findById(request.getCurrencyId());
        if(currencyOptional.isPresent()){
            if(!repository.existsByCurrency(currencyOptional.get())){
                Stock stock =  repository.save(StockMapper.mapObject(request, new Stock(), currencyOptional.get()));
                loggingService.create(loggingService.loggingRequest(FeatureEnum.STOCK, ActionEnum.CREATE, null, stock));

            }
        }
    }

    public Stock getById(String id){
        return repository.findById(id).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), null, HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    public void masterStockUpdate(String id, StockRequest request){
        Stock stock = repository.findById(id).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), null, HttpStatus.NOT_FOUND.getReasonPhrase()));
        if(stock.isEditable()) {
            Stock oldValue = new Stock(stock);
            stock.setMasterStock(request.getMasterStock());
            repository.save(stock);

            loggingService.create(loggingService.loggingRequest(FeatureEnum.STOCK, ActionEnum.UPDATE, oldValue, stock));
        }

    }

    public List<Stock> getList(){
        return repository.findAll();
    }

    public Page<Stock> getPage(int page, int size, String sortBy, String sortDir, String search){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Stock> cq = cb.createQuery(Stock.class);
        Root<Stock> root = cq.from(Stock.class);

        // Apply search filter
        if (search != null && !search.isEmpty()) {
            Join<Stock, Currency> currencyJoin = root.join("currency");
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
        List<Stock> resultList = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Count total records for pagination purposes
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(Currency.class)));
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultList, pageable, count);
    }

    public void setEditable(String id){
        Stock stock = repository.findById(id).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), null, HttpStatus.NOT_FOUND.getReasonPhrase()));
        stock.setEditable(!stock.isEditable());
        repository.save(stock);
    }
}
