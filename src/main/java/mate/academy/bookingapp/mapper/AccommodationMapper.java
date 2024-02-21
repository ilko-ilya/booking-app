package mate.academy.bookingapp.mapper;

import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.dto.accommodation.AccommodationDto;
import mate.academy.bookingapp.dto.accommodation.AccommodationRequestDto;
import mate.academy.bookingapp.dto.address.AddressRequestDto;
import mate.academy.bookingapp.model.Accommodation;
import mate.academy.bookingapp.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = AddressMapper.class)
public interface AccommodationMapper {

    @Mapping(source = "location.id", target = "locationId")
    AccommodationDto toDto(Accommodation accommodation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(source = "location", target = "location", qualifiedByName = "mapRequestDtoToAddress")
    Accommodation toModel(AccommodationRequestDto requestDto);

    @Named("mapRequestDtoToAddress")
    default Address mapRequestDtoToAddress(AddressRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        Address address = new Address();
        address.setCountry(requestDto.getCountry());
        address.setCity(requestDto.getCity());
        address.setStreet(requestDto.getStreet());
        address.setAddressLine(requestDto.getAddressLine());
        address.setZipCode(requestDto.getZipCode());
        address.setDeleted(false);

        return address;
    }

    default Long mapAddressToLong(Address location) {
        return (location != null) ? location.getId() : null;
    }

}
