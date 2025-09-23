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

    private CashCard expectedEntity;
    private String expectedJson = """
                {
                    "id": 99,
                    "amount": 123.45
                }
            """;

    @BeforeEach
    void setup() {
        this.expectedEntity = new CashCard(99L, new BigDecimal("123.45"));
    }

    @Test
    void cashCardSerializationTest() throws Exception {
        assertThat(this.json.write(this.expectedEntity)).isStrictlyEqualToJson("expected.json");
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
    }

    @Test
    void cashCardDeserialization_shouldMatchIdAndAmount() throws IOException {
        assertThat(this.json.parse(this.expectedJson)).hasFieldOrPropertyWithValue("id", 99L);
        assertThat(this.json.parse(this.expectedJson)).hasFieldOrPropertyWithValue("amount", new BigDecimal("123.45"));
    }
}
