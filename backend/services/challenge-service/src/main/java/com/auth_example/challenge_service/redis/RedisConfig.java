package com.auth_example.challenge_service.redis;

import com.auth_example.challenge_service.user.models.UserEntry;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UserEntry> userEntryTemplate(RedisConnectionFactory factory) {
        return buildTemplate(factory, UserEntry.class);
    }

    private <T> RedisTemplate<String, T> buildTemplate(RedisConnectionFactory factory, Class<T> classType) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Configure ObjectMapper to handle Java time types properly
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()) // enable support for Instant, LocalDateTime, etc
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // Makes date readable (ISO)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // serialize value into JSON for readability and structure
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, classType);

        // Keys are always string in Redis - StringRedisSerializer ensures UTF-8 encoding
        template.setKeySerializer(new StringRedisSerializer());

        // Values are serialized to JSON using the Jackson serializer
        template.setValueSerializer(serializer);
        template.setDefaultSerializer(serializer); // set default in case other serializers are used internally

        // must call this to initialize the template after setting all serializer
        template.afterPropertiesSet();

        return template;
    }
}
