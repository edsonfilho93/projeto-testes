package com.edson.dscatalog.services.integration;

import com.edson.dscatalog.repositories.ProductRepository;
import com.edson.dscatalog.services.ProductService;
import com.edson.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.data.domain.Sort.*;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTests {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long totalProducts;
    private String firstElement;
    private String secondElement;
    private String thirdElement;


    @BeforeEach
    void setUp() throws Exception {
        this.existingId = 1L;
        this.nonExistingId = 1000L;
        this.totalProducts = 25L;
        this.firstElement = "Macbook Pro";
        this.secondElement = "PC Gamer";
        this.thirdElement = "PC Gamer Alfa";
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        service.delete(existingId);
        assertEquals(totalProducts - 1, repository.count());
    }

    @Test
    public void deleteShouldThrowsResourceNotFoundExceptionWhenIdNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        var result = service.findAll(of(0, 10));

        assertFalse(result.isEmpty());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertEquals(totalProducts, result.getTotalElements());
    }

    @Test
    public void findAllPagaedShouldReturnEmptyPageWhenPageDoesNotExists() {
        var result = service.findAll(of(10, 10));
        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortByName() {
        var result = service.findAll(of(0, 10, by("Name")));

        assertFalse(result.isEmpty());
        assertEquals(firstElement, result.getContent().get(0).getName());
        assertEquals(secondElement, result.getContent().get(1).getName());
        assertEquals(thirdElement, result.getContent().get(2).getName());
    }
}
