package usociety.manager.domain.service.common.impl;

import static org.mockito.ArgumentMatchers.any;
import static usociety.manager.domain.util.Constants.USER_NOT_FOUND;

import java.time.Clock;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import usociety.manager.app.api.GroupApiFixture;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserApiFixture;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.user.UserService;
import usociety.manager.domain.util.mapper.CustomObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class AbstractServiceImplTest {

    @Mock
    private CustomObjectMapper objectMapper;
    @Mock
    private GroupService groupService;
    @Mock
    private UserService userService;
    @Mock
    private Clock clock;

    @InjectMocks
    private AbstractServiceImpl subject;

    @Test
    public void shouldGetUserCorrectly() throws GenericException {
        Mockito.when(userService.get(any())).thenReturn(UserApiFixture.defaultValue);
        UserApi executed = subject.getUser(UserApiFixture.username);
        Assert.assertEquals(UserApiFixture.defaultValue, executed);
        Mockito.verify(userService).get(UserApiFixture.username);
    }

    @Test(expected = GenericException.class)
    public void shouldFailGettingUserIfItDoesNotExist() throws GenericException {
        try {
            subject.getUser(UserApiFixture.username);
        } catch (GenericException e) {
            Assert.assertEquals(USER_NOT_FOUND, e.getErrorCode());
            throw e;
        }
        Assert.fail();
    }

    @Test
    public void shouldGetGroupCorrectly() throws GenericException {
        Group group = Group.newBuilder().id(GroupApiFixture.id).build();
        Mockito.when(groupService.get(any())).thenReturn(group);
        Group executed = subject.getGroup(GroupApiFixture.id);
        Assert.assertEquals(group, executed);
        Mockito.verify(groupService).get(GroupApiFixture.id);
    }

    @Test
    public void shouldValidateIfUserIsMemberCorrectly() throws GenericException {
        String error = "ERROR";
        subject.validateIfUserIsMember(UserApiFixture.username, GroupApiFixture.id, error);
        Mockito.verify(groupService).validateIfUserIsMember(UserApiFixture.username, GroupApiFixture.id, error);
    }

}