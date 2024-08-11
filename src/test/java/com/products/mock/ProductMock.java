package com.products.mock;

import com.products.domain.CategoryType;
import com.products.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductMock {
    public static Product PRODUCT = new Product(
            "Product 01",
            "Product description.",
            CategoryType.AUTOMOTIVE,
            10L,
            new BigDecimal("100")
    );

    public static Product INVALID_PRODUCT = new Product(
            "",
            "",
            CategoryType.SPORT,
            10L,
            new BigDecimal("11")
    );

    public static Product P1 = new Product(1L, "Product 001", "Product 001 description.", CategoryType.AUTOMOTIVE, 10L, new BigDecimal("100.00"));
    public static Product P2 = new Product(2L, "Product 002", "Product 002 description.", CategoryType.HEALTH, 20L, new BigDecimal("200.00"));
    public static Product P3 = new Product(3L, "Product 003", "Product 003 description.", CategoryType.SPORT, 30L, new BigDecimal("300.00"));

    public static List<Product> PRODUCTS = new ArrayList<>() { { add(P1); add(P2); add(P3); } };
}
