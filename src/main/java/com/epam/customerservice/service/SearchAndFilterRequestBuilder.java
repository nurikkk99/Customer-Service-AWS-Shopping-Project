package com.epam.customerservice.service;

import com.epam.customerservice.dto.SearchAndFilterRequestDto;
import java.util.List;
import java.util.Optional;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

public class SearchAndFilterRequestBuilder {

    public static SearchRequest buildSearchAndFilterRequest(String indexName, SearchAndFilterRequestDto requestDto) {
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .postFilter(getQueryBuilder(requestDto));

        SortOrder sortOrder = Optional.ofNullable(requestDto.getSortOrder()).orElse(SortOrder.ASC);
        Optional.ofNullable(requestDto.getSortType()).ifPresent(x->builder.sort(x,sortOrder));
        SearchRequest request = new SearchRequest(indexName);
        request.source(builder);
        return request;
    }

    private static QueryBuilder getQueryBuilder(SearchAndFilterRequestDto requestDto) {
        final List<String> searchFields = requestDto.getSearchFields();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if(searchFields != null) {
            if (searchFields.size() > 1) {
                MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(requestDto.getSearchTerm()).type(Type.CROSS_FIELDS).operator(Operator.AND);
                searchFields.forEach(x -> queryBuilder.field(x));
                boolQueryBuilder.must(queryBuilder);
            } else if (searchFields.size() == 1) {
                QueryBuilder queryBuilder = searchFields.stream().findFirst()
                        .map(field -> QueryBuilders.matchQuery(field, requestDto.getSearchTerm()).operator(Operator.AND)).orElse(null);
                boolQueryBuilder.must(queryBuilder);
            }
        }
        Optional.ofNullable(requestDto.getType())
                .ifPresent(x -> boolQueryBuilder.must(QueryBuilders.matchQuery("type", x)));
        Optional.ofNullable(requestDto.getManufacturer()).ifPresent(x->boolQueryBuilder.must(QueryBuilders.matchQuery("manufacturer", requestDto.getManufacturer())));

        return boolQueryBuilder;
    }
}
