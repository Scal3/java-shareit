package ru.practicum.shareit;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

                    Optional<Booking> lastBookingOptional = context.getSource().stream()
                            .filter(b -> b.getBookingDateStart().isBefore(LocalDateTime.now())
                                    && !b.getStatus().equals(BookingStatus.REJECTED))
                            .min((b1, b2) -> {
                                if (b1.getBookingDateStart().isEqual(b2.getBookingDateStart())) return 0;

                                if (b1.getBookingDateStart().isBefore(b2.getBookingDateStart())) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            });

                    if (lastBookingOptional.isEmpty()) return null;

                    Booking lastBooking = lastBookingOptional.get();
                    ShortBookingDto dto = new ShortBookingDto();
                    dto.setId(lastBooking.getId());
                    dto.setBookerId(lastBooking.getUser().getId());

                    return dto;
                };

        Converter<List<Booking>, ShortBookingDto> nextBookingConverter =
                context -> {
                    if (context.getSource() == null || context.getSource().size() == 0) return null;

                    Optional<Booking> nextBookingOptional = context.getSource().stream()
                            .filter(b -> b.getBookingDateStart().isAfter(LocalDateTime.now())
                                    && !b.getStatus().equals(BookingStatus.REJECTED))
                            .min((b1, b2) -> {
                                if (b2.getBookingDateStart().isEqual(b1.getBookingDateStart())) return 0;

                                if (b2.getBookingDateStart().isBefore(b1.getBookingDateStart())) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            });

                    if (nextBookingOptional.isEmpty()) return null;

                    Booking nextBooking = nextBookingOptional.get();
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

        TypeMap<Comment, CommentDto> commentCommentDtoTypeMap =
                mapper.createTypeMap(Comment.class, CommentDto.class);

        Converter<User, String> userStringConverter =
                context -> {
                    if (context.getSource() == null) return null;

                    return context.getSource().getName();
                };

        commentCommentDtoTypeMap.addMappings(
                m -> m.using(userStringConverter)
                        .map(Comment::getUser, CommentDto::setAuthorName)
        );

        return mapper;
    }
}
