package mate.academy.bookingapp.mapper.booking;

import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.BookingRequestDto;
import mate.academy.bookingapp.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    @Mapping(source = "booking.id", target = "id")
    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "user.id", target = "userId")
    BookingDto toDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "accommodation.id", source = "accommodationId")
    @Mapping(target = "deleted", ignore = true)
    Booking toModel(BookingRequestDto requestDto);
}
