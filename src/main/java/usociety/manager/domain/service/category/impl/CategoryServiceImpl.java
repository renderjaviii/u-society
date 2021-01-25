package usociety.manager.domain.service.category.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.CategoryApi;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.repository.CategoryRepository;
import usociety.manager.domain.service.category.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryApi> getAll() throws GenericException {
        return categoryRepository.findAll()
                .stream()
                .map(Converter::category)
                .collect(Collectors.toList());
    }

    @Override
    public Category get(Long id) throws GenericException {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (!optionalCategory.isPresent()) {
            throw new GenericException(String.format("Categoría con id: %s no existe.", id), "ERROR_GETTING_CATEGORY");
        }
        return optionalCategory.get();
    }

}
