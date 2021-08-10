package usociety.manager.app.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import usociety.manager.app.api.CategoryApi;
import usociety.manager.domain.service.category.CategoryService;

@Tag(name = "Category controller")
@Validated
@RestController
@RequestMapping(path = "v1/services/categories")
public class CategoryController extends AbstractController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get all", responses = @ApiResponse(responseCode = "200"))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoryApi>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

}
