package usociety.manager.app.rest;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;

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

import usociety.manager.app.api.ApiError;
import usociety.manager.app.api.PaymentApi;
import usociety.manager.app.handler.RestExceptionHandler;
import usociety.manager.domain.enums.CardTypeEnum;
import usociety.manager.domain.enums.DocumentTypeEnum;
import usociety.manager.domain.service.payment.PaymentService;

@RunWith(MockitoJUnitRunner.class)
public class PaymentControllerTest extends TestUtils {

    private static final String BASE_PATH = "/services/payments";

    @Mock
    private SecurityContext securityContext;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController subject;

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
    public void shouldCreateCardPaymentCorrectly() throws Exception {
        PaymentApi request = PaymentApi.CardPaymentApi.newBuilder()
                .amount(new BigDecimal("1000.2"))
                .documentType(DocumentTypeEnum.CC)
                .nameOnTheCard("Name on the card")
                .cardNumber("1234432145677654")
                .cardType(CardTypeEnum.CREDIT)
                .documentNumber("123456789")
                .cvv("1234")
                .quotes(1)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(paymentService, Mockito.only()).create(USERNAME, request);
    }

    @Test
    public void shouldCreatePSEPaymentCorrectly() throws Exception {
        PaymentApi request = PaymentApi.PSEPaymentApi.newBuilder()
                .amount(new BigDecimal("2500"))
                .documentType(DocumentTypeEnum.NIT)
                .pseEmail("example@email.com")
                .documentNumber("123456789")
                .pseBankCode(1)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(paymentService, Mockito.only()).create(USERNAME, request);
    }

    @Test
    public void shouldFailCreatingCardPaymentDueToInvalidRequest() throws Exception {
        PaymentApi request = PaymentApi.CardPaymentApi.newBuilder()
                .amount(new BigDecimal("1.234"))
                .nameOnTheCard("#12")
                .cardNumber("1122334455")
                .documentNumber("123456789.")
                .cvv("12")
                .quotes(0)
                .build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        ApiError executed = readMvcResultValue(mvcResult, ApiError.class);

        String errorDescription = executed.getDescription();
        Assert.assertTrue(errorDescription.contains("documentNumber must match \"[\\d+]{7,11}\""));
        Assert.assertTrue(errorDescription.contains("quotes must be greater than or equal to 1"));
        Assert.assertTrue(errorDescription.contains("nameOnTheCard must be alphanumeric"));
        Assert.assertTrue(errorDescription.contains("cardNumber must match \"[\\d+]{16}"));
        Assert.assertTrue(errorDescription.contains("documentType must not be null"));
        Assert.assertTrue(errorDescription.contains("cvv must match \"[\\d+]{4}\""));
        Assert.assertTrue(errorDescription.contains("cardType must not be null"));
        Assert.assertTrue(errorDescription
                .contains("amount numeric value out of bounds (<17 digits>.<2 digits> expected)"));

        Mockito.verifyNoInteractions(paymentService);
    }

    @Test
    public void shouldFailCreatingPSEPaymentDueToInvalidRequest() throws Exception {
        PaymentApi request = PaymentApi.PSEPaymentApi.newBuilder()
                .pseEmail("invalid-email")
                .documentNumber("901")
                .pseBankCode(0)
                .build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        ApiError executed = readMvcResultValue(mvcResult, ApiError.class);

        String errorDescription = executed.getDescription();
        Assert.assertTrue(errorDescription.contains("pseBankCode must be greater than or equal to 1"));
        Assert.assertTrue(errorDescription.contains("pseEmail must be a well-formed email address"));
        Assert.assertTrue(errorDescription.contains("documentNumber must match \"[\\d+]{7,11}\""));
        Assert.assertTrue(errorDescription.contains("documentType must not be null"));
        Assert.assertTrue(errorDescription.contains("amount must not be null"));

        Mockito.verifyNoInteractions(paymentService);
    }

}