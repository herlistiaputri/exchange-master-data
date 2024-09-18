package com.learning.transaction.exchange.module.journal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalRepository extends JpaRepository<Journal, String> {
}
