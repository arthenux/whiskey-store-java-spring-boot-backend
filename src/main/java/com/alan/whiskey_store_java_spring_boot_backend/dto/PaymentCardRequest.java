package com.alan.whiskey_store_java_spring_boot_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaymentCardRequest(
        @NotBlank @Size(max = 160) String cardHolderName,
        @NotBlank @Size(max = 30) String cardNumber,
        @NotBlank @Size(max = 2) String expiryMonth,
        @NotBlank @Size(max = 4) String expiryYear,
        @NotBlank @Size(max = 4) String cvv
) {
}
