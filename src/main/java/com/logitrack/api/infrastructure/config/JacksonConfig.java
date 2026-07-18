package com.logitrack.api.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 💡 Toque Sênior: Registra o módulo para suportar as novas classes de data do Java 8+ (LocalDateTime, etc.)
        // Sem isso, o Jackson falha ao serializar datas em APIs corporativas europeias.
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }
}