package com.buckb.spring.academy.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnCashCardWhenDataIsSaved() {
        ResponseEntity<String> response = this.restTemplate.getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertThat(id).isEqualTo(99);
        assertThat(amount).isEqualTo(123.45);
    }

    @Test
    void shouldReturn404WhenDataIsNotFound() {
        ResponseEntity<String> response = this.restTemplate.getForEntity("/cashcards/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    void shouldReturn201WhenDataIsCreated() {
        CashCard newCashCard = new CashCard(null, new BigDecimal("55.55"));
        ResponseEntity<Void> response = this.restTemplate.postForEntity("/cashcards", newCashCard, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var savedCashCard = this.restTemplate.getForEntity(response.getHeaders().getLocation(), String.class);
        assertThat(savedCashCard.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(savedCashCard.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");
        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(55.55);
    }

    @Test
    void shouldReturnCorrectListOfCashCards() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
