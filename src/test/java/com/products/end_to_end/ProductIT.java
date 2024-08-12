package com.products.end_to_end;

import static org.assertj.core.api.Assertions.assertThat;
import static com.products.mock.ProductMock.*;

import com.products.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

/**
 * End-to-End testing
 *
 * @RafaGuedes 12/07/2024
 */
@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = { "/import_products.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/remove_products.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class ProductIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private void assertProductProperties(Product actual, Product expected) {
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getCategory()).isEqualTo(expected.getCategory());
        assertThat(actual.getQuantity()).isEqualTo(expected.getQuantity());
        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    }

    @Test
    public void createProduct_ReturnsCreated() {
        ResponseEntity<Product> sut = restTemplate.postForEntity("/products", PRODUCT, Product.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(sut.getBody().getId()).isNotNull();
        assertProductProperties(sut.getBody(), PRODUCT);
    }

    @Test
    public void getProduct_ReturnsProduct() {
        ResponseEntity<Product> sut = restTemplate.getForEntity("/products/1", Product.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertProductProperties(sut.getBody(), P1);
    }

    @Test
    public void getProductByName_ReturnsProduct() {
        ResponseEntity<Product> sut = restTemplate.getForEntity("/products/name/" + P1.getName(), Product.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertProductProperties(sut.getBody(), P1);
    }

    @Test
    public void listProducts_ReturnsAllProducts() {
        ResponseEntity<Product[]> sut = restTemplate.getForEntity("/products", Product[].class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).hasSize(3);
        assertProductProperties(sut.getBody()[0], P1);
    }

    @Test
    public void listProducts_byCategory_ReturnsProducts() {
        ResponseEntity<Product[]> sut = restTemplate
                .getForEntity("/products?category=" + P1.getCategory(), Product[].class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sut.getBody()).hasSize(1);
        assertProductProperties(sut.getBody()[0], P1);
    }

    @Test
    public void removeProduct_ReturnNoContent() {
        ResponseEntity<Void> sut = restTemplate
                .exchange("/products/" + P1.getId(), HttpMethod.DELETE, null, Void.class);

        assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}