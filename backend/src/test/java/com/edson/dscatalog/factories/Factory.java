package com.edson.dscatalog.factories;

import com.edson.dscatalog.dto.ProductDTO;
import com.edson.dscatalog.entities.Category;
import com.edson.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {
    public static Product createProductWithIdNull() {
        Product product = new Product(null, "Phone", "Good Phone", 800.0, "https://img.com/img.png", Instant.parse("2020-09-17T03:00:00Z"));
        product.getCategories().add(createCategory());
        return product;
    }

    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img.png", Instant.parse("2020-09-17T03:00:00Z"));
        product.getCategories().add(createCategory());
        return product;
    }

    public static ProductDTO createProductDTO() {
        return new ProductDTO(createProduct());
    }

    public static ProductDTO createProductDTOWithIdNull() {
        return new ProductDTO(createProductWithIdNull());
    }

    public static Category createCategory() {
        return new Category(4L, "Eletronics");
    }
}
