package com.alan.whiskey_store_java_spring_boot_backend.controller;

import com.alan.whiskey_store_java_spring_boot_backend.dto.StoreInfoResponse;
import com.alan.whiskey_store_java_spring_boot_backend.service.StoreInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store-info")
public class StoreInfoController {

    private final StoreInfoService storeInfoService;

    public StoreInfoController(StoreInfoService storeInfoService) {
        this.storeInfoService = storeInfoService;
    }

    @GetMapping
    public StoreInfoResponse getStoreInfo() {
        return storeInfoService.getStoreInfo();
    }
}
