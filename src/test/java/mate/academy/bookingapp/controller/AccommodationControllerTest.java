//package mate.academy.bookingapp.controller;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
//import mate.academy.bookingapp.dto.accommodation.AccommodationRequestDto;
//import mate.academy.bookingapp.dto.address.AddressRequestDto;
//import mate.academy.bookingapp.model.Accommodation;
//import mate.academy.bookingapp.model.Address;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class AccommodationControllerTest {
//    protected static MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeAll
//    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(applicationContext)
//                .apply(SecurityMockMvcConfigurers.springSecurity())
//                .build();
//    }
//
//    @WithMockUser(username = "manager", roles = {"MANAGER"})
//    @DisplayName("Create a new Accommodation")
//    @Test
//    public void createAccommodation_ValidRequestDto_Success() throws Exception {
//        Address address =
//                createAddress(1L, "Italy", "Milan", "Shevchenko", "123", 5400);
//
//        AddressRequestDto addressRequestDto = createAddressRequestDto(address);
//
//        AccommodationRequestDto requestDto = new AccommodationRequestDto();
//        requestDto.setType("APARTMENT");
//        requestDto.setSize("100");
//        requestDto.setAmenities(List.of("TV", "Wi-Fi"));
//        requestDto.setLocation(addressRequestDto);
//        requestDto.setAvailability(5);
//        requestDto.setDailyRate(BigDecimal.valueOf(80));
//
//        AccommodationDto expected = new AccommodationDto();
//        expected.setId(1L);
//        expected.setType(requestDto.getType());
//        expected.setSize(requestDto.getSize());
//        expected.setAmenities(requestDto.getAmenities());
//        expected.setLocationId(1L);
//        expected.setDailyRate(requestDto.getDailyRate());
//        expected.setAvailability(requestDto.getAvailability());
//
//        String jsonRequest = objectMapper.writeValueAsString(requestDto);
//        MvcResult result = mockMvc.perform(post("/api/accommodations")
//                        .content(jsonRequest)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andReturn();
//
//        AccommodationDto actual = objectMapper
//                .readValue(result.getResponse().getContentAsString(), AccommodationDto.class);
//
//        assertNotNull(actual);
//        assertNotNull(actual.getId());
//        assertEquals(expected, actual, "id");
//    }
//
//    private Address createAddress(
//            Long id,
//            String country,
//            String city,
//            String street,
//            String addressLing,
//            Integer zipCode
//    ) {
//        Address address = new Address();
//        address.setId(id);
//        address.setCountry(country);
//        address.setCity(city);
//        address.setStreet(street);
//        address.setAddressLine(addressLing);
//        address.setZipCode(zipCode);
//
//        return address;
//    }
//
//    private AddressRequestDto createAddressRequestDto(Address address) {
//        AddressRequestDto requestDto = new AddressRequestDto();
//        requestDto.setCountry(address.getCountry());
//        requestDto.setCity(address.getCity());
//        requestDto.setStreet(address.getStreet());
//        requestDto.setAddressLine(address.getAddressLine());
//        requestDto.setZipCode(address.getZipCode());
//
//        return requestDto;
//    }
//
//    private Accommodation createAccommodation(
//            Long id,
//            Accommodation.Type type,
//            Address location,
//            String size,
//            List<String> amenities,
//            BigDecimal dailyRate,
//            Integer availability
//    ) {
//        Accommodation accommodation = new Accommodation();
//        accommodation.setId(id);
//        accommodation.setType(type);
//        accommodation.setLocation(location);
//        accommodation.setSize(size);
//        accommodation.setAmenities(amenities);
//        accommodation.setDailyRate(dailyRate);
//        accommodation.setAvailability(availability);
//
//        return accommodation;
//    }
//}
