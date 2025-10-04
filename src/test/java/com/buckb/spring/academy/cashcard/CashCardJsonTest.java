package com.buckb.spring.academy.cashcard;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CashCardJsonTest {

    @Autowired
    private JacksonTester<CashCard> json;

    @Autowired
    private JacksonTester<CashCard[]> jsonList;

    private CashCard[] cashCardsList;
    private CashCard expectedEntity;
    private final String expectedJson = """
                {
                    "id": 99,
                    "amount": 123.45
                }
            """;

    @BeforeEach
    void setup() {
        this.expectedEntity = new CashCard(99L, new BigDecimal("123.45"));
        this.cashCardsList = new CashCard[] {
                new CashCard(99L, new BigDecimal("123.45")),
                new CashCard(100L, new BigDecimal("1.00")),
                new CashCard(101L, new BigDecimal("150.00")),
                new CashCard(102L, new BigDecimal("200.00")),
                new CashCard(103L, new BigDecimal("37.00")),
                new CashCard(104L, new BigDecimal("5.00")),
                new CashCard(105L, new BigDecimal("9.20")),
                new CashCard(106L, new BigDecimal("75.00")),
                new CashCard(107L, new BigDecimal("22.00")),
                new CashCard(108L, new BigDecimal("3.00"))
        };
    }

    @Test
    void cashCardSerializationTest() throws IOException {
        CashCard cashCard = this.cashCardsList[0];
        assertThat(this.json.write(cashCard)).isStrictlyEqualToJson("single.json");
        assertThat(this.json.write(cashCard)).hasJsonPathNumberValue("@.id");
        assertThat(this.json.write(cashCard)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(99);
        assertThat(this.json.write(cashCard)).hasJsonPathNumberValue("@.amount");
        assertThat(this.json.write(cashCard)).extractingJsonPathNumberValue("@.amount")
                .isEqualTo(123.45);
    }

    @Test
    void cashCardSerialization_shouldContainIdAndAmount() throws Exception {
        assertThat(this.json.write(this.expectedEntity)).hasJsonPathNumberValue("@.id");
        assertThat(this.json.write(this.expectedEntity)).extractingJsonPathNumberValue("@.id").isEqualTo(99);
        assertThat(this.json.write(this.expectedEntity)).hasJsonPathNumberValue("@.amount");
        assertThat(this.json.write(this.expectedEntity)).extractingJsonPathNumberValue("@.amount").isEqualTo(123.45);
    }

    @Test
    void cashCardDeserializationTest() throws IOException {
        assertThat(this.json.parse(this.expectedJson)).isEqualTo(this.expectedEntity);
        assertThat(this.json.parseObject(this.expectedJson).id()).isEqualTo(99);
        assertThat(this.json.parseObject(this.expectedJson).amount().doubleValue()).isEqualTo(123.45);
    }

    @Test
    void cashCardDeserialization_shouldMatchIdAndAmount() throws IOException {
        assertThat(this.json.parse(this.expectedJson)).hasFieldOrPropertyWithValue("id", 99L);
        assertThat(this.json.parse(this.expectedJson)).hasFieldOrPropertyWithValue("amount", new BigDecimal("123.45"));
    }
}
