package dev.vality.exporter.walletbalances.service;

import dev.vality.exporter.walletbalances.model.CustomTag;
import dev.vality.exporter.walletbalances.model.Metric;
import dev.vality.exporter.walletbalances.model.WalletBalanceData;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("LineLength")
public class WalletBalancesService {

    private final OpenSearchService openSearchService;
    private final MeterRegistryService meterRegistryService;
    private final Map<String, Double> walletBalancesAggregatesMap;

    public void registerMetrics() {
        var walletsBalancesDataByInterval = openSearchService.getWalletsBalancesDataByInterval();
        for (WalletBalanceData walletBalanceData : walletsBalancesDataByInterval) {
            var id = walletBalanceData.getWallet().getId();
            if (!walletBalancesAggregatesMap.containsKey(id)) {
                var gauge = Gauge.builder(
                                Metric.WALLET_BALANCES_AMOUNT.getName(),
                                walletBalancesAggregatesMap,
                                map -> map.get(id))
                        .description(Metric.WALLET_BALANCES_AMOUNT.getDescription())
                        .tags(getTags(walletBalanceData));
                meterRegistryService.registry(gauge);
            }
            walletBalancesAggregatesMap.put(id, Double.parseDouble(walletBalanceData.getWallet().getBalance().getAmount()));
        }
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
