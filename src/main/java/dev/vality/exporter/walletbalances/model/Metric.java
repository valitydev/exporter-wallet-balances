package dev.vality.exporter.walletbalances.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Metric {

    WALLET_BALANCES_AMOUNT(
            formatWithPrefix("wallet_balances_amount"),
            "Wallet balances amount since last scrape");

    @Getter
    private final String name;
    @Getter
    private final String description;

    private static String formatWithPrefix(String name) {
        return String.format("ewb_%s", name);
    }
}
