package com.edson.dscatalog.resources.integration;

import com.edson.dscatalog.dto.ProductDTO;
import com.edson.dscatalog.factories.Factory;
import com.edson.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private Long existingId;
    private Long nonExistingId;
    private Long totalProducts;
    private Integer totalPages;
    private String firstElement;
    private String secondElement;
    private String thirdElement;
    private ProductDTO productDTO;


    @BeforeEach
    void setUp() throws Exception {
        this.existingId = 1L;
        this.nonExistingId = 1000L;
        this.totalProducts = 25L;
        this.totalPages = 3;
        this.firstElement = "Macbook Pro";
        this.secondElement = "PC Gamer";
        this.thirdElement = "PC Gamer Alfa";
        this.productDTO = Factory.createProductDTO();
    }

    @Test
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        mockMvc.perform(get("/products/?page=0&size=12&sort=name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(totalProducts))
                .andExpect(jsonPath("$.totalPages").value(totalPages))
                .andExpect(jsonPath("$.content[0].name").value(firstElement))
                .andExpect(jsonPath("$.content[1].name").value(secondElement))
                .andExpect(jsonPath("$.content[2].name").value(thirdElement));
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        String jsonBody = mapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdNonExists() throws Exception {
        String jsonBody = mapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }
}
