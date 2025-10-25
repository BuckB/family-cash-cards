package com.buckb.spring.academy.cashcard;

import java.security.Principal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashCard> findById(@PathVariable Long id, Principal principal) {
        Optional<CashCard> response = this.cashCardRepository
                .findByIdAndOwner(id, principal.getName());
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CashCard newCashCard,
            UriComponentsBuilder uriBuilder, Principal principal) {
        CashCard cardToSave = new CashCard(null, newCashCard.amount(), principal.getName());
        CashCard savedCashCard = this.cashCardRepository.save(cardToSave);

        var location = uriBuilder.path("/cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<Iterable<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = this.cashCardRepository.findByOwner(
                principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSort()));

        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CashCard updatedCashCard,
            Principal principal) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }

        if (updatedCashCard.id() != null && !updatedCashCard.id().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<CashCard> existingCard = this.cashCardRepository.findByIdAndOwner(id, principal.getName());
        if (existingCard.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CashCard cardToSave = new CashCard(
                id,
                updatedCashCard.amount(),
                principal.getName());
        this.cashCardRepository.save(cardToSave);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        Optional<CashCard> existingCard = this.cashCardRepository.findByIdAndOwner(id, principal.getName());
        if (existingCard.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        this.cashCardRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
