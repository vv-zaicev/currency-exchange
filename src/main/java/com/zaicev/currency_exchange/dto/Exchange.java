package com.zaicev.currency_exchange.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.zaicev.currency_exchange.model.Currency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonPropertyOrder({"baseCurrency", "targetCurrency", "rate", "amount", "convertedAmount"})
public class Exchange {
	private Currency baseCurrency;
	private Currency targetCurrency;
	private BigDecimal rate;
	private BigDecimal amount;
	private BigDecimal convertedAmount;
}
