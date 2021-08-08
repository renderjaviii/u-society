package usociety.manager.app.rest;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.type.TypeReference;

import usociety.manager.app.api.CategoryApi;
import usociety.manager.app.handler.RestExceptionHandler;
import usociety.manager.domain.service.category.CategoryService;
import usociety.manager.domain.util.mapper.CustomObjectMapper;
import usociety.manager.domain.util.mapper.impl.CustomObjectMapperImpl;

@RunWith(MockitoJUnitRunner.class)
public class CategoryControllerTest {

    private static final CustomObjectMapper mapper = new CustomObjectMapperImpl();
    private static final String BASE_PATH = "/v1/services/categories";

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController subject;

    private MockMvc mockMvc;
    private List<CategoryApi> categoryList;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(subject)
                .setControllerAdvice(new RestExceptionHandler())
                .build();

        categoryList = Arrays.asList(
                new CategoryApi(1L, "Category 1"),
                new CategoryApi(2L, "Category 2")
        );
    }

    @Test
    public void shouldGetAllCorrectly() throws Exception {
        Mockito.when(categoryService.getAll()).thenReturn(categoryList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentResponse = mvcResult.getResponse().getContentAsString();
        List<CategoryApi> executed = mapper.readValue(contentResponse, new TypeReference<List<CategoryApi>>() {
        });

        Assert.assertEquals(categoryList, executed);
        Mockito.verify(categoryService, Mockito.only()).getAll();
    }

}