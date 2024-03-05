package mate.academy.bookingapp.service.address;

import lombok.RequiredArgsConstructor;
import mate.academy.bookingapp.dto.address.AddressRequestDto;
import mate.academy.bookingapp.mapper.AddressMapper;
import mate.academy.bookingapp.model.Address;
import mate.academy.bookingapp.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    private AddressRepository addressRepository;
    private AddressMapper addressMapper;

    public AddressServiceImpl(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
        logger.info("AddressServiceImpl instantiated with AddressMapper: {}", addressMapper);
    }

    @Override
    public void deleteById(Long id) {
        addressRepository.deleteById(id);
        logger.info("Address with ID {} deleted successfully.", id);
    }

    @Override
    public Address save(AddressRequestDto requestDto) {
        Address address = addressMapper.toModel(requestDto);
        Address savedAddress = addressRepository.save(address);
        logger.info("Address saved successfully. ID: {}", savedAddress.getId());
        return savedAddress;
    }
}
