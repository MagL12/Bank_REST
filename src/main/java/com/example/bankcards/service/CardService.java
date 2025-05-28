package com.example.bankcards.service;

import com.example.bankcards.config.EncryptionConfig;
import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.EncryptionUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EncryptionConfig encryptionConfig;

    @Transactional
    public CardDTO createCardForUser(CreateCardRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (cardRepository.existsByCardNumber(request.getCardNumber())) {
            throw new RuntimeException("Card with this number already exists");
        }

        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setCardNumber(request.getCardNumber());
        card.setExpiryDate(request.getExpiryDate());
        card.setCvv(request.getCvv());
        card.setBalance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO);
        card.setUser(user);

        Card savedCard = cardRepository.save(card);

        return toCardDTO(savedCard);
    }

    @Transactional
    public CardDTO blockCard(UUID cardId, String username) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        if (!card.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to block this card");
        }
        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);

        return toCardDTO(savedCard);
    }

    @Transactional
    public CardDTO activateCard(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new RuntimeException("Cannot activate expired card");
        }
        card.setStatus(CardStatus.ACTIVE);
        Card savedCard = cardRepository.save(card);

        return toCardDTO(savedCard);
    }

    @Transactional
    public CardDTO blockCardAsAdmin(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);

        return toCardDTO(savedCard);
    }

    @Transactional
    public void deleteCard(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        cardRepository.delete(card);
    }

    @Transactional(readOnly = true)
    public Page<CardDTO> getUserCards(String username, String cardNumber, CardStatus status, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cards;

        if (cardNumber != null && !cardNumber.isBlank()) {
            String maskedSearch = cardNumber.replaceAll("[^0-9]", "");
            if (maskedSearch.length() >= 4) {
                maskedSearch = maskedSearch.substring(maskedSearch.length() - 4);
            }
            final String searchPattern = "%" + maskedSearch;
            if (status != null) {
                cards = cardRepository.findByUserIdAndCardNumberEndingWithAndStatus(user.getId(), searchPattern, status, pageable);
            } else {
                cards = cardRepository.findByUserIdAndCardNumberEndingWith(user.getId(), searchPattern, pageable);
            }
        } else if (status != null) {
            cards = cardRepository.findByUserIdAndStatus(user.getId(), status, pageable);
        } else {
            cards = cardRepository.findByUserId(user.getId(), pageable);
        }

        return cards.map(this::toCardDTO);
    }

    @Transactional(readOnly = true)
    public Page<CardDTO> getAllCards(String username, CardStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cards;

        if (username != null && !username.isBlank()) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (status != null) {
                cards = cardRepository.findByUserIdAndStatus(user.getId(), status, pageable);
            } else {
                cards = cardRepository.findByUserId(user.getId(), pageable);
            }
        } else if (status != null) {
            cards = cardRepository.findByStatus(status, pageable);
        } else {
            cards = cardRepository.findAll(pageable);
        }

        return cards.map(this::toCardDTO);
    }

    private CardDTO toCardDTO(Card card) {
        CardDTO result = new CardDTO();
        result.setId(card.getId());
        result.setCardNumber(maskCardNumber(card.getCardNumber()));
        result.setExpiryDate(card.getExpiryDate());
        result.setBalance(card.getBalance());
        result.setUserId(card.getUser().getId());
        result.setStatus(card.getStatus());
        result.setCreatedAt(card.getCreatedAt());
        return result;
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 16) {
            return cardNumber;
        }
        return "**** **** **** " + cardNumber.substring(12);
    }
}