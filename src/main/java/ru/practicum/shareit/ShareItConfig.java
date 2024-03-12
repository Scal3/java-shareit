package ru.practicum.shareit;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;


@Configuration
public class ShareItConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        TypeMap<CreateBookingDto, Booking> createBookingDtoToBookingTypeMap =
                mapper.createTypeMap(CreateBookingDto.class, Booking.class);

        createBookingDtoToBookingTypeMap.addMappings(
                m -> {
                    m.map(CreateBookingDto::getStart, Booking::setBookingDateStart);
                    m.map(CreateBookingDto::getEnd, Booking::setBookingDateEnd);
                    m.skip(Booking::setId);
                }
        );

        TypeMap<Booking, BookingDto> bookingToBookingDtoTypeMap =
                mapper.createTypeMap(Booking.class, BookingDto.class);

        bookingToBookingDtoTypeMap.addMappings(
                m -> m.map(Booking::getUser, BookingDto::setBooker)
        );

        TypeMap<Item, ItemDtoWithBooking> itemItemDtoWithBookingTypeMap =
                mapper.createTypeMap(Item.class, ItemDtoWithBooking.class);

        Converter<List<Booking>, ShortBookingDto> lastBookingConverter =
                context -> {
                    if (context.getSource() == null || context.getSource().size() == 0) return null;

                    Booking lastBooking = context.getSource().stream()
                            .filter(b -> b.getBookingDateEnd().isBefore(LocalDateTime.now()))
                            .min((b1, b2) -> {
                                if (b2.getBookingDateEnd().isEqual(b1.getBookingDateEnd())) return 0;

                                if (b2.getBookingDateEnd().isBefore(b1.getBookingDateEnd())) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            })
                            .orElseThrow(() -> new RuntimeException("lastBookingConverter problem"));

                    ShortBookingDto dto = new ShortBookingDto();
                    dto.setId(lastBooking.getId());
                    dto.setBookerId(lastBooking.getUser().getId());

                    return dto;
                };

        Converter<List<Booking>, ShortBookingDto> nextBookingConverter =
                context -> {
                    if (context.getSource() == null || context.getSource().size() == 0) return null;

                    Booking nextBooking = context.getSource().stream()
                            .filter(b -> b.getBookingDateStart().isAfter(LocalDateTime.now()))
                            .min((b1, b2) -> {
                                if (b2.getBookingDateStart().isEqual(b1.getBookingDateStart())) return 0;

                                if (b2.getBookingDateStart().isBefore(b1.getBookingDateStart())) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            })
                            .orElseThrow(() -> new RuntimeException("nextBookingConverter problem"));

                    ShortBookingDto dto = new ShortBookingDto();
                    dto.setId(nextBooking.getId());
                    dto.setBookerId(nextBooking.getUser().getId());

                    return dto;
                };

        itemItemDtoWithBookingTypeMap.addMappings(
                m -> m.using(lastBookingConverter)
                        .map(Item::getBookings, ItemDtoWithBooking::setLastBooking)
        );

        itemItemDtoWithBookingTypeMap.addMappings(
                m -> m.using(nextBookingConverter)
                        .map(Item::getBookings, ItemDtoWithBooking::setNextBooking)
        );

        return mapper;
    }
}
