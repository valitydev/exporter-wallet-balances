package dev.vality.exporter.walletbalances.service;

import dev.vality.exporter.walletbalances.config.OpenSearchProperties;
import dev.vality.exporter.walletbalances.model.WalletBalanceData;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.mapping.FieldType;
import org.opensearch.client.opensearch._types.query_dsl.MatchPhraseQuery;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenSearchService {

    private static final String KUBERNETES_CONTAINER_NAME = "kubernetes.container_name";
    private static final String TIMESTAMP = "@timestamp";
    private static final String DATE_TIME = "date_time";
    private static final String STRICT_DATE_OPTIONAL_TIME = "strict_date_optional_time";
    private static final String FISTFUL = "fistful";
    private static final String WALLET_BALANCE = "\"Wallet balance\"";

    private final OpenSearchProperties openSearchProperties;
    private final OpenSearchClient openSearchClient;

    @Value("${interval.time}")
    private String intervalTime;

    @SneakyThrows
    public List<WalletBalanceData> getWalletsBalancesDataByInterval() {
        return openSearchClient.search(s -> s
                                .size(10000)
                                .index(openSearchProperties.getIndex())
                                .sort(builder -> builder
                                        .field(builder1 -> builder1
                                                .field(TIMESTAMP)
                                                .order(SortOrder.Asc)
                                                .unmappedType(FieldType.Boolean)))
                                .docvalueFields(builder -> builder
                                        .field(TIMESTAMP)
                                        .format(DATE_TIME))
                                .query(builder -> builder
                                        .bool(builder1 -> builder1
                                                .must(builder2 -> builder2
                                                        .queryString(builder3 -> builder3
                                                                .query(WALLET_BALANCE)
                                                                .analyzeWildcard(true)))
                                                .filter(new RangeQuery.Builder()
                                                                .field(TIMESTAMP)
                                                                .gte(JsonData.of(
                                                                        String.format("now-%ss", intervalTime)))
                                                                .format(STRICT_DATE_OPTIONAL_TIME)
                                                                .build()
                                                                ._toQuery(),
                                                        new MatchPhraseQuery.Builder()
                                                                .field(KUBERNETES_CONTAINER_NAME)
                                                                .query(FISTFUL)
                                                                .build()
                                                                ._toQuery()))),
                        WalletBalanceData.class)
                .hits()
                .hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }
}
