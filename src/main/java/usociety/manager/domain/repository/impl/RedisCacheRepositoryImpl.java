package usociety.manager.domain.repository.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

import usociety.manager.domain.repository.CacheRepository;

@Repository
@Qualifier("redisRepository")
public class RedisCacheRepositoryImpl implements CacheRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheRepositoryImpl.class);

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> template;

    @PostConstruct
    private void init() {
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = template.opsForValue().get(key);
        try {
            return clazz.cast(value);
        } catch (ClassCastException ex) {
            LOGGER.error("Error getting cache value", ex);
        }
        return null;
    }

    @Override
    public void set(String key, Object value) {
        template.opsForValue().set(key, value);
    }

    @Override
    public Boolean remove(String key) {
        return template.delete(key);
    }

}
