package dev.vality.exporter.walletbalances.config;

import org.apache.http.HttpHost;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

@Configuration
public class OpenSearchClientConfig {

    @Bean(destroyMethod = "close")
    public SdkHttpClient httpClient() {
        return ApacheHttpClient.builder().build();
    }

    @Bean
    public OpenSearchClient openSearchClient(OpenSearchProperties openSearchProperties, SdkHttpClient httpClient) {
        return new OpenSearchClient(new AwsSdk2Transport(
                httpClient,
                HttpHost.create(openSearchProperties.getEndpoint()).getHostName(),
                openSearchProperties.getService(),
                Region.of(openSearchProperties.getRegion()),
                AwsSdk2TransportOptions.builder().build()));
    }
}
