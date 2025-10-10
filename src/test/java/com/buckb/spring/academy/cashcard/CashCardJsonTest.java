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

    private final String expectedJson = """
                {
                    "id": 99,
                    "amount": 123.45,
                    "owner": "Kenshin"
                }
            """;
    @Autowired
    private JacksonTester<CashCard> json;

    @Autowired
    private JacksonTester<CashCard[]> jsonList;

    private CashCard[] cashCardsList;
    private CashCard expectedEntity;

    @BeforeEach
    void setup() {
        this.expectedEntity = new CashCard(99L, new BigDecimal("123.45"), "Kenshin");
        this.cashCardsList = new CashCard[] {
                new CashCard(99L, new BigDecimal("123.45"), "Kenshin"),
                new CashCard(100L, new BigDecimal("1.00"), "Pierre"),
                new CashCard(101L, new BigDecimal("150.00"), "Kenshin"),
                new CashCard(102L, new BigDecimal("200.00"), "Sarah1"),
                new CashCard(103L, new BigDecimal("37.00"), "Sarah1"),
                new CashCard(104L, new BigDecimal("5.00"), "Pierre"),
                new CashCard(105L, new BigDecimal("9.20"), "Sarah1"),
                new CashCard(106L, new BigDecimal("75.00"), "Sarah1"),
                new CashCard(107L, new BigDecimal("22.00"), "Pierre"),
                new CashCard(108L, new BigDecimal("3.00"), "Kenshin")
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

    @Test
    void cashCardListSerializationTest() throws IOException {
        assertThat(this.jsonList.write(this.cashCardsList)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void setCashCardListDeserializationTest() throws IOException {
        String expected = """
                    [
                        {"id": 99, "amount": 123.45, "owner": "Kenshin"},
                        {"id": 100, "amount": 1.00, "owner": "Pierre"},
                        {"id": 101, "amount": 150.00, "owner": "Kenshin"},
                        {"id": 102, "amount": 200.00, "owner": "Sarah1"},
                        {"id": 103, "amount": 37.00, "owner": "Sarah1"},
                        {"id": 104, "amount": 5.00, "owner": "Pierre"},
                        {"id": 105, "amount": 9.20, "owner": "Sarah1"},
                        {"id": 106, "amount": 75.00, "owner": "Sarah1"},
                        {"id": 107, "amount": 22.00, "owner": "Pierre"},
                        {"id": 108, "amount": 3.00, "owner": "Kenshin"}
                    ]
                """;

        assertThat(this.jsonList.parse(expected)).isEqualTo(this.cashCardsList);
    }
}
