package ru.practicum.shareit;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;


@Configuration
public class ShareItConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        TypeMap<CreateBookingDto, Booking> createBookingDtoToBookingMapper =
                mapper.createTypeMap(CreateBookingDto.class, Booking.class);

        createBookingDtoToBookingMapper.addMappings(
                m -> {
                    m.map(CreateBookingDto::getStart, Booking::setBookingDateStart);
                    m.map(CreateBookingDto::getEnd, Booking::setBookingDateEnd);
                }
        );

        TypeMap<Booking, BookingDto> bookingToBookingDtoMapper =
                mapper.createTypeMap(Booking.class, BookingDto.class);

        bookingToBookingDtoMapper.addMappings(
                m -> m.map(Booking::getUser, BookingDto::setBooker)
        );

        return mapper;
    }
}
