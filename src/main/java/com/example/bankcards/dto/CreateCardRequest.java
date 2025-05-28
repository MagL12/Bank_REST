package com.example.bankcards.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.math.BigDecimal;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = LuhnValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
@interface LuhnCheck {
    String message() default "Invalid card number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Getter
@Setter
public class CreateCardRequest {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    @LuhnCheck
    String cardNumber;

    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiry date must be in MM/YY format")
    String expiryDate;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "\\d{3}", message = "CVV must be 3 digits")
    String cvv;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    BigDecimal balance;
}