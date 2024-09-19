package com.learning.transaction.exchange.module.transactions;

import com.learning.transaction.exchange.module.journal.JournalRequest;
import com.learning.transaction.exchange.module.journal.JournalService;
import com.learning.transaction.exchange.module.journal.JournalTypeEnum;
import com.learning.transaction.masterdata.configuration.exception.BaseException;
import com.learning.transaction.masterdata.module.currency.model.Currency;
import com.learning.transaction.masterdata.module.currency.repository.CurrencyRepository;
import com.learning.transaction.masterdata.module.rate.model.Rate;
import com.learning.transaction.masterdata.module.rate.repository.RateRepository;
import com.learning.transaction.masterdata.module.stock.model.Stock;
import com.learning.transaction.masterdata.module.stock.repository.StockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final RateRepository rateRepository;
    private final StockRepository stockRepository;
    private final CurrencyRepository currencyRepository;
    private final JournalService journalService;
    private final EntityManager entityManager;
    private final TransactionReceiptUtil receiptUtil;

    public void create(TransactionRequest request){
        Rate rate = rateRepository.findById(request.getRateId()).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), null, HttpStatus.NOT_FOUND.getReasonPhrase()));

        stockCheckAndUpdate(request, rate.getCurrency());

        Transaction transaction = new Transaction();
        transaction.setTrxCode(getTransactionCode(request.getType()));
        transaction.setCurrency(rate.getCurrency());
        transaction.setTransactionType(request.getType().name());
        transaction.setRate(rate);
        transaction.setAmount(request.getAmount());
        transaction.setTotalIdr(request.getTotalIdr());
        transaction.setCustomerName(request.getCustomerName());
        transaction.setCustomerEmail(request.getCustomerEmail());
        repository.save(transaction);

        setJournal(transaction);
        setJournalIdr(transaction);

    }

    private String getTransactionCode(TransactionTypeEnum type){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = currentDate.format(formatter);

        long count = repository.count();
        String formattedNumber = String.format("%05d", count+1);

        String firstConst = "EXC";
        String secType = type.name().equalsIgnoreCase("buy") ? "B" : "S";

        return firstConst + "-" + secType+formattedDate+formattedNumber;
    }

    public BigDecimal countAmount (Rate rate, Transaction transaction){
        BigDecimal rateValue = transaction.getTransactionType().equalsIgnoreCase("buy")
                ? rate.getBuyRate()
                : rate.getSellRate();

        return rateValue.multiply(transaction.getAmount());
    }

    public List<Transaction> getList(){
        return repository.findAll();
    }

    private void setJournal(Transaction transaction){
        JournalRequest journalRequest = new JournalRequest();
        journalRequest.setTrxCode(transaction.getTrxCode());
        journalRequest.setJournalType(transaction.getTransactionType().equalsIgnoreCase("buy") ? JournalTypeEnum.CREDIT : JournalTypeEnum.DEBIT);
        journalRequest.setAmount(transaction.getAmount());
        journalRequest.setCurrencyCode(transaction.getCurrency().getCurrencyCode());
        journalRequest.setJournalSource("Transaction");
        journalService.create(journalRequest);
    }

    private void setJournalIdr(Transaction transaction){
        JournalRequest journalRequest = new JournalRequest();
        journalRequest.setTrxCode(transaction.getTrxCode());
        journalRequest.setJournalType(transaction.getTransactionType().equalsIgnoreCase("buy") ? JournalTypeEnum.DEBIT : JournalTypeEnum.CREDIT);
        journalRequest.setAmount(transaction.getTotalIdr());
        journalRequest.setCurrencyCode("IDR");
        journalRequest.setJournalSource("Transaction");
        journalService.create(journalRequest);
    }

    public Transaction getById(String id){
        return repository.findById(id).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), null, HttpStatus.NOT_FOUND.getReasonPhrase()));
    }


    public void stockCheckAndUpdate(TransactionRequest request, Currency currency) {
        Stock stock = stockRepository.findByCurrency(currency).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), null, HttpStatus.NOT_FOUND.getReasonPhrase()));
        Stock stockIdr = stockRepository.findByCurrency(getIdr()).orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), null, HttpStatus.NOT_FOUND.getReasonPhrase()));
        if(request.getType().name().equalsIgnoreCase("buy")) {
            BigDecimal stockDelta = stock.getDynamicStock().subtract(request.getAmount());
            BigDecimal stockIdrDelta = stockIdr.getDynamicStock().add(request.getTotalIdr());
            if(stockDelta.compareTo(BigDecimal.ZERO) < 0 ) {
                throw new BaseException(HttpStatus.BAD_REQUEST.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), "Not sufficient amount");
            } else {
                stock.setDynamicStock(stockDelta);
                stockRepository.save(stock);

                stockIdr.setDynamicStock(stockIdrDelta);
                stockRepository.save(stockIdr);
            }
        }
        if(request.getType().name().equalsIgnoreCase("sell")) {
            BigDecimal stockDelta = stock.getDynamicStock().add(request.getAmount());
            stock.setDynamicStock(stockDelta);
            stockRepository.save(stock);

            BigDecimal stockIdrDelta = stockIdr.getDynamicStock().subtract(request.getTotalIdr());
            stockIdr.setDynamicStock(stockIdrDelta);
            stockRepository.save(stockIdr);
        }

    }

    private Currency getIdr(){
        return currencyRepository.findByCurrencyCode("IDR").orElseThrow(() -> new BaseException(HttpStatus.NOT_FOUND.value(), null, HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    public Page<Transaction> getPage(int page, int size, String sortBy, String sortDir, String search){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> root = cq.from(Transaction.class);

        // Apply search filter
        if (search != null && !search.isEmpty()) {
            Join<Transaction, Currency> currencyJoin = root.join("currency");
            Predicate currencyNamePredicate = cb.like(currencyJoin.get("currencyName"), "%" + search + "%");
            Predicate currencyCodePredicate = cb.like(currencyJoin.get("currencyCode"), "%" + search + "%");
            cq.where(cb.or(currencyCodePredicate, currencyNamePredicate));
        }

        // Apply sorting
        if (sortBy == null || sortBy.isEmpty()) {
           sortBy = "createdAt";
        }

        if(sortDir.equalsIgnoreCase("asc")) {
            cq.orderBy(cb.asc(root.get(sortBy)));
        } else {
            cq.orderBy(cb.desc(root.get(sortBy)));
        }

        // Create Pageable object
        Pageable pageable = PageRequest.of(page, size);

        // Get results with pagination
        List<Transaction> resultList = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Count total records for pagination purposes
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(Transaction.class)));
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultList, pageable, count);
    }

    public byte[] generateReceipt(String id) {
        Transaction transaction = getById(id);
        return createReceipt(transaction);
    }

    private byte[] createReceipt(Transaction transaction){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        Map<String, Object> receiptData = new HashMap<>();
        receiptData.put("companyName","Company Name");
        receiptData.put("companyAddress","Company Address");
        receiptData.put("companyPhoneNumber","Company Phone Number");
        receiptData.put("companyEmail","Company Email");

        receiptData.put("customerName",transaction.getCustomerName());
        receiptData.put("customerEmail",transaction.getCustomerEmail());
        receiptData.put("transactionDate",formattedDate);

        receiptData.put("currency",transaction.getCurrency().getCurrencyCode());
        receiptData.put("type",transaction.getTransactionType());
        receiptData.put("exchangeRate",transaction.getTransactionType().equalsIgnoreCase("buy") ? transaction.getRate().getBuyRate() : transaction.getRate().getSellRate());
        receiptData.put("amount",transaction.getAmount());
        receiptData.put("totalAmount",transaction.getTotalIdr());
        receiptData.put("total",transaction.getTotalIdr());

        return receiptUtil.generatePdf(receiptData);

    }
}
