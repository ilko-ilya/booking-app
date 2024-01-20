package mate.academy.bookingapp.mapper.payment;

import mate.academy.bookingapp.config.MapperConfig;
import mate.academy.bookingapp.dto.payment.PaymentDto;
import mate.academy.bookingapp.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);
}
