package com.crio.qeats.config;

import static com.crio.qeats.globals.GlobalConstants.REDIS_HOST;
import static com.crio.qeats.globals.GlobalConstants.REDIS_PORT;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisConfiguration {

  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration configuration
        = new RedisStandaloneConfiguration(REDIS_HOST, REDIS_PORT);
    JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
        .usePooling().build();
    JedisConnectionFactory factory
        = new JedisConnectionFactory(configuration,jedisClientConfiguration);
    factory.afterPropertiesSet();

    return factory;
  }

  @Bean
  RedisTemplate<Object, Object> redisTemplate() {
    RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
    redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
    redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
    redisTemplate.setConnectionFactory(jedisConnectionFactory());

    return redisTemplate;
  }
}
