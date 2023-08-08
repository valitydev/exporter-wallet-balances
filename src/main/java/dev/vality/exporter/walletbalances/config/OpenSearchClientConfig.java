package dev.vality.exporter.walletbalances.config;

import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
@SuppressWarnings("LineLength")
public class OpenSearchClientConfig {

    @Bean(destroyMethod = "close")
    public RestClient restClient(OpenSearchProperties openSearchProperties) {
        final var credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(openSearchProperties.getUsername(), openSearchProperties.getPassword()));
        var httpHost = new HttpHost(openSearchProperties.getHostname(), openSearchProperties.getPort(), "https");
        return RestClient.builder(httpHost)
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)).build();
    }

    @Bean
    public OpenSearchClient openSearchClient(RestClient restClient) {
        var transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new OpenSearchClient(transport);
    }

    @SneakyThrows
    private SSLContext sslContext(KeyStore keyStore, String password) {
        return new SSLContextBuilder()
                .loadTrustMaterial(keyStore, (x509Certificates, s) -> true)
                .loadKeyMaterial(keyStore, password.toCharArray())
                .build();
    }

    @SneakyThrows
    private KeyStore keyStore(String type, Resource certificate, String password) {
        var keyStore = KeyStore.getInstance(type);
        try (InputStream pKeyFileStream = certificate.getInputStream()) {
            keyStore.load(pKeyFileStream, password.toCharArray());
        }
        return keyStore;
    }
}
