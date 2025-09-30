package com.buckb.spring.academy.cashcard;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById() {
        CashCard cashCard = new CashCard(1L, BigDecimal.valueOf(100));
        return ResponseEntity.ok(cashCard);
    }
}
