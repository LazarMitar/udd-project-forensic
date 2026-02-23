package com.example.ddmdemo.service.impl;

import ai.djl.translate.TranslateException;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import com.example.ddmdemo.service.interfaces.SearchService;
import com.example.ddmdemo.util.VectorizationUtil;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchOperations elasticsearchTemplate;

    @Override
    public Page<ForensicReportIndex> simpleSearch(List<String> keywords, Pageable pageable, boolean isKNN) {
        if (isKNN) {
            try {
                return searchByVector(VectorizationUtil.getEmbedding(Strings.join(keywords, " ")));
            } catch (TranslateException e) {
                log.error("Vectorization failed");
                return Page.empty();
            }
        }

        var searchQueryBuilder = new NativeQueryBuilder()
                .withQuery(buildSimpleSearchQuery(keywords))
                .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }

    @Override
    public Page<ForensicReportIndex> advancedSearch(String expression, Pageable pageable) {
        var query = buildBooleanQuery(expression);
        var searchQueryBuilder = new NativeQueryBuilder()
                .withQuery(query)
                .withPageable(pageable);
        return runQuery(searchQueryBuilder.build());
    }

    // Boolean parser - infiksna u postfiksnu notaciju (Shunting-yard algoritam)
    private Query buildBooleanQuery(String expression) {
        // Tokenizacija
        var tokens = tokenize(expression);
        // Konverzija u postfiksnu notaciju
        var postfix = toPostfix(tokens);
        // Gradnja ES upita iz postfiksa
        return evalPostfix(postfix);
    }

    private List<String> tokenize(String expression) {
        var tokens = new ArrayList<String>();
        // Podrzavamo: field:value, "phrase query", AND, OR, NOT
        var matcher = java.util.regex.Pattern.compile(
                "\"[^\"]+\"|AND|OR|NOT|[\\w]+:[\\w\\s\"-]+"
        ).matcher(expression);
        while (matcher.find()) {
            tokens.add(matcher.group().trim());
        }
        return tokens;
    }

    private List<String> toPostfix(List<String> tokens) {
        var output = new ArrayList<String>();
        var stack = new ArrayDeque<String>();

        for (var token : tokens) {
            if (isOperator(token)) {
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(token)) {
                    output.add(stack.pop());
                }
                stack.push(token);
            } else {
                output.add(token);
            }
        }
        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }
        return output;
    }

    private Query evalPostfix(List<String> postfix) {
        var stack = new ArrayDeque<Query>();

        for (var token : postfix) {
            if (isOperator(token)) {
                if (token.equals("NOT")) {
                    var operand = stack.pop();
                    stack.push(BoolQuery.of(b -> b.mustNot(operand))._toQuery());
                } else {
                    var right = stack.pop();
                    var left = stack.pop();
                    if (token.equals("AND")) {
                        stack.push(BoolQuery.of(b -> b.must(left).must(right))._toQuery());
                    } else { // OR
                        stack.push(BoolQuery.of(b -> b.should(left).should(right))._toQuery());
                    }
                }
            } else {
                stack.push(buildSingleQuery(token));
            }
        }
        return stack.pop();
    }

    private Query buildSingleQuery(String token) {
        // Proveri da li je phrase query (sadrzi navodnik)
        boolean isPhrase = token.contains("\"");

        // Parsiranje field:value
        var parts = token.split(":", 2);
        var field = parts[0].trim();
        var value = parts[1].trim().replace("\"", "");

        if (isPhrase) {
            return co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery.of(
                    m -> m.field(field).query(value))._toQuery();
        } else {
            return co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery.of(
                    m -> m.field(field).query(value))._toQuery();
        }
    }

    private boolean isOperator(String token) {
        return token.equals("AND") || token.equals("OR") || token.equals("NOT");
    }

    private int precedence(String op) {
        return switch (op) {
            case "NOT" -> 3;
            case "AND" -> 2;
            case "OR" -> 1;
            default -> 0;
        };
    }

    private Query buildSimpleSearchQuery(List<String> keywords) {
        return BoolQuery.of(q -> q.must(mb -> mb.bool(b -> {
            keywords.forEach(keyword -> {
                b.should(sb -> sb.match(m -> m.field("forensic_analyst").query(keyword)));
                b.should(sb -> sb.match(m -> m.field("organization").query(keyword)));
                b.should(sb -> sb.match(m -> m.field("malware_name").query(keyword)));
                b.should(sb -> sb.match(m -> m.field("description").query(keyword)));
                b.should(sb -> sb.term(m -> m.field("threat_classification").value(keyword)));
                b.should(sb -> sb.term(m -> m.field("hash_value").value(keyword)));
            });
            return b;
        })))._toQuery();
    }

    public Page<ForensicReportIndex> searchByVector(float[] queryVector) {
        Float[] floatObjects = new Float[queryVector.length];
        for (int i = 0; i < queryVector.length; i++) {
            floatObjects[i] = queryVector[i];
        }
        List<Float> floatList = Arrays.stream(floatObjects).collect(Collectors.toList());

        var knnQuery = new KnnQuery.Builder()
                .field("vectorizedContent")
                .queryVector(floatList)
                .numCandidates(100)
                .k(10)
                .boost(10.0f)
                .build();

        var searchQuery = NativeQuery.builder()
                .withKnnQuery(knnQuery)
                .withMaxResults(5)
                .withSearchType(null)
                .build();

        var searchHitsPaged = SearchHitSupport.searchPageFor(
                elasticsearchTemplate.search(searchQuery, ForensicReportIndex.class),
                searchQuery.getPageable());

        return (Page<ForensicReportIndex>) SearchHitSupport.unwrapSearchHits(searchHitsPaged);
    }

    private Page<ForensicReportIndex> runQuery(NativeQuery searchQuery) {
        var searchHits = elasticsearchTemplate.search(searchQuery, ForensicReportIndex.class,
                IndexCoordinates.of("forensic_reports"));
        var searchHitsPaged = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());
        return (Page<ForensicReportIndex>) SearchHitSupport.unwrapSearchHits(searchHitsPaged);
    }
}