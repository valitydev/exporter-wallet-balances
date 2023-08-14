package dev.vality.exporter.walletbalances.opensearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.vality.exporter.walletbalances.config.OpenSearchProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.mapping.FieldType;
import org.opensearch.client.opensearch._types.query_dsl.MatchPhraseQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.util.ObjectBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"Indentation", "LineLength"})
public class OpenSearchCustomClient {

    private final OpenSearchProperties openSearchProperties;
    private final OpenSearchClient openSearchClient;

    @Value("${interval.time}")
    private String intervalTime;

    @SneakyThrows
    public List<WalletBalanceData> getWalletBalanceData() {
        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        var collect = openSearchClient.search(s -> s
                                .index(openSearchProperties.getIndex())
                                .size(500)
                                .sort(builder -> builder
                                        .field(builder1 -> builder1
                                                .field("@timestamp")
                                                .order(SortOrder.Desc)
                                                .unmappedType(FieldType.Boolean)))
                                .docvalueFields(builder -> builder
                                        .field("@timestamp")
                                        .format("date_time"))
                                .query(builder -> builder
                                        .bool(builder1 -> builder1
                                                .must(builder2 -> builder2
                                                        .queryString(builder3 -> builder3
                                                                .query("\"Wallet balance\"")
                                                                .analyzeWildcard(true)))
                                                .filter(new RangeQuery.Builder()
                                                                .field("@timestamp")
                                                                .gte(JsonData.of(String.format("now-%ss", intervalTime)))
                                                                .format("strict_date_optional_time")
                                                                .build()
                                                                ._toQuery(),
                                                        new MatchPhraseQuery.Builder()
                                                                .field("kubernetes.container_name")
                                                                .query("fistful")
                                                                .build()
                                                                ._toQuery()))),
                        WalletBalanceData.class)
                .hits()
                .hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
        log.info("size {}, list {}", collect.size(), collect);
//        return openSearchClient.search(searchRequest, WalletBalanceData.class).hits().hits()
//                .stream()
//                .map(Hit::source)
//                .collect(Collectors.toList());
        return List.of();
    }

    private ObjectBuilder<Query> range(Query.Builder builder1) {
        return builder1.range(this::timestamp);
    }

    private RangeQuery.Builder timestamp(RangeQuery.Builder builder2) {
        return builder2.field("@timestamp").gte(JsonData.of(String.format("now-%ss", intervalTime)));
    }
}
