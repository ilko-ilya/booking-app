package mate.academy.bookingapp.mapper.booking;

import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.dto.booking.BookingDto;
import mate.academy.bookingapp.dto.booking.BookingRequestDto;
import mate.academy.bookingapp.model.Booking;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    BookingDto toDto(Booking booking);

    Booking toModel(BookingRequestDto requestDto);
}
