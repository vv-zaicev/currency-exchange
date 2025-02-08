package com.zaicev.currency_exchange.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zaicev.currency_exchange.model.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
	
	Optional<Currency> findByCode(String code);
}
