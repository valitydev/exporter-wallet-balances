package dev.vality.exporter.walletbalances.service;

import dev.vality.exporter.walletbalances.model.CustomTag;
import dev.vality.exporter.walletbalances.model.Metric;
import dev.vality.exporter.walletbalances.model.WalletBalanceData;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("LineLength")
public class WalletBalancesService {

    private final OpenSearchService openSearchService;
    private final MeterRegistryService meterRegistryService;

    public void registerMetrics() {
        var walletsBalancesDataByInterval = openSearchService.getWalletsBalancesDataByInterval();
        var objectObjectHashMap = new HashMap<String, WalletBalanceData>();
        for (WalletBalanceData walletBalanceData : walletsBalancesDataByInterval) {
            log.info("walletBalanceData {}", walletBalanceData);
            objectObjectHashMap.put(walletBalanceData.getWallet().getId(), walletBalanceData);
        }
        objectObjectHashMap.values().forEach(
                dto -> {
                    log.info("walletBalanceData {}", dto);
                    final var amount = Double.parseDouble(dto.getWallet().getBalance().getAmount());
                    var gauge = Gauge.builder(Metric.WALLET_BALANCES_AMOUNT.getName(), this, o -> amount)
                            .description(Metric.WALLET_BALANCES_AMOUNT.getDescription())
                            .tags(getTags(dto));
                    meterRegistryService.registry(gauge);
                });
        var registeredMetricsSize = meterRegistryService.getRegisteredMetricsSize(Metric.WALLET_BALANCES_AMOUNT.getName());
        log.info("Payments with final statuses metrics have been registered to 'prometheus', " +
                "registeredMetricsSize = {}, clientSize = {}", registeredMetricsSize, walletsBalancesDataByInterval.size());
    }

    private Tags getTags(WalletBalanceData dto) {
        return Tags.of(
                CustomTag.walletId(dto.getWallet().getId()),
                CustomTag.currency(dto.getWallet().getBalance().getCurrency()));
    }
}
