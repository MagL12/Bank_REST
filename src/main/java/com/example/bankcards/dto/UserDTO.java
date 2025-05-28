package com.example.bankcards.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String username;
    private String role;
    private boolean enabled;
}