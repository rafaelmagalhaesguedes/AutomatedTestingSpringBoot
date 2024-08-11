package com.products.domain;

import static com.products.mock.ProductMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach() {
        PRODUCT.setId(null);
    }

    @Test
    public void createProduct_WithValidData_ReturnsPlanet() {
        var product = repository.save(PRODUCT);

        var sut = testEntityManager.find(Product.class, product.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(PRODUCT.getName());
        assertThat(sut.getDescription()).isEqualTo(PRODUCT.getDescription());
        assertThat(sut.getCategory()).isEqualTo(PRODUCT.getCategory());
        assertThat(sut.getQuantity()).isEqualTo(PRODUCT.getQuantity());
        assertThat(sut.getPrice()).isEqualTo(PRODUCT.getPrice());
    }

    @Test
    public void createProduct_WithInvalidData_ThrowsException() {
        var emptyProduct = new Product();
        var invalidProduct = new Product("", "", CategoryType.SPORT, 10L, new BigDecimal("11"));

        assertThatThrownBy(() -> repository.save(emptyProduct)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repository.save(invalidProduct)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void createProduct_WithExistingName_ThrowsException() {
        Product product = testEntityManager.persistFlushFind(PRODUCT);
        testEntityManager.detach(product);
        product.setId(null);

        assertThatThrownBy(() -> repository.save(product)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getProduct_ByExistingId_ReturnsProduct() {
        var product = testEntityManager.persistFlushFind(PRODUCT);

        Optional<Product> productOpt = repository.findById(product.getId());

        assertThat(productOpt).isNotEmpty();
        assertThat(productOpt.get()).isEqualTo(product);
    }

    @Test
    public void getProduct_ByUnexistingId_ReturnsEmpty() {
        Optional<Product> productOpt = repository.findById(91264219092L);

        assertThat(productOpt).isEmpty();
    }

    @Test
    public void getProduct_ByExistingName_ReturnsProduct() {
        var product = testEntityManager.persistFlushFind(PRODUCT);

        Optional<Product> productOpt = repository.findByName(product.getName());

        assertThat(productOpt).isNotEmpty();
        assertThat(productOpt.get()).isEqualTo(product);
    }

    @Test
    public void getProduct_ByUnexistingName_ReturnsNotFound() {
        Optional<Product> productOpt = repository.findByName("0");

        assertThat(productOpt).isEmpty();
    }

    @Sql(scripts = "/import_products.sql")
    @Test
    public void listProducts_ReturnsFilteredProducts() {
        Example<Product> queryWithoutFilters = QueryBuilder.makeQuery(new Product());
        Example<Product> queryWithFilters = QueryBuilder.makeQuery(new Product(P1.getName(), P1.getCategory()));

        List<Product> responseWithoutFilters = repository.findAll(queryWithoutFilters);
        List<Product> responseWithFilters = repository.findAll(queryWithFilters);

        assertThat(responseWithoutFilters).isNotEmpty();
        assertThat(responseWithoutFilters).hasSize(3);
        assertThat(responseWithFilters).isNotEmpty();
        assertThat(responseWithFilters).hasSize(1);
    }

    @Test
    public void listProducts_ReturnsNoProducts() {
        Example<Product> query = QueryBuilder.makeQuery(new Product());

        List<Product> response = repository.findAll((query));

        assertThat(response).isEmpty();
    }

    @Test
    public void removeProduct_WithExistingId_RemovesProductFromDatabase() {
        var product = testEntityManager.persistFlushFind(PRODUCT);

        repository.deleteById(product.getId());

        var removedProduct = testEntityManager.find(Product.class, product.getId());
        assertThat(removedProduct).isNull();
    }

    /* *
    @Test
    public void removeProduct_WithUnexistingId_ThrowsException() {
        assertThatThrownBy(() -> repository.deleteById(PRODUCT.getId()))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
    */
}
