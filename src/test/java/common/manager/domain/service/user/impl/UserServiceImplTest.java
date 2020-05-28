package common.manager.domain.service.user.impl;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
/*
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl subject;

    private final String userName = "username";
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

        UserApi executed = subject.get(userName);
        assertEquals(Converter.user(user), executed);
        verify(userRepository).findByUsername(userName);
    }*/

}