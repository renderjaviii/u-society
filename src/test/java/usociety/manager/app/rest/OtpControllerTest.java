package usociety.manager.app.rest;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import usociety.manager.TestUtils;
import usociety.manager.app.api.OtpApi;
import usociety.manager.app.handler.RestExceptionHandler;
import usociety.manager.domain.service.otp.OtpService;
import usociety.manager.domain.util.mapper.CustomObjectMapper;
import usociety.manager.domain.util.mapper.impl.CustomObjectMapperImpl;

@RunWith(MockitoJUnitRunner.class)
public class OtpControllerTest extends TestUtils {

    private static final CustomObjectMapper mapper = new CustomObjectMapperImpl();
    private static final String BASE_PATH = "/services/otps";

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private OtpService otpService;

    @InjectMocks
    private OtpController subject;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(subject, "httpServletRequest", httpServletRequest);

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
        Mockito.when(otpService.create(any())).thenReturn(otp);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .param("email", email))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        OtpApi executed = readMvcResultValue(mvcResult, OtpApi.class);

        Assert.assertEquals(executed, otp);
        Mockito.verify(otpService, Mockito.only()).create(email);
    }

    @Test
    public void shouldValidateOtpCorrectly() throws Exception {
        String otpCode = "12345";
        Mockito.when(httpServletRequest.getHeader(any())).thenReturn(otpCode);

        String email = "email@domain.com";
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH.concat("/validate"))
                .param("email", email))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(otpService, Mockito.only()).validate(email, otpCode);
        Mockito.verify(httpServletRequest).getHeader("otpCode");
    }

}