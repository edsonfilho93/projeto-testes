package com.edson.dscatalog.services;

import com.edson.dscatalog.dto.CategoryDTO;
import com.edson.dscatalog.entities.Category;
import com.edson.dscatalog.repositories.CategoryRepository;
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
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(Pageable pageable) {
        Page<Category> list = repository.findAll(pageable);
        return list.map(CategoryDTO::new);
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        return new CategoryDTO(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entidade não encontrada!")));
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = repository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category entity = repository.getOne(id);
            entity.setName(dto.getName());
            entity = repository.save(entity);
            return new CategoryDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("id não encontrado: ".concat(String.valueOf(id)));
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("id não encontrado:".concat(String.valueOf(id)));
        } catch (DataIntegrityViolationException e2) {
            throw new DatabaseException("Violação de Integridade do BD");
        }
    }
}
