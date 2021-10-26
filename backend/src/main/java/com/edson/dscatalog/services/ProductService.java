package com.edson.dscatalog.services;

import com.edson.dscatalog.dto.ProductDTO;
import com.edson.dscatalog.entities.Category;
import com.edson.dscatalog.entities.Product;
import com.edson.dscatalog.repositories.CategoryRepository;
import com.edson.dscatalog.repositories.ProductRepository;
import com.edson.dscatalog.services.exceptions.DatabaseException;
import com.edson.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> list = repository.findAll(pageable);
        return list.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        //Optional<Product> obj = repository.findById(id);
        Product entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entidade não encontrada"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(entity, dto);
        entity = repository.save(entity);
        return new ProductDTO(entity);

    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getOne(id);
            copyDtoToEntity(entity, dto);
            entity = repository.save(entity);
            return new ProductDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("id não encontrado:".concat(String.valueOf(id)));
        } catch (DataIntegrityViolationException e2) {
            throw new DatabaseException("Violação de Integridade do BD");
        }
    }

    private void copyDtoToEntity(Product entity, ProductDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setPrice(dto.getPrice());

        entity.getCategories().clear();

        dto.getCategories().forEach(categoryDTO -> {
            Category category = categoryRepository.getOne(categoryDTO.getId());
            entity.getCategories().add(category);
        });
    }
}
