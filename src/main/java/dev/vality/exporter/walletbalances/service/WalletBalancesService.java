package dev.vality.exporter.walletbalances.service;

import dev.vality.exporter.walletbalances.model.CustomTag;
import dev.vality.exporter.walletbalances.model.Metric;
import dev.vality.exporter.walletbalances.model.WalletBalanceData;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("LineLength")
public class WalletBalancesService {

    private final OpenSearchService openSearchService;
    private final MultiGauge multiGaugeWalletBalancesAmount;
    private final MeterRegistryService meterRegistryService;

    public void registerMetrics() {
        var walletBalanceData = openSearchService.getWalletBalanceDataByInterval();
        var rows = walletBalanceData.stream()
                .map(dto -> {
                    final var amount = Double.parseDouble(dto.getWallet().getBalance().getAmount());
                    return MultiGauge.Row.of(getTags(dto), this, o -> amount);
                })
                .collect(Collectors.<MultiGauge.Row<?>>toList());
        multiGaugeWalletBalancesAmount.register(rows, true);
        var registeredMetricsSize = meterRegistryService.getRegisteredMetricsSize(Metric.WALLET_BALANCES_AMOUNT.getName());
        log.info("Payments with final statuses metrics have been registered to 'prometheus', " +
                "registeredMetricsSize = {}, clientSize = {}", registeredMetricsSize, walletBalanceData.size());
    }

    private Tags getTags(WalletBalanceData dto) {
        return Tags.of(
                CustomTag.walletId(dto.getWallet().getId()),
                CustomTag.currency(dto.getWallet().getBalance().getCurrency()));
    }
}
