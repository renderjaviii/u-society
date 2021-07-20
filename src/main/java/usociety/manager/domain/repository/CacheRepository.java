package usociety.manager.domain.repository;

/**
 * Cache repository
 * It's the general representation of a cache data source
 *
 * @author Javier Ardila
 * @version 1.0
 * @see usociety.manager.domain.repository.impl.RedisCacheRepositoryImpl
 */
public interface CacheRepository {

    /**
     * It allows to retrieve a value stored into the cache data source
     *
     * @param key   Value unique identifier
     * @param clazz Value data type
     * @return Value cast
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * It allows to store a value into the cache data source
     *
     * @param key   Value unique identifier
     * @param value Value
     */
    void set(String key, Object value);

    /**
     * It allows to delete a value from the cache data source
     *
     * @param key Value unique identifier
     * @return It indicates if the value could be deleted
     */
    Boolean remove(String key);

}
