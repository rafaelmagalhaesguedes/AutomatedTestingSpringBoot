package com.products.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.products.mock.ProductMock.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Test
    public void createProduct_WithValidData_ReturnsProduct() {
        when(repository.save(PRODUCT))
                .thenReturn(PRODUCT);

        Product sut = service.create(PRODUCT);

        assertThat(sut).isEqualTo(PRODUCT);
    }

    @Test
    public void createProduct_WithInvalidData_ThrowsException() {
        when(repository.save(INVALID_PRODUCT))
                .thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> service.create(INVALID_PRODUCT))
                .isInstanceOf(RuntimeException.class);

    }

    @Test
    public void getProduct_ByExistingId_ReturnsProduct() {
        var ID = PRODUCT.getId();

        when(repository.findById(ID))
                .thenReturn(Optional.of(PRODUCT));

        Optional<Product> sut = service.get(ID);

        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(PRODUCT);
    }

    @Test
    public void getProduct_ByNonExistentId_ReturnsEmpty() {
        var ID = PRODUCT.getId();

        when(repository.findById(ID))
                .thenReturn(Optional.empty());

        Optional<Product> sut = service.get(ID);

        assertThat(sut).isEmpty();
    }

    @Test
    public void getProduct_ByExistingName_ReturnsProduct() {
        var NAME = PRODUCT.getName();

        when(repository.findByName(NAME))
                .thenReturn(Optional.of(PRODUCT));

        Optional<Product> sut = service.getByName(NAME);

        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(PRODUCT);
    }

    @Test
    public void getProduct_ByNonExistentName_ReturnsEmpty() {
        when(repository.findByName("003-a"))
                .thenReturn(Optional.empty());

        Optional<Product> sut = service.getByName("003-a");

        assertThat(sut).isEmpty();
    }

    @Test
    public void listProducts_ReturnsAllProducts() {
        List<Product> products = new ArrayList<>() {{
            add(PRODUCT);
        }};

        when(repository.findAll(any(Example.class))).thenReturn(products);

        List<Product> sut = service.list(PRODUCT.getName(), PRODUCT.getCategory());

        assertThat(sut).isNotEmpty();
        assertThat(sut).hasSize(1);
        assertThat(sut.get(0)).isEqualTo(PRODUCT);
    }

    @Test
    public void listProducts_ReturnsNoProducts() {
        when(repository.findAll(any())).thenReturn(Collections.emptyList());

        List<Product> sut = service.list(PRODUCT.getName(), PRODUCT.getCategory());

        assertThat(sut).isEmpty();
    }

    @Test
    public void removeProduct_WithExistingId_doesNotThrowsAnyException() {
        assertThatCode(() -> service.remove(PRODUCT.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    public void removeProduct_WithUnexistingId_ThrowsException() {
        doThrow(new RuntimeException()).when(repository)
                .deleteById(999999L);

        assertThatThrownBy(() -> service.remove(999999L))
                .isInstanceOf(RuntimeException.class);
    }
}
