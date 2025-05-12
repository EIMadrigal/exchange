package com.example.exchange.assets;

import com.example.exchange.enums.AssetEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssetServiceTest {

    final Long USER_SYSTEM = 1L;

    final Long USER_A = 101L;

    final Long USER_B = 102L;

    final Long USER_C = 103L;

    AssetService assetService;

    @BeforeEach
    void setUp() {
        assetService = new AssetService();
        init();
    }

    @AfterEach
    void tearDown() {
        verify();
    }

    @Test
    void tryTransfer() {
        assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, USER_A, USER_B, AssetEnum.USD,
                new BigDecimal(12000), true);
        assertBDEquals(300, assetService.getAsset(USER_A, AssetEnum.USD).available);
        assertBDEquals(45600 + 12000, assetService.getAsset(USER_B, AssetEnum.USD).available);
    }

    /**
     * A: USD=12300, BTC=12
     * B: USD=45600
     * C: BTC=34
     */
    void init() {
        assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, USER_SYSTEM, USER_A, AssetEnum.USD,
                BigDecimal.valueOf(12300), false);
        assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, USER_SYSTEM, USER_A, AssetEnum.BTC,
                BigDecimal.valueOf(12), false);

        assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, USER_SYSTEM, USER_B, AssetEnum.USD,
                BigDecimal.valueOf(45600), false);
        assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, USER_SYSTEM, USER_C, AssetEnum.BTC,
                BigDecimal.valueOf(34), false);

        assertBDEquals(-57900, assetService.getAsset(USER_SYSTEM, AssetEnum.USD).available);
        assertBDEquals(-46, assetService.getAsset(USER_SYSTEM, AssetEnum.BTC).available);
    }

    void verify() {
        BigDecimal totalUSD = BigDecimal.ZERO;
        BigDecimal totalBTC = BigDecimal.ZERO;
        for (Long userId : assetService.userAssets.keySet()) {
            var assetUSD = assetService.getAsset(userId, AssetEnum.USD);
            if (assetUSD != null) {
                totalUSD = totalUSD.add(assetUSD.available).add(assetUSD.frozen);
            }
            var assetBTC = assetService.getAsset(userId, AssetEnum.BTC);
            if (assetBTC != null) {
                totalBTC = totalBTC.add(assetBTC.available).add(assetBTC.frozen);
            }
        }
        assertBDEquals(0, totalUSD);
        assertBDEquals(0, totalBTC);
    }

    void assertBDEquals(long expected, BigDecimal actual) {
        assertTrue(new BigDecimal(expected).compareTo(actual) == 0);
    }

}
