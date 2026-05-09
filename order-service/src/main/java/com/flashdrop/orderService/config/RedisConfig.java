package com.flashdrop.orderService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }


    @Bean
    public RedisScript<Long> checkAndDeductScript(){
        ClassPathResource script = new ClassPathResource("scripts/check-and-deduct.lua");
        return RedisScript.of(script, Long.class);
    }

    @Bean
    public RedisScript<String> preloadStockScript() {
        return RedisScript.of(
                new ClassPathResource("scripts/preload-stock.lua"),
                String.class
        );
    }
}
