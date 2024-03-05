package ru.practicum.shareit;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShareItConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
