package com.zaicev.currency_exchange.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.zaicev.currency_exchange.dto.Exchange;
import com.zaicev.currency_exchange.model.Currency;
import com.zaicev.currency_exchange.model.ExchangeRate;
import com.zaicev.currency_exchange.repository.CurrencyRepository;
import com.zaicev.currency_exchange.repository.ExchangeRateRepository;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/exchange")
public class ExchangeController {

	@Autowired
	ExchangeRateRepository exchangeRateRepository;

	@Autowired
	CurrencyRepository currencyRepository;

	@GetMapping()
	public Exchange getExchange(@RequestParam @Length(min = 3, max = 3) String from,
			@RequestParam @Length(min = 3, max = 3) String to, @RequestParam @NotNull BigDecimal amount) {

		Currency baseCurrency = currencyRepository.findByCode(from).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найдена валюта с кодом " + from));
		Currency targetCurrency = currencyRepository.findByCode(to).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найдена валюта с кодом " + to));

		BigDecimal rate = GetRate(from, to);

		return new Exchange(baseCurrency, targetCurrency, rate, amount, amount.multiply(rate).setScale(2, RoundingMode.DOWN));
	}

	private BigDecimal GetRate(String from, String to) {
		Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByBaseAndTargetCurrencyCode(from, to);
		if (exchangeRate.isPresent()) {
			return exchangeRate.get().getRate();
		}

		Optional<ExchangeRate> reverseExchangeRate = exchangeRateRepository.findByBaseAndTargetCurrencyCode(to, from);
		if (reverseExchangeRate.isPresent()) {
			return (new BigDecimal(1)).divide(reverseExchangeRate.get().getRate(), 6, RoundingMode.HALF_UP);
		}

		Optional<ExchangeRate> exchangeRateUsdFrom = exchangeRateRepository.findByBaseAndTargetCurrencyCode("USD",
				from);
		Optional<ExchangeRate> exchangeRateUsdTo = exchangeRateRepository.findByBaseAndTargetCurrencyCode("USD", to);
		if (exchangeRateUsdFrom.isPresent() && exchangeRateUsdTo.isPresent()) {
			return exchangeRateUsdTo.get().getRate().divide(exchangeRateUsdFrom.get().getRate(), 6, RoundingMode.HALF_UP);
		}

		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден курс обмена");
	}
}
