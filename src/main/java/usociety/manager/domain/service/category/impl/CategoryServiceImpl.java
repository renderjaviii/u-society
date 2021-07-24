package usociety.manager.domain.service.category.impl;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.CategoryApi;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.repository.CategoryRepository;
import usociety.manager.domain.service.category.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_RESOURCE_CACHE_NAME = "categories";

    private static final String GETTING_CATEGORY_ERROR_CODE = "ERROR_GETTING_CATEGORY";
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @CachePut(value = CATEGORY_RESOURCE_CACHE_NAME)
    public List<CategoryApi> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(Converter::category)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CATEGORY_RESOURCE_CACHE_NAME)
    public Category get(Long id) throws GenericException {
        return categoryRepository.findById(id)
                .orElseThrow(buildCategoryNotFoundException(id));
    }

    private Supplier<GenericException> buildCategoryNotFoundException(Long id) {
        return () -> {
            String errorMessage = String.format("Category with id: %s does not exist.", id);
            return new GenericException(errorMessage, GETTING_CATEGORY_ERROR_CODE);
        };
    }

}
