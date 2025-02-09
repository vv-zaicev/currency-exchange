package com.zaicev.currency_exchange.controllers;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.zaicev.currency_exchange.model.Currency;
import com.zaicev.currency_exchange.repository.CurrencyRepository;

@RestController
@RequestMapping("/currencies")
public class CurrenciesController {

	@Autowired
	CurrencyRepository currencyRepository;

	@GetMapping()
	public List<Currency> getAllCurrencies() {
		return currencyRepository.findAll();
	}

	@GetMapping("/{code}")
	public Currency getCurrencyByCode(@PathVariable("code") @Length(min = 3, max = 3) String code) {
		return currencyRepository.findByCode(code)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Валюта не найдена"));
	}

	@PostMapping
	public Currency createCurrency(@Validated Currency currency) {
		if (currencyRepository.existsByCode(currency.getCode())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Валюта уже существует");
		}
		currencyRepository.save(currency);
		return currency;
	}

}
