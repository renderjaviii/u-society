package usociety.manager.app.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static usociety.manager.domain.util.Constants.INVALID_CREDENTIALS;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import usociety.manager.TestUtils;
import usociety.manager.app.api.ApiError;
import usociety.manager.app.api.CategoryApi;
import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.GroupApiFixture;
import usociety.manager.app.api.UserApiFixture;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.app.handler.RestExceptionHandler;
import usociety.manager.app.rest.request.CreateOrUpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.service.group.GroupService;

@RunWith(MockitoJUnitRunner.class)
public class GroupControllerTest extends TestUtils {

    @Mock
    private SecurityContext securityContext;
    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController subject;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subject)
                .setControllerAdvice(new RestExceptionHandler())
                .build();

        when(securityContext.getAuthentication()).thenReturn(auth2Authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void shouldCreateGroupCorrectly() throws Exception {
        Mockito.when(groupService.create(any(), any())).thenReturn(GroupApiFixture.defaultValue);

        CreateOrUpdateGroupRequest request = CreateOrUpdateGroupRequest.newBuilder()
                .category(new CategoryApi(1L, "Category name"))
                .objectives(Collections.singletonList("Main"))
                .rules(Collections.singletonList("Important"))
                .description("Group description")
                .name("Group name")
                .build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/services/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        GroupApi executed = readMvcResultValue(mvcResult, GroupApi.class);
        Assert.assertEquals(GroupApiFixture.defaultValue, executed);

        Mockito.verify(groupService).create(USERNAME, request);
    }

    @Test
    public void shouldGetGroupCorrectly() throws Exception {
        GetGroupResponse response = GetGroupResponse.newBuilder()
                .activeMembers(Collections.singletonList(UserApiFixture.defaultValue))
                .membershipStatus(UserGroupStatusEnum.ACTIVE)
                .pendingMembers(Collections.emptyList())
                .group(GroupApiFixture.defaultValue)
                .isAdmin(Boolean.TRUE)
                .build();
        Mockito.when(groupService.get(any(), any())).thenReturn(response);

        long groupId = 1L;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/services/groups/{id}", groupId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        GetGroupResponse executed = readMvcResultValue(mvcResult, GetGroupResponse.class);
        Assert.assertEquals(response, executed);

        Mockito.verify(groupService).get(USERNAME, groupId);
    }

    @Test
    public void shouldGetGroupBySlugCorrectly() throws Exception {
        GetGroupResponse response = GetGroupResponse.newBuilder()
                .membershipStatus(UserGroupStatusEnum.PENDING)
                .group(GroupApiFixture.defaultValue)
                .isAdmin(Boolean.FALSE)
                .build();
        Mockito.when(groupService.getBySlug(any(), any())).thenReturn(response);

        String slug = "fake-group-name";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/services/groups/{slug}/slug", slug))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        GetGroupResponse executed = readMvcResultValue(mvcResult, GetGroupResponse.class);
        Assert.assertEquals(response, executed);

        Mockito.verify(groupService).getBySlug(USERNAME, slug);
    }

    @Test
    public void shouldUpdateGroupCorrectly() throws Exception {
        Mockito.when(groupService.update(any(), any(), any())).thenReturn(GroupApiFixture.defaultValue);

        CreateOrUpdateGroupRequest request = CreateOrUpdateGroupRequest.newBuilder()
                .category(new CategoryApi(1L, "Category name"))
                .objectives(Collections.singletonList("Main"))
                .rules(Collections.singletonList("Important"))
                .description("Group description")
                .name("Group name")
                .build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .put("/services/groups/{id}", GroupApiFixture.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        GroupApi executed = readMvcResultValue(mvcResult, GroupApi.class);
        Assert.assertEquals(GroupApiFixture.defaultValue, executed);

        Mockito.verify(groupService).update(USERNAME, GroupApiFixture.id, request);
    }

    @Test
    public void shouldGetAllUserGroupsCorrectly() throws Exception {
        Mockito.when(groupService.getAllUserGroups(any()))
                .thenReturn(Collections.singletonList(GroupApiFixture.defaultValue));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/services/groups/{username}/all", USERNAME))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<GroupApi> executed = readGroupListResponse(mvcResult);
        Assert.assertEquals(Collections.singletonList(GroupApiFixture.defaultValue), executed);

        Mockito.verify(groupService).getAllUserGroups(USERNAME);
    }

    @Test
    public void shouldUpdateMembershipCorrectly() throws Exception {
        UserGroupApi request = UserGroupApi.newBuilder()
                .member(UserApiFixture.defaultValue)
                .status(UserGroupStatusEnum.REJECTED)
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/services/groups/{id}/update-membership", GroupApiFixture.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(groupService).updateMembership(USERNAME, GroupApiFixture.id, request);
    }

    @Test
    public void shouldGetAllGroupsUsingFiltersCorrectly() throws Exception {
        Mockito.when(groupService.getByFilters(any(), any()))
                .thenReturn(Collections.nCopies(7, GroupApiFixture.defaultValue));

        String name = "pattern";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/services/groups/all")
                .param("name", name)
                .param("categoryId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<GroupApi> executed = readGroupListResponse(mvcResult);
        Assert.assertEquals(Collections.nCopies(7, GroupApiFixture.defaultValue), executed);

        Mockito.verify(groupService).getByFilters(name, 1L);
    }

    @Test
    public void shouldJoinToGroupCorrectly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/services/groups/{id}/join", GroupApiFixture.id))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(groupService).join(USERNAME, GroupApiFixture.id);
    }

    @Test
    public void shouldFailGettingAllUserGroupsIfIfTokenDoesNotBelongToHim() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/services/groups/{username}/all", "fake-user"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();

        ApiError executed = readMvcResultValue(mvcResult, ApiError.class);
        Assert.assertEquals(INVALID_CREDENTIALS, executed.getStatusCode());

        Mockito.verifyNoInteractions(groupService);
    }

    @Test
    public void shouldFailCreatingGroupIfRequestIsInvalid() throws Exception {
        CreateOrUpdateGroupRequest request = CreateOrUpdateGroupRequest.newBuilder()
                .category(new CategoryApi(null, "Category name"))
                .description("Group description")
                .build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/services/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        ApiError executed = readMvcResultValue(mvcResult, ApiError.class);

        String errorDescription = executed.getDescription();
        Assert.assertThat(errorDescription, Matchers.containsString("objectives must not be null"));
        Assert.assertThat(errorDescription, Matchers.containsString("name must not be empty"));
        Assert.assertThat(errorDescription, Matchers.containsString("name must be alphanumeric"));
        Assert.assertThat(errorDescription, Matchers.containsString("rules must not be null"));
        Assert.assertThat(errorDescription, Matchers.containsString("category.id must not be null"));

        Mockito.verifyNoInteractions(groupService);
    }

    private List<GroupApi> readGroupListResponse(MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        String responseContent = mvcResult.getResponse().getContentAsString();

        return mapper.readValue(responseContent, new TypeReference<List<GroupApi>>() {
        });
    }

}