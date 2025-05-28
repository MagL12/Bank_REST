package com.example.bankcards.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TransferRequest {
    private UUID fromCardId;
    private UUID toCardId;
    private BigDecimal amount;
}
