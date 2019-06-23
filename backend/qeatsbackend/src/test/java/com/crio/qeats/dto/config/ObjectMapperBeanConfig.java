package com.crio.qeats.dto.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

// Helper class if you want to use it.
@Configuration
public class ObjectMapperBeanConfig {
  @Bean
  @Scope("prototype")
  @Primary
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}