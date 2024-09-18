package com.learning.transaction.exchange.module.journal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository repository;

    public void create(JournalRequest request){
        Journal journal = new Journal();
        journal.setTrxCode(request.getTrxCode());
        journal.setJournalType(request.getJournalType().name());
        journal.setAmount(request.getAmount());
        journal.setCurrencyCode(request.getCurrencyCode());
        repository.save(journal);
    }

    public List<Journal> getList(){
        return repository.findAll();
    }
}
