package com.products.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository repository;

    @Autowired
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product create(Product product) {
        return repository.save(product);
    }

    public Optional<Product> get(Long id) {
        return repository.findById(id);
    }

    public Optional<Product> getByName(String name) {
        return repository.findByName(name);
    }

    public List<Product> list(String name, CategoryType category) {
        Example<Product> query = QueryBuilder.makeQuery(new Product(name, category));
        return repository.findAll(query);
    }

    public void remove(Long id) {
        repository.deleteById(id);
    }
}
