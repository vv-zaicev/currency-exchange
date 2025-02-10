package com.zaicev.currency_exchange.controllers;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.zaicev.currency_exchange.model.Currency;
import com.zaicev.currency_exchange.repository.CurrencyRepository;

@WebMvcTest(controllers = { CurrenciesController.class })
public class TestCurrenciesController {

	@MockitoBean
	CurrencyRepository currencyRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getAllCurrencies_WithSomeCurrencies() throws Exception {
		Currency currency1 = new Currency(1L, "RUB", "Russian Ruble", "₽");
		Currency currency2 = new Currency(2L, "USD", "United States dollar", "$");
		when(currencyRepository.findAll()).thenReturn(List.of(currency1, currency2));

		mockMvc.perform(get("/currencies")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[*].code", hasItem("RUB"))).andExpect(jsonPath("$[*].code", hasItem("USD")));
	}

	@Test
	void getAllCurrencies_WithoutCurrencies() throws Exception {
		mockMvc.perform(get("/currencies")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void getCurrencyByCode_NotFound() throws Exception {
		mockMvc.perform(get("/currencies/RUB")).andExpect(status().isNotFound())
				.andExpect(status().reason("Валюта не найдена"));
	}

	@Test
	void getCurrencyByCode_Succes() throws Exception {
		Currency currency = new Currency(1L, "RUB", "Russian Ruble", "₽");
		when(currencyRepository.findByCode("RUB")).thenReturn(Optional.of(currency));

		mockMvc.perform(get("/currencies/RUB").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("Russian Ruble"))
				.andExpect(jsonPath("$.sign").value("₽")).andExpect(jsonPath("$.code").value("RUB"));
	}

	@Test
	void getCurrencyByCode_WithIncorrectCode() throws Exception {
		mockMvc.perform(get("/currencies/RUBS")).andExpect(status().isBadRequest());
	}

	@Test
	void createCurrency_WhenDuplicatedCode() throws Exception {
		when(currencyRepository.existsByCode("USD")).thenReturn(true);
		mockMvc.perform(post("/currencies").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("code", "USD")
				.param("name", "United States dollar").param("sign", "$")).andExpect(status().isConflict())
				.andExpect(status().reason("Валюта уже существует"));
	}

	@Test
	void createCurrency_Succes() throws Exception {
		when(currencyRepository.existsByCode("USD")).thenReturn(false);
		mockMvc.perform(post("/currencies").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("code", "USD")
				.param("name", "United States dollar").param("sign", "$").param("id", "1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.code").value("USD"));
	}

	@Test
	void createCurrency_WithInvalidData() throws Exception {
		when(currencyRepository.existsByCode("USD")).thenReturn(true);
		mockMvc.perform(post("/currencies").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "United States dollar").param("sign", "$")).andExpect(status().isBadRequest());
	}
}
