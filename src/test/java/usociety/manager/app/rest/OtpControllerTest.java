package usociety.manager.app.rest;

import java.time.LocalDateTime;

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

import usociety.manager.app.api.OtpApi;
import usociety.manager.app.handler.RestExceptionHandler;
import usociety.manager.domain.service.otp.OtpService;
import usociety.manager.domain.util.mapper.CustomObjectMapper;
import usociety.manager.domain.util.mapper.impl.CustomObjectMapperImpl;

@RunWith(MockitoJUnitRunner.class)
public class OtpControllerTest {

    private static final CustomObjectMapper mapper = new CustomObjectMapperImpl();
    private static final String BASE_PATH = "/services/otps";

    @Mock
    private OtpService otpService;

    @InjectMocks
    private OtpController subject;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subject)
                .setControllerAdvice(new RestExceptionHandler(), new RestExceptionHandler())
                .build();
    }

    @Test
    public void shouldCreateOtpCorrectly() throws Exception {
        String email = "email@domain.com";
        OtpApi otp = OtpApi.newBuilder()
                .createdAt(LocalDateTime.of(2022, 10, 5, 0, 0))
                .expiresAt(LocalDateTime.of(2022, 10, 6, 0, 0))
                .userEmailOwner(email)
                .usernameOwner("123456789")
                .active(Boolean.TRUE)
                .otpCode("12345")
                .id(1L)
                .build();
        Mockito.when(otpService.create(Mockito.any())).thenReturn(otp);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .param("email", email))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        OtpApi executed = mapper.readValue(responseContent, OtpApi.class);

        Assert.assertEquals(executed, otp);
        Mockito.verify(otpService, Mockito.only()).create(email);
    }

    @Test
    public void shouldValidateOtpCorrectly() throws Exception {
        String email = "email@domain.com";
        String otpCode = "12345";

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH.concat("/validate"))
                .param("email", email)
                .param("otpCode", otpCode))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(otpService, Mockito.only()).validate(email, otpCode);
    }

}