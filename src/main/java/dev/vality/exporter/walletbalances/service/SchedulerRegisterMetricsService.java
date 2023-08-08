package dev.vality.exporter.walletbalances.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerRegisterMetricsService {

    private final MetricsService metricsService;

    @Scheduled(cron = "${exporter-wallet-balances.cron:-}")
    public void registerMetricsTask() {
        log.debug("Start of registration of wallet balances in prometheus");
        metricsService.registerMetrics();
        log.debug("Finished of registration of wallet balances in prometheus");
    }
}
