package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // Пользовательские операции
    @GetMapping("/api/cards")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Page<CardDTO>> getUserCards(
            Authentication authentication,
            @RequestParam(required = false) String cardNumber,
            @RequestParam(required = false) CardStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CardDTO> cards = cardService.getUserCards(authentication.getName(), cardNumber, status, page, size);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/api/cards/{id}/block")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CardDTO> blockCard(@PathVariable UUID id, Authentication authentication) {
        try {
            CardDTO blocked = cardService.blockCard(id, authentication.getName());
            return ResponseEntity.ok(blocked);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Card not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            if (e.getMessage().equals("Unauthorized to block this card")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            throw e;
        }
    }

    // Админские операции
    @GetMapping("/api/admin/cards")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<CardDTO>> getAllCards(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) CardStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CardDTO> cards = cardService.getAllCards(username, status, page, size);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/api/admin/cards")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CardDTO> createCardAsAdmin(@Valid @RequestBody CreateCardRequest request, @RequestParam String username) {
        try {
            CardDTO created = cardService.createCardForUser(request, username);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Card with this number already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
            if (e.getMessage().equals("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            throw e;
        }
    }

    @PutMapping("/api/admin/cards/{id}/activate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CardDTO> activateCard(@PathVariable UUID id) {
        try {
            CardDTO activated = cardService.activateCard(id);
            return ResponseEntity.ok(activated);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Card not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            if (e.getMessage().equals("Cannot activate expired card")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            throw e;
        }
    }

    @PutMapping("/api/admin/cards/{id}/block")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CardDTO> blockCardAsAdmin(@PathVariable UUID id) {
        try {
            CardDTO blocked = cardService.blockCardAsAdmin(id);
            return ResponseEntity.ok(blocked);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Card not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            throw e;
        }
    }

    @DeleteMapping("/api/admin/cards/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID id) {
        try {
            cardService.deleteCard(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            throw e;
        }
    }
}