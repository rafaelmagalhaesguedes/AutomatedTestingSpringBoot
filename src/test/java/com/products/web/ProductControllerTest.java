package com.products.web;

import static com.products.mock.ProductMock.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.products.domain.CategoryType;
import com.products.domain.Product;
import com.products.domain.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService service;

    @Test
    public void createProduct_WithValidData_ReturnsCreated() throws Exception {
        when(service.create(PRODUCT)).thenReturn(PRODUCT);

        mockMvc.perform(post("/products")
                        .content(objectMapper.writeValueAsString(PRODUCT))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void createProduct_WithInvalidData_ReturnBadRequest() throws Exception {
        var emptyProduct = new Product();
        var invalidProduct = new Product("", "", CategoryType.SPORT, 10L, new BigDecimal("11"));

        mockMvc.perform(post("/products")
                        .content(objectMapper.writeValueAsString(emptyProduct))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(post("/products")
                        .content(objectMapper.writeValueAsString(invalidProduct))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void createProduct_WithExistingName_ReturnsConflict() throws Exception {
        when(service.create(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/products")
                        .content(objectMapper.writeValueAsString(PRODUCT))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

    }

    @Test
    public void getProduct_ByExistingId_ReturnsProduct() throws Exception {
        when(service.get(PRODUCT.getId())).thenReturn(Optional.of(PRODUCT));

        mockMvc.perform(get("/products/" + PRODUCT.getId())
                        .content(objectMapper.writeValueAsString(PRODUCT))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getProduct_ByUnexistingId_ReturnsEmpty() throws Exception {
        when(service.get(99246579109999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/products/" + 92191791332L)
                        .content(objectMapper.writeValueAsString(PRODUCT))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getProduct_ByExistingName_ReturnsProduct() throws Exception {
        when(service.getByName(PRODUCT.getName())).thenReturn(Optional.of(PRODUCT));

        mockMvc.perform(get("/products/name/" + PRODUCT.getName())
                        .content(objectMapper.writeValueAsString(PRODUCT))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getProduct_ByUnexistingName_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/products/name/1")
                        .content(objectMapper.writeValueAsString(PRODUCT))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void listProducts_ReturnsFilteredProducts() throws Exception {
        when(service.list(null, null)).thenReturn(PRODUCTS);
        when(service.list(P1.getName(), P1.getCategory())).thenReturn(java.util.List.of(P1));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void listProducts_ReturnsNoProducts() throws Exception {
        when(service.list(null, null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    public void removeProduct_WithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void removeProduct_WithUnexistingId_ReturnsNoContent() throws Exception {
        final Long productId = 1L;

        doThrow(new EmptyResultDataAccessException(1))
                .when(service).remove(productId);

        mockMvc.perform(delete("/products/" + productId))
                .andExpect(status().isNotFound());
    }
}
