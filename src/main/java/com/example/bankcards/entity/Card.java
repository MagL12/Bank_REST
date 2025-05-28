package com.example.bankcards.entity;

import com.example.bankcards.config.EncryptionConfig;
import com.example.bankcards.util.EncryptionUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    @Column(nullable = false, unique = true, length = 255)
    @Convert(converter = CardNumberConverter.class)
    private String cardNumber;

    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiry date must be in MM/YY format")
    @Column(nullable = false, length = 5)
    private String expiryDate;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "\\d{3}", message = "CVV must be 3 digits")
    @Column(nullable = false, length = 3)
    private String cvv;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status = CardStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updateStatus();
    }

    @PreUpdate
    public void preUpdate() {
        updateStatus();
    }

    private void updateStatus() {
        if (status != CardStatus.BLOCKED) { // Не перезаписываем BLOCKED
            LocalDate expiry = LocalDate.parse("01/" + expiryDate, DateTimeFormatter.ofPattern("dd/MM/yy"));
            if (expiry.isBefore(LocalDate.now())) {
                status = CardStatus.EXPIRED;
            } else {
                status = CardStatus.ACTIVE;
            }
        }
    }

    @Converter
    public static class CardNumberConverter implements AttributeConverter<String, String> {

        @Override
        public String convertToDatabaseColumn(String cardNumber) {
            if (cardNumber == null) {
                return null;
            }
            try {
                return EncryptionUtil.encrypt(cardNumber);
            } catch (Exception e) {
                throw new RuntimeException("Failed to encrypt card number", e);
            }
        }

        @Override
        public String convertToEntityAttribute(String encryptedCardNumber) {
            if (encryptedCardNumber == null) {
                return null;
            }
            try {
                return EncryptionUtil.decrypt(encryptedCardNumber);
            } catch (Exception e) {
                throw new RuntimeException("Failed to decrypt card number", e);
            }
        }
    }
}