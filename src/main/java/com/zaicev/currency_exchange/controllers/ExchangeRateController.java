package com.zaicev.currency_exchange.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.zaicev.currency_exchange.model.Currency;
import com.zaicev.currency_exchange.model.ExchangeRate;
import com.zaicev.currency_exchange.repository.CurrencyRepository;
import com.zaicev.currency_exchange.repository.ExchangeRateRepository;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/exchangeRates")
public class ExchangeRateController {

	@Autowired
	ExchangeRateRepository exchangeRateRepository;
	@Autowired
	CurrencyRepository currencyRepository;

	@GetMapping()
	public List<ExchangeRate> returanAllExchangeRates() {
		return exchangeRateRepository.findAll();
	}

	@GetMapping("/{codes}")
	public ExchangeRate returnExchangeRateByCodes(@PathVariable("codes") @Length(min = 6, max = 6) String codes) {
		String baseCode = codes.substring(0, 3);
		String targetCode = codes.substring(3);
		return exchangeRateRepository.findByBaseAndTargetCurrencyCode(baseCode, targetCode)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@PostMapping()
	public ExchangeRate creatExchangeRate(@RequestParam @Length(min = 3, max = 3) String baseCurrencyCode,
			@RequestParam @Length(min = 3, max = 3) String targetCurrencyCode, @RequestParam @NotNull BigDecimal rate) {
		Currency baseCurrency = currencyRepository.findByCode(baseCurrencyCode)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		Currency targetCurrency = currencyRepository.findByCode(targetCurrencyCode)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		ExchangeRate exchangeRate = ExchangeRate.builder().baseCurrency(baseCurrency).targetCurrency(targetCurrency)
				.rate(rate).build();

		if (exchangeRateRepository.findByBaseAndTargetCurrencyCode(baseCurrencyCode, targetCurrencyCode).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}

		exchangeRateRepository.save(exchangeRate);

		return exchangeRate;
	}

	@PatchMapping("/{codes}")
	public ExchangeRate updateExchangeRate(@PathVariable("codes") @Length(min = 6, max = 6) String codes,
			@RequestParam @NotNull BigDecimal rate) {
		String baseCode = codes.substring(0, 3);
		String targetCode = codes.substring(3);

		ExchangeRate exchangeRate = exchangeRateRepository.findByBaseAndTargetCurrencyCode(baseCode, targetCode)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		exchangeRate.setRate(rate);
		
		exchangeRateRepository.save(exchangeRate);
		
		return exchangeRate;
	}

}
