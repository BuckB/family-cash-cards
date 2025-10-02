package com.buckb.spring.academy.cashcard;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnCashCardWhenDataIsSaved() {
        ResponseEntity<CashCard> response = this.restTemplate.getForEntity("/cashcards/99", CashCard.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(99L);

        BigDecimal expectedAmount = new BigDecimal("123.45");
        assertThat(response.getBody().amount()).isEqualByComparingTo(expectedAmount);
    }

    @Test
    void shouldReturn404WhenDataIsNotFound() {
        ResponseEntity<CashCard> response = this.restTemplate.getForEntity("/cashcards/1", CashCard.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

}
