package com.products.domain;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

public class QueryBuilder {
    private QueryBuilder() { }

    public static Example<Product> makeQuery(Product product) {
        ExampleMatcher exampleMatcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withIgnoreNullValues();

        return Example.of(product, exampleMatcher);
    }
}
