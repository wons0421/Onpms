package kr.co.onandon.onpms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.port}")
    private int port;

    private RedisServer redisServer;

    @PostConstruct
    void redisServer() throws Exception {
        redisServer = new RedisServer(port);
        redisServer.start();
    }

    @PreDestroy
    void stopRedis() throws Exception {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
