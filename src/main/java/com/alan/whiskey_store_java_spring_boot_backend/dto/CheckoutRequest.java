package com.alan.whiskey_store_java_spring_boot_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CheckoutRequest(
        @NotBlank @Size(max = 160) String shippingName,
        @NotBlank @Email @Size(max = 180) String shippingEmail,
        @NotBlank @Size(max = 180) String shippingAddressLine1,
        @Size(max = 180) String shippingAddressLine2,
        @NotBlank @Size(max = 120) String shippingCity,
        @NotBlank @Size(max = 40) String shippingPostcode,
        @NotBlank @Size(max = 80) String shippingCountry,
        @Valid @NotNull PaymentCardRequest payment
) {
}
