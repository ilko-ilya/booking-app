package mate.academy.bookingapp.service.address;

import mate.academy.bookingapp.dto.address.AddressRequestDto;
import mate.academy.bookingapp.model.Address;

public interface AddressService {
    void deleteById(Long id);

    Address save(AddressRequestDto requestDto);
}
