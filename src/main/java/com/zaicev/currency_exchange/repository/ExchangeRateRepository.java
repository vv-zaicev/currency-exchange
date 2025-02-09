package com.zaicev.currency_exchange.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zaicev.currency_exchange.model.ExchangeRate;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
	@Query("SELECT er FROM ExchangeRate er WHERE er.baseCurrency.code = :baseCurrencyCode AND er.targetCurrency.code = :targetCurrencyCode")
	Optional<ExchangeRate> findByBaseAndTargetCurrencyCode(@Param("baseCurrencyCode") String baseCurrencyCode,
			@Param("targetCurrencyCode") String targetCurrencyCode);

}
