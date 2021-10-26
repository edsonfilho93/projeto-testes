package com.edson.dscatalog.repositories;

import com.edson.dscatalog.entities.Product;
import com.edson.dscatalog.factories.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long totalProducts;

    @BeforeEach
    void setup() {
        existingId = 1L;
        nonExistingId = 9_999L;
        totalProducts = 25L;
    }


    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void deleteShouldThrowExcpetionWhenIdDoesNotExists() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            repository.deleteById(nonExistingId);
        });
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Product entity = Factory.createProductWithIdNull();
        entity = repository.save(entity);
        Assertions.assertEquals(totalProducts + 1, entity.getId());
    }

    @Test
    public void findByIdShouldReturnObjectWhenIdExists() {
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void findByIdShouldNotReturnObjectWhenIdNonExists() {
        Optional<Product> result = repository.findById(nonExistingId);
        Assertions.assertTrue(result.isEmpty());
    }
}
