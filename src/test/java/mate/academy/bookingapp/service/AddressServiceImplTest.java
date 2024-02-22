package mate.academy.bookingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import mate.academy.bookingapp.dto.address.AddressRequestDto;
import mate.academy.bookingapp.mapper.AddressMapper;
import mate.academy.bookingapp.model.Address;
import mate.academy.bookingapp.repository.AddressRepository;
import mate.academy.bookingapp.service.address.AddressServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private AddressMapper addressMapper;
    @InjectMocks
    private AddressServiceImpl addressService;

    @DisplayName("Create an address with valid date")
    @Test
    public void saveAddress_WithValidDate_ShouldReturnAddress() {
        AddressRequestDto requestDto = new AddressRequestDto(/* заповніть значення для тесту */);
        requestDto.setCountry("Ukraine");
        requestDto.setCity("Kyiv");
        requestDto.setStreet("Main Street");
        requestDto.setAddressLine("Apt 123");
        requestDto.setZipCode(12345);

        Address mappedAddress = new Address();
        mappedAddress.setCountry("Ukraine");
        mappedAddress.setCity("Kyiv");
        mappedAddress.setStreet("Main Street");
        mappedAddress.setAddressLine("Apt 123");
        mappedAddress.setZipCode(12345);

        when(addressMapper.toModel(requestDto)).thenReturn(mappedAddress);
        when(addressRepository.save(mappedAddress)).thenReturn(mappedAddress);

        Address result = addressService.save(requestDto);

        assertEquals(mappedAddress, result);
        verify(addressMapper, times(1)).toModel(requestDto);
        verify(addressRepository, times(1)).save(mappedAddress);
    }

    @DisplayName("Delete an address by ID")
    @Test
    public void deleteByValidId_ShouldDeleteAddressSuccessfully() {
        Long addressIdToDelete = 1L;

        addressService.deleteById(addressIdToDelete);

        verify(addressRepository, times(1)).deleteById(addressIdToDelete);
    }
}
