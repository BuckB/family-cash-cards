package com.buckb.spring.academy.cashcard;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

public record CashCard(@Id Long id, BigDecimal amount) {
}
