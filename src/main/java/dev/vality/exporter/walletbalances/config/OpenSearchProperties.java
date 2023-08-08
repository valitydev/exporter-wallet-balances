package dev.vality.exporter.walletbalances.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "opensearch")
public class OpenSearchProperties {

    private String endpoint;
    private String service;
    private String region;

}
