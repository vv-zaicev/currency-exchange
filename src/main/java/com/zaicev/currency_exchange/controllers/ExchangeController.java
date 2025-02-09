package com.zaicev.currency_exchange.controllers;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.zaicev.currency_exchange.dto.Exchange;
import com.zaicev.currency_exchange.model.ExchangeRate;
import com.zaicev.currency_exchange.repository.ExchangeRateRepository;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/exchange")
public class ExchangeController {

	@Autowired
	ExchangeRateRepository exchangeRateRepository;

	@GetMapping()
	public Exchange getExchange(@RequestParam @Length(min = 3, max = 3) String from,
			@RequestParam @Length(min = 3, max = 3) String to, @RequestParam @NotNull BigDecimal amount) {
		ExchangeRate exchangeRate = exchangeRateRepository.findByBaseAndTargetCurrencyCode(from, to)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		return new Exchange(exchangeRate.getBaseCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate(),
				amount, amount.multiply(exchangeRate.getRate()));
	}
}
