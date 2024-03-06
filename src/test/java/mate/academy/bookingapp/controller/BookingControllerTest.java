package mate.academy.bookingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.BookingRequestDto;
import mate.academy.bookingapp.dto.booking.BookingUpdateDto;
import mate.academy.bookingapp.model.Booking;
import mate.academy.bookingapp.model.User;
import mate.academy.bookingapp.service.booking.BookingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingControllerTest {
    protected static MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @DisplayName("Create a new Booking")
    @Sql(scripts = "classpath:database/accommodations/bookings/"
            + "delete-booking_by_id_1_from_bookings_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void createBooking_ValidRequestDto_Success() throws Exception {
        LocalDate checkInDate = LocalDate.now().plusDays(1);
        LocalDate checkOutDate = checkInDate.plusDays(5);
        User user = createUser(
                1L,
                "John.doe@example.com",
                "password123",
                "John",
                "Doe",
                User.Role.CUSTOMER
        );
        Long accommodationId = 1L;

        BookingRequestDto requestDto = createBookingRequestDto(
                checkInDate,
                checkOutDate,
                user,
                accommodationId
        );

        BookingDto expected = createBookingDto(
                1L,
                checkInDate,
                checkOutDate,
                user.getId(),
                accommodationId,
                "CONFIRMED"
        );

        when(bookingService.createBooking(any())).thenReturn(expected);

        MvcResult result = mockMvc.perform(
                        post("/api/bookings")
                                .content(objectMapper.writeValueAsString(requestDto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookingDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDto.class);

        assertEquals(expected, actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("Get Bookings by UserID and Status")
    @Test
    public void getBookingByUserIdAndStatus() throws Exception {
        BookingDto bookingDto1 = createBookingDto(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                1L,
                1L,
                "CONFIRMED"
        );
        BookingDto bookingDto2 = createBookingDto(
                2L,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                2L,
                2L,
                "PENDING"
        );
        List<BookingDto> expectedList = List.of(bookingDto1, bookingDto2);

        when(bookingService.getBookingsByUserIdAndStatus(
                eq(1L),
                eq(Booking.Status.CONFIRMED),
                any(Pageable.class)))
                .thenReturn(expectedList);

        MvcResult result = mockMvc.perform(get("/api/bookings")
                        .param("userId", "1")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookingDto> actualList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(expectedList, actualList);
    }

    @WithMockUser(username = "customer", roles = "CUSTOMER")
    @DisplayName("Update booking with valid data")
    @Test
    public void updateBooking_ValidData_Success() throws Exception {
        BookingDto bookingDto = createBookingDto(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                1L,
                1L,
                "PENDING"
        );

        BookingUpdateDto bookingUpdateDto = new BookingUpdateDto();
        bookingUpdateDto.setCheckInDate(LocalDate.now().plusDays(2));
        bookingUpdateDto.setCheckOutDate(LocalDate.now().plusDays(5));
        bookingUpdateDto.setStatus("CONFIRMED");

        BookingDto expected = createBookingDto(
                bookingDto.getId(),
                bookingUpdateDto.getCheckInDate(),
                bookingUpdateDto.getCheckOutDate(),
                bookingDto.getUserId(),
                bookingDto.getAccommodationId(),
                bookingUpdateDto.getStatus()
        );

        when(bookingService.updateBooking(
                bookingDto.getId(),
                bookingUpdateDto)
        ).thenReturn(expected);

        MvcResult result = mockMvc.perform(put("/api/bookings/1")
                        .content(objectMapper.writeValueAsString(bookingUpdateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDto.class);

        assertEquals(expected, actual);
    }

    @WithMockUser(username = "customer", roles = "CUSTOMER")
    @DisplayName("Get list of user's bookings")
    @Test
    public void getUserBookings_Success() throws Exception {
        BookingDto bookingDto1 = createBookingDto(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                1L,
                1L,
                "CONFIRMED"
        );
        BookingDto bookingDto2 = createBookingDto(
                2L,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                1L,
                2L,
                "PENDING"
        );

        List<BookingDto> expectedList = List.of(bookingDto1, bookingDto2);

        when(bookingService.getUserBookings(any(Authentication.class),
                any(Pageable.class))).thenReturn(expectedList);

        MvcResult result = mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookingDto> actualList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(expectedList, actualList);
    }

    @WithMockUser(username = "customer", roles = "CUSTOMER")
    @DisplayName("Get Booking By ID")
    @Test
    public void getBookingById_Success() throws Exception {
        BookingDto expected = createBookingDto(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                1L,
                1L,
                "PENDING"
        );
        when(bookingService.getBookingById(expected.getId())).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andReturn();

        BookingDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), BookingDto.class);

        assertEquals(expected, actual);
    }

    @WithMockUser(username = "manager", roles = "MANAGER")
    @DisplayName("Delete Booking by ID")
    @Test
    public void deleteBookingById_ManagerRole_Success() throws Exception {
        Long bookingId = 1L;

        mockMvc.perform(delete("/api/bookings/{id}", bookingId))
                .andExpect(status().isNoContent());

        verify(bookingService).deleteBookingById(bookingId);
    }

    private BookingDto createBookingDto(
            Long id,
            LocalDate checkIn,
            LocalDate checkOut,
            Long userId,
            Long accommodationId,
            String status
    ) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(id);
        bookingDto.setCheckInDate(checkIn);
        bookingDto.setCheckOutDate(checkOut);
        bookingDto.setUserId(userId);
        bookingDto.setAccommodationId(accommodationId);
        bookingDto.setStatus(status);

        return bookingDto;
    }

    private BookingRequestDto createBookingRequestDto(
            LocalDate checkIn,
            LocalDate checkOut,
            User user,
            Long accommodationId
    ) {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setCheckInDate(checkIn);
        requestDto.setCheckOutDate(checkOut);
        requestDto.setUser(user);
        requestDto.setAccommodationId(accommodationId);

        return requestDto;
    }

    private User createUser(
            Long id,
            String email,
            String password,
            String firstName,
            String lastName,
            User.Role role
    ) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);

        return user;
    }
}
