package dev.vality.exporter.walletbalances.model;

import io.micrometer.core.instrument.Tag;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomTag {

    public static final String PROVIDER_ID_TAG = "provider_id";
    public static final String PROVIDER_NAME_TAG = "provider_name";
    public static final String TERMINAL_ID_TAG = "terminal_id";
    public static final String TERMINAL_NAME_TAG = "terminal_name";
    public static final String SHOP_ID_TAG = "shop_id";
    public static final String SHOP_NAME_TAG = "shop_name";
    public static final String CURRENCY_TAG = "currency";
    public static final String COUNTRY_TAG = "issuer_country";
    public static final String BANK_TAG = "issuer_bank";
    public static final String BANK_CARD_PAYMENT_SYSTEM_TAG = "issuer_bank_card_payment_system";
    public static final String STATUS_TAG = "status";
    public static final String WALLET_ID_TAG = "wallet_id";
    public static final String WALLET_NAME_TAG = "wallet_name";

    public static Tag providerId(String providerId) {
        return Tag.of(PROVIDER_ID_TAG, providerId);
    }

    public static Tag providerName(String providerName) {
        return Tag.of(PROVIDER_NAME_TAG, providerName);
    }

    public static Tag terminalId(String terminalId) {
        return Tag.of(TERMINAL_ID_TAG, terminalId);
    }

    public static Tag terminalName(String terminalName) {
        return Tag.of(TERMINAL_NAME_TAG, terminalName);
    }

    public static Tag shopId(String shopId) {
        return Tag.of(SHOP_ID_TAG, shopId);
    }

    public static Tag shopName(String shopName) {
        return Tag.of(SHOP_NAME_TAG, shopName);
    }

    public static Tag currency(String currency) {
        return Tag.of(CURRENCY_TAG, currency);
    }

    public static Tag issuerCountry(String country) {
        return Tag.of(COUNTRY_TAG, country);
    }

    public static Tag issuerBank(String bank) {
        return Tag.of(BANK_TAG, bank);
    }

    public static Tag issuerBankCardPaymentSystem(String issuerBankCardPaymentSystem) {
        return Tag.of(BANK_CARD_PAYMENT_SYSTEM_TAG, issuerBankCardPaymentSystem);
    }

    public static Tag status(String status) {
        return Tag.of(STATUS_TAG, status);
    }

    public static Tag walletId(String walletId) {
        return Tag.of(WALLET_ID_TAG, walletId);
    }

    public static Tag walletName(String walletName) {
        return Tag.of(WALLET_NAME_TAG, walletName);
    }
}
