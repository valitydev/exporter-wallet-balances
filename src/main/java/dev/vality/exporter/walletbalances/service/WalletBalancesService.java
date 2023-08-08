package dev.vality.exporter.walletbalances.service;

import dev.vality.exporter.walletbalances.opensearch.OpenSearchCustomClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletBalancesService {

    private final OpenSearchCustomClient openSearchCustomClient;

    public void registerMetrics() {
        var walletBalanceData = openSearchCustomClient.getWalletBalanceData();
        log.info("walletBalanceData {}", walletBalanceData);
    }
}
