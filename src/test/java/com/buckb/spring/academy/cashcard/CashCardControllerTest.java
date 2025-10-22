package com.buckb.spring.academy.cashcard;

import net.minidev.json.JSONArray;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardControllerTest {

        @Autowired
        TestRestTemplate restTemplate;

        @Test
        @DisplayName("When CashCard exists, FindById should return valid data")
        void givenDataIsSaved_whenFindById_thenShouldReturnCashCard() {
                ResponseEntity<String> response = this.restTemplate
                                .withBasicAuth("Sarah1", "abc123")
                                .getForEntity("/cashcards/99", String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

                DocumentContext documentContext = JsonPath.parse(response.getBody());
                Number id = documentContext.read("$.id");
                Double amount = documentContext.read("$.amount");

                assertThat(id).isEqualTo(99);
                assertThat(amount).isEqualTo(123.45);
        }

        @Test
        @DisplayName("When CashCard doesn't exist, FindById should not return data")
        void givenDataDoesntExist_whenFindById_shouldReturn404NotFound() {
                ResponseEntity<String> response = this.restTemplate
                                .withBasicAuth("Sarah1", "abc123")
                                .getForEntity("/cashcards/1", String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                assertThat(response.getBody()).isBlank();
        }

        @Test
        @DirtiesContext
        @DisplayName("When creating new valid CashCard, it should return 201_CREATED")
        void givenValidCashCard_whenCreate_thenShouldReturn201Created() {
                CashCard newCashCard = new CashCard(null, new BigDecimal("55.55"), "Sarah1");
                ResponseEntity<Void> response = this.restTemplate
                                .withBasicAuth("Sarah1", "abc123")
                                .postForEntity("/cashcards", newCashCard, Void.class);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

                var savedCashCard = this.restTemplate
                                .withBasicAuth("Sarah1", "abc123")
                                .getForEntity(response.getHeaders().getLocation(), String.class);

                assertThat(savedCashCard.getStatusCode()).isEqualTo(HttpStatus.OK);

                DocumentContext documentContext = JsonPath.parse(savedCashCard.getBody());
                Number id = documentContext.read("$.id");
                Double amount = documentContext.read("$.amount");
                String owner = documentContext.read("$.owner");

                assertThat(id).isNotNull();
                assertThat(amount).isEqualTo(newCashCard.amount().doubleValue());
                assertThat(owner).isEqualTo(newCashCard.owner());
        }

        @Test
        @DisplayName("FindAll should return a list containing all CashCards from a given owner, if any.")
        void givenCashCardsExists_whenFindAll_thenShouldReturnListOfCashCards() {
                ResponseEntity<String> response = this.restTemplate
                                .withBasicAuth("Sarah1", "abc123")
                                .getForEntity("/cashcards", String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                DocumentContext context = JsonPath.parse(response.getBody());
                int listSize = context.read("$.length()");
                assertThat(listSize).isEqualTo(5);
        }

        @Test
        @DisplayName("FindAll response should contain valid data")
        void givenCashCardsExists_WhenFindAll_thenShouldReturnValidData() {
                ResponseEntity<String> response = this.restTemplate
                                .withBasicAuth("Sarah1", "abc123")
                                .getForEntity("/cashcards", String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

                DocumentContext context = JsonPath.parse(response.getBody());
                JSONArray ids = context.read("$..id");
                JSONArray amounts = context.read("$..amount");
                assertThat(ids).containsExactlyInAnyOrder(99, 102, 103, 105, 106);
                assertThat(amounts).containsExactlyInAnyOrder(123.45, 200.00, 37, 9.20, 75);
        }

        @Test
        @DisplayName("FindAll should return paginated response")
        void givenCashCardsExists_WhenFindAll_thenResponseShouldBePaginated() {
                ResponseEntity<String> response = this.restTemplate
                                .withBasicAuth("Sarah1", "abc123")
                                .getForEntity("/cashcards?page=0&size=2", String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

                DocumentContext context = JsonPath.parse(response.getBody());
                JSONArray page = context.read("$[*]");
                assertThat(page.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("FindAll paginated with sorting should return sorted response")
        void givenCashCardsExists_WhenFindAll_withSortOrder_thenResponseShouldBePaginatedAndSorted() {
                ResponseEntity<String> response = this.restTemplate
                                .withBasicAuth("Sarah1", "abc123")
                                .getForEntity("/cashcards?page=0&size=2&sort=amount,desc", String.class);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

                DocumentContext context = JsonPath.parse(response.getBody());
                JSONArray page = context.read("$[*]");
                assertThat(page.size()).isEqualTo(2);
                Number actualAmount = context.read("$[0].amount");
                assertThat(actualAmount).isEqualTo(200.00);
        }

        @Test
        @DisplayName("When providing bad credentials, should return 401_UNAUTHORIZED")
        void givenBadCredentials_whenFindAll_thenShouldReturn401Unauthorized() {
                ResponseEntity<String> testInvalidUser = this.restTemplate
                                .withBasicAuth("invalidUser", "abc123")
                                .getForEntity("/cashcards", String.class);
                assertThat(testInvalidUser.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

                ResponseEntity<String> testInvalidPassword = this.restTemplate
                                .withBasicAuth("Sarah1", "wrongPassword")
                                .getForEntity("/cashcards", String.class);
                assertThat(testInvalidPassword.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
}
