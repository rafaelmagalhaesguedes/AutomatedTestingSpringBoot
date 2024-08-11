package com.products.domain;

import org.springframework.data.domain.Example;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long>, QueryByExampleExecutor<Product> {
    Optional<Product> findByName(String name);

    @Override
    <S extends Product> List<S> findAll(Example<S> example);
}
