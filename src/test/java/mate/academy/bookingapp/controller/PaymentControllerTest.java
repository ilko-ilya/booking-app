package mate.academy.bookingapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import mate.academy.bookingapp.dto.payment.CancelledPaymentResponseDto;
import mate.academy.bookingapp.dto.payment.PaymentDto;
import mate.academy.bookingapp.dto.payment.SuccessfulPaymentResponseDto;
import mate.academy.bookingapp.service.payment.PaymentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;
    @MockBean
    private PaymentService paymentService;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(value = "customer", roles = "CUSTOMER")
    @DisplayName("Get payments for user")
    @Test
    public void getPaymentsForUser_ValidParameters_Success() throws Exception {
        Long userId = 1L;

        PaymentDto paymentDto1 = createPaymentDto(
                1L,
                "PENDING",
                1L,
                new URL("https://example.com/session1"),
                "session1",
                BigDecimal.valueOf(200)
        );

        PaymentDto paymentDto2 = createPaymentDto(
                2L,
                "PAID",
                2L,
                new URL("https://example.com/session2"),
                "session2",
                BigDecimal.valueOf(300)
        );

        List<PaymentDto> expectedPayments = List.of(paymentDto1, paymentDto2);

        when(paymentService.getPaymentsForUser(eq(userId), any(Pageable.class)))
                .thenReturn(expectedPayments);

        mockMvc.perform(get("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId))
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "UNSORTED"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(expectedPayments.size())))
                .andExpect(jsonPath("$[0].id")
                        .value(expectedPayments.get(0).getId()))
                .andExpect(jsonPath("$[0].status")
                        .value(expectedPayments.get(0).getStatus()))
                .andExpect(jsonPath("$[0].bookingId")
                        .value(expectedPayments.get(0).getBookingId()))
                .andExpect(jsonPath("$[0].sessionUrl")
                        .value(expectedPayments.get(0).getSessionUrl().toString()))
                .andExpect(jsonPath("$[0].sessionId")
                        .value(expectedPayments.get(0).getSessionId()))
                .andExpect(jsonPath("$[0].amountToPay")
                        .value(expectedPayments.get(0).getAmountToPay()))
                .andExpect(jsonPath("$[1].id")
                        .value(expectedPayments.get(1).getId()))
                .andExpect(jsonPath("$[1].status")
                        .value(expectedPayments.get(1).getStatus()))
                .andExpect(jsonPath("$[1].bookingId")
                        .value(expectedPayments.get(1).getBookingId()))
                .andExpect(jsonPath("$[1].sessionUrl")
                        .value(expectedPayments.get(1).getSessionUrl().toString()))
                .andExpect(jsonPath("$[1].sessionId")
                        .value(expectedPayments.get(1).getSessionId()))
                .andExpect(jsonPath("$[1].amountToPay")
                        .value(expectedPayments.get(1).getAmountToPay()));

        verify(paymentService).getPaymentsForUser(eq(userId), any(Pageable.class));
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @DisplayName("Handle Successful Payment - Success")
    @Test
    public void handleSuccessfulPayment_Success() throws Exception {
        String paymentId = "validPaymentId";
        SuccessfulPaymentResponseDto expectedResponse = new SuccessfulPaymentResponseDto();
        expectedResponse.setPaymentId(paymentId);
        expectedResponse.setStatus("PAID");
        expectedResponse.setSessionUrl("http://example.com/session");

        when(paymentService.handleSuccessfulPayment(paymentId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/payments/success")
                        .param("paymentId", paymentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus()))
                .andExpect(jsonPath("$.paymentId").value(expectedResponse.getPaymentId()))
                .andExpect(jsonPath("$.sessionUrl").value(expectedResponse.getSessionUrl()));
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @DisplayName("Handle Cancelled Payment - Success")
    @Test
    public void handleCancelledPayment_Success() throws Exception {
        String paymentId = "cancelledPaymentId";
        CancelledPaymentResponseDto expectedResponse = new CancelledPaymentResponseDto();
        expectedResponse.setPaymentId(paymentId);
        expectedResponse.setStatus("CANCELLED");

        when(paymentService.handleCancelledPayment(paymentId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/payments/cancel")
                        .param("paymentId", paymentId))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus()))
                .andExpect(jsonPath("$.paymentId").value(expectedResponse.getPaymentId()));
    }

    private PaymentDto createPaymentDto(
            Long id,
            String status,
            Long bookingId,
            URL sessionUrl,
            String sessionId,
            BigDecimal amountToPay
    ) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(id);
        paymentDto.setStatus(status);
        paymentDto.setBookingId(bookingId);
        paymentDto.setSessionUrl(sessionUrl);
        paymentDto.setSessionId(sessionId);
        paymentDto.setAmountToPay(amountToPay);

        return paymentDto;
    }
}
