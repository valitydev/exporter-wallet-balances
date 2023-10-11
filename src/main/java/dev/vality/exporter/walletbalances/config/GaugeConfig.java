package dev.vality.exporter.walletbalances.config;

import dev.vality.exporter.walletbalances.model.Metric;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GaugeConfig {

    @Bean
    public MultiGauge multiGaugeWalletBalancesAmount(MeterRegistry meterRegistry) {
        return MultiGauge.builder(Metric.WALLET_BALANCES_AMOUNT.getName())
                .description(Metric.WALLET_BALANCES_AMOUNT.getDescription())
                .register(meterRegistry);
    }
}