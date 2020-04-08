package common.manager.domain.service.user.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import common.manager.app.api.UserApi;
import common.manager.domain.converter.Converter;
import common.manager.domain.exception.GenericException;
import common.manager.domain.model.User;
import common.manager.domain.repository.UserRepository;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl subject;

    private final Long userId = 123456789L;
    private User user;

    @Before
    public void setUp() {
        user = User.newBuilder()
                .documentNumber("documentNumber")
                .build();
    }

    @Test
    public void shouldGetUserUsingTheCorrectData() throws GenericException {
        when(userRepository.getOne(any())).thenReturn(user);

        UserApi executed = subject.get(userId);
        assertEquals(Converter.converUser(user), executed);
        verify(userRepository).getOne(userId);
    }

}