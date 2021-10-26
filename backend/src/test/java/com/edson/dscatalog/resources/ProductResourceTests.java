package com.edson.dscatalog.resources;

import com.edson.dscatalog.dto.ProductDTO;
import com.edson.dscatalog.factories.Factory;
import com.edson.dscatalog.services.ProductService;
import com.edson.dscatalog.services.exceptions.DatabaseException;
import com.edson.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper mapper;

    private PageImpl<ProductDTO> page;
    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private ProductDTO productDTO;

    @BeforeEach
    void setup() throws Exception {
        this.existingId = 1L;
        this.nonExistingId = 2L;
        this.dependentId = 3L;
        this.productDTO = Factory.createProductDTO();
        this.page = new PageImpl<>(of(productDTO));

        when(service.findAll(any())).thenReturn(page);

        when(service.findById(existingId)).thenReturn(productDTO);
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(service.update(eq(existingId), any())).thenReturn(productDTO);
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        doThrow(DatabaseException.class).when(service).delete(dependentId);

        when(service.insert(any())).thenReturn(productDTO);

    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/products/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdNonExists() throws Exception {
        mockMvc.perform(get("/products/{id}", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
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
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    public void deleteShouldNothingWhenIdExists() throws Exception {
        mockMvc.perform(delete("/products/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdNonExists() throws Exception {
        mockMvc.perform(delete("/products/{id}", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() throws Exception {
        mockMvc.perform(delete("/products/{id}", dependentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Database exception"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DatabaseException));
    }

    @Test
    public void insertShouldReturnProductDTO() throws Exception {
        String jsonBody = mapper.writeValueAsString(productDTO);

        mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isCreated());
    }
}
