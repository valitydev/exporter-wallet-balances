package dev.vality.exporter.walletbalances.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final WalletBalancesService walletBalancesService;

    public void registerMetrics() {
        walletBalancesService.registerMetrics();
    }
}
