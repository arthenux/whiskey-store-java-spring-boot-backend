package com.alan.whiskey_store_java_spring_boot_backend.dto;

public record StoreInfoResponse(
        String storeName,
        String sampleCardHolderName,
        String sampleCardNumber,
        String sampleCardExpiryMonth,
        String sampleCardExpiryYear,
        String sampleCardCvv,
        String currency
) {
}
