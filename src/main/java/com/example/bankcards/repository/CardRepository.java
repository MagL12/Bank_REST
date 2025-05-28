package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

    boolean existsByCardNumber(String cardNumber);

    Page<Card> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.cardNumber LIKE :numberPattern")
    Page<Card> findByUserIdAndCardNumberEndingWith(@Param("userId") UUID userId, @Param("numberPattern") String numberPattern, Pageable pageable);

    Page<Card> findByUserIdAndStatus(UUID userId, CardStatus status, Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.cardNumber LIKE :numberPattern AND c.status = :status")
    Page<Card> findByUserIdAndCardNumberEndingWithAndStatus(@Param("userId") UUID userId, @Param("numberPattern") String numberPattern, @Param("status") CardStatus status, Pageable pageable);

    Page<Card> findByStatus(CardStatus status, Pageable pageable);
}