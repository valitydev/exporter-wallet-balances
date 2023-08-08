package dev.vality.exporter.walletbalances.opensearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceData {

    private Wallet wallet;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Wallet {

        private String id;
        private Balance balance;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Balance {

        private String amount;
        private String currency;

    }
}
