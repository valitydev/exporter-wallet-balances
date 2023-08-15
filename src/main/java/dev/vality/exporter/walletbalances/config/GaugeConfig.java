package dev.vality.exporter.walletbalances.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class GaugeConfig {

    @Bean
    public Map<String, Double> walletBalancesAggregatesMap() {
        return new ConcurrentHashMap<>();
    }
}
