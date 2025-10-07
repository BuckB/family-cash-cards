package com.buckb.spring.academy.cashcard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashCardRepository extends CrudRepository<CashCard, Long> {

}
