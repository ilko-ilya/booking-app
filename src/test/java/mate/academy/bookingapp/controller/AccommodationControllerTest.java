package mate.academy.bookingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationRequestDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationUpdateDto;
import mate.academy.bookingapp.dto.address.AddressRequestDto;
import mate.academy.bookingapp.model.Address;
import mate.academy.bookingapp.service.accommodation.AccommodationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccommodationControllerTest {
    protected static MockMvc mockMvc;
    @MockBean
    private AccommodationService accommodationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("Create a new Accommodation")
    @Sql(scripts = "classpath:database/accommodations/"
            + "delete-accommodation_by_id_4_from-accommodations_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void createAccommodation_ValidRequestDto_Success() throws Exception {

        Address address = createAddress(
                4L,
                "Italy",
                "Milan",
                "Shevchenko",
                "123",
                5400
        );

        AddressRequestDto addressRequestDto = createAddressRequestDto(address);

        AccommodationRequestDto requestDto = new AccommodationRequestDto();
        requestDto.setType("APARTMENT");
        requestDto.setSize("100");
        requestDto.setAmenities(List.of("TV", "Wi-Fi"));
        requestDto.setLocation(addressRequestDto);
        requestDto.setAvailability(5);
        requestDto.setDailyRate(BigDecimal.valueOf(80));

        AccommodationDto expected = new AccommodationDto();
        expected.setId(4L);
        expected.setType(requestDto.getType());
        expected.setSize(requestDto.getSize());
        expected.setAmenities(requestDto.getAmenities());
        expected.setLocationId(address.getId());
        expected.setDailyRate(requestDto.getDailyRate());
        expected.setAvailability(requestDto.getAvailability());

        when(accommodationService.create(any())).thenReturn(expected);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/api/accommodations")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        AccommodationDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), AccommodationDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected, actual, "id");
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @DisplayName("Create a new Accommodation - Unauthorized Role")
    @Test
    public void createAccommodation_unAuthorizedRole() throws Exception {

        Address address = createAddress(
                4L,
                "Italy",
                "Milan",
                "Shevchenko",
                "123",
                5400
        );

        AddressRequestDto addressRequestDto = createAddressRequestDto(address);

        AccommodationRequestDto requestDto = new AccommodationRequestDto();
        requestDto.setType("APARTMENT");
        requestDto.setSize("100");
        requestDto.setAmenities(List.of("TV", "Wi-Fi"));
        requestDto.setLocation(addressRequestDto);
        requestDto.setAvailability(5);
        requestDto.setDailyRate(BigDecimal.valueOf(80));

        AccommodationDto expected = new AccommodationDto();
        expected.setId(4L);
        expected.setType(requestDto.getType());
        expected.setSize(requestDto.getSize());
        expected.setAmenities(requestDto.getAmenities());
        expected.setLocationId(address.getId());
        expected.setDailyRate(requestDto.getDailyRate());
        expected.setAvailability(requestDto.getAvailability());

        when(accommodationService.create(any())).thenReturn(expected);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/accommodations")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "test", password = "test", roles = {"CUSTOMER", "MANAGER"})
    @DisplayName("Get all Accommodations")
    @Sql(scripts = "classpath:database/accommodations/"
            + "delete_accommodation_by_id_from_4_to_6_from_accommodations_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void getAllAccommodations_Success() throws Exception {
        List<AccommodationDto> expectedList = List.of(
                createAccommodationDto(
                        4L,
                        "HOUSE",
                        "150",
                        List.of("Wi-Fi"),
                        2L,
                        BigDecimal.valueOf(100),
                        8),
                createAccommodationDto(
                        5L,
                        "APARTMENT",
                        "100",
                        List.of("Swimming pool"),
                        1L,
                        BigDecimal.valueOf(70),
                        5),
                createAccommodationDto(
                        6L,
                        "CONDO",
                        "90",
                        List.of("TV"),
                        3L,
                        BigDecimal.valueOf(120),
                        7)
        );

        when(accommodationService.getAll(any())).thenReturn(expectedList);

        Pageable pageable = PageRequest.of(0, 10);

        MvcResult mvcResult =
                mockMvc.perform(get("/api/accommodations").contentType(MediaType.APPLICATION_JSON)
                                .param("page", String.valueOf(
                                        pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize()))
                        ).andExpect(status().isOk())
                        .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<AccommodationDto> actual = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertEquals(expectedList, actual);
    }

    @WithMockUser(username = "test", password = "test", roles = {"CUSTOMER", "MANAGER"})
    @DisplayName("Get Accommodation by accommodationID")
    @Sql(scripts = "classpath:database/accommodations/"
            + "delete-accommodation_by_id_4_from-accommodations_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void getAccommodationById_Success() throws Exception {
        AccommodationDto expected = createAccommodationDto(
                4L,
                "HOUSE",
                "150",
                List.of("Air conditioner"),
                2L,
                BigDecimal.valueOf(150),
                8
        );

        when(accommodationService.getById(expected.getId())).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/api/accommodations/{id}", 4L))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AccommodationDto.class);

        assertEquals(expected, actual);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("Delete Accommodation by valid ID")
    @Test
    public void deleteAccommodationById_Success() throws Exception {
        mockMvc.perform(delete("/api/accommodations/{id}", 4L))
                .andExpect(status().isNoContent());

        verify(accommodationService, times(1)).deleteById(4L);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @DisplayName("Delete an Accommodation by Non Existing accommodationID")
    @Test
    public void deleteAccommodationByNonExistingId_NotFound() throws Exception {
        mockMvc.perform(delete("/api/accommodations/{id}", 999L))
                .andExpect(status().isNoContent());

        verify(accommodationService).deleteById(999L);
    }

    @WithMockUser(username = "manager", roles = "MANAGER")
    @DisplayName("Update Accommodation")
    @Test
    public void updateAccommodation_Success() throws Exception {
        final Long accommodationId = 1L;

        AccommodationUpdateDto updateDto = new AccommodationUpdateDto();
        updateDto.setAmenities(List.of("New coach"));
        updateDto.setAvailability(3);
        updateDto.setDailyRate(BigDecimal.valueOf(300));

        when(accommodationService.update(accommodationId, updateDto))
                .thenReturn(createUpdatedAccommodationDto(accommodationId, updateDto));

        MvcResult result = mockMvc.perform(patch("/api/accommodations/{id}", accommodationId)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class);

        assertEquals(createUpdatedAccommodationDto(accommodationId, updateDto), actual);
    }

    private AccommodationDto createUpdatedAccommodationDto(
            Long id,
            AccommodationUpdateDto updateDto
    ) {
        return createAccommodationDto(
                id,
                "APARTMENT",
                "100",
                updateDto.getAmenities(),
                1L, updateDto.getDailyRate(), updateDto.getAvailability());
    }

    private AccommodationDto createAccommodationDto(
            Long id,
            String type,
            String size,
            List<String> amenities,
            Long locationId,
            BigDecimal dailyRate,
            Integer availability
    ) {
        AccommodationDto accommodationDto = new AccommodationDto();
        accommodationDto.setId(id);
        accommodationDto.setType(type);
        accommodationDto.setSize(size);
        accommodationDto.setAmenities(amenities);
        accommodationDto.setLocationId(locationId);
        accommodationDto.setDailyRate(dailyRate);
        accommodationDto.setAvailability(availability);

        return accommodationDto;
    }

    private Address createAddress(
            Long id,
            String country,
            String city,
            String street,
            String addressLing,
            Integer zipCode
    ) {
        Address address = new Address();
        address.setId(id);
        address.setCountry(country);
        address.setCity(city);
        address.setStreet(street);
        address.setAddressLine(addressLing);
        address.setZipCode(zipCode);

        return address;
    }

    private AddressRequestDto createAddressRequestDto(Address address) {
        AddressRequestDto requestDto = new AddressRequestDto();
        requestDto.setCountry(address.getCountry());
        requestDto.setCity(address.getCity());
        requestDto.setStreet(address.getStreet());
        requestDto.setAddressLine(address.getAddressLine());
        requestDto.setZipCode(address.getZipCode());

        return requestDto;
    }
}
