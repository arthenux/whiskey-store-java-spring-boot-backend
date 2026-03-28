package com.alan.whiskey_store_java_spring_boot_backend.service;

import com.alan.whiskey_store_java_spring_boot_backend.dto.PaymentCardRequest;
import com.alan.whiskey_store_java_spring_boot_backend.dto.StoreInfoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class StoreInfoService {

    public static final String SAMPLE_CARD_HOLDER = "Whiskey Store Tester";
    public static final String SAMPLE_CARD_NUMBER = "4242 4242 4242 4242";
    public static final String SAMPLE_CARD_EXPIRY_MONTH = "12";
    public static final String SAMPLE_CARD_EXPIRY_YEAR = "2030";
    public static final String SAMPLE_CARD_CVV = "123";

    public StoreInfoResponse getStoreInfo() {
        return new StoreInfoResponse(
                "Whiskey Store",
                SAMPLE_CARD_HOLDER,
                SAMPLE_CARD_NUMBER,
                SAMPLE_CARD_EXPIRY_MONTH,
                SAMPLE_CARD_EXPIRY_YEAR,
                SAMPLE_CARD_CVV,
                "GBP"
        );
    }

    public void validateTestCard(PaymentCardRequest payment) {
        boolean matches = Objects.equals(payment.cardHolderName().trim(), SAMPLE_CARD_HOLDER)
                && normalizeNumber(payment.cardNumber()).equals(normalizeNumber(SAMPLE_CARD_NUMBER))
                && Objects.equals(payment.expiryMonth().trim(), SAMPLE_CARD_EXPIRY_MONTH)
                && Objects.equals(payment.expiryYear().trim(), SAMPLE_CARD_EXPIRY_YEAR)
                && Objects.equals(payment.cvv().trim(), SAMPLE_CARD_CVV);
        if (!matches) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Use the provided test card details to place an order in this demo."
            );
        }
    }

    private String normalizeNumber(String value) {
        return value.replaceAll("\\s+", "");
    }
}
