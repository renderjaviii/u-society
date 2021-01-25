package usociety.manager.domain.service.category;

import java.util.List;

import usociety.manager.app.api.CategoryApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;

public interface CategoryService {

    List<CategoryApi> getAll() throws GenericException;

    Category get(Long id) throws GenericException;

}
