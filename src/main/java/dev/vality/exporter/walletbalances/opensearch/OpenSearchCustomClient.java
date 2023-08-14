package dev.vality.exporter.walletbalances.opensearch;

import dev.vality.exporter.walletbalances.config.OpenSearchProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.util.ObjectBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("Indentation")
public class OpenSearchCustomClient {

    private final OpenSearchProperties openSearchProperties;
    private final OpenSearchClient openSearchClient;

    @Value("${interval.time}")
    private String intervalTime;

    @SneakyThrows
    public List<WalletBalanceData> getWalletBalanceData() {
        MatchQuery mustMatchQuery1 = new MatchQuery.Builder().field("message")
                .query(builder -> builder.stringValue("Wallet balance")).build();
        MatchQuery mustMatchQuery2 = new MatchQuery.Builder().field("kubernetes.container_name")
                .query(builder -> builder.stringValue("fistful")).build();
        var timestamp = timestamp(new RangeQuery.Builder()).build();
        List filterQueries = new ArrayList<>();
        filterQueries.add(timestamp._toQuery());
        List mustQueries = new ArrayList<>();
        mustQueries.add(mustMatchQuery1._toQuery());
        mustQueries.add(mustMatchQuery2._toQuery());
        BoolQuery boolQuery = new BoolQuery.Builder().must(mustQueries).filter(filterQueries).build();
        var searchRequest = new SearchRequest.Builder()
                .index(openSearchProperties.getIndex())
                .query(q -> q.match(builder -> builder.field("message")
                        .query(builder1 -> builder1.stringValue("Wallet balance"))))
                .query(q -> q.bool(builder -> builder.filter(this::range)))
                .build();
        var collect = openSearchClient.search(s -> {
                            s.query(boolQuery._toQuery());
                            return s;
                        },
                        Object.class).hits().hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
        log.info("{}", collect);
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
