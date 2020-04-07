package common.manager.app.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application")
public class DatabasePropsReader implements BeanPostProcessor {

    private String configTableName;

    private final ConfigurableEnvironment environment;

    @Autowired
    public DatabasePropsReader(ConfigurableEnvironment environment,
                               @Value("${config.table}") String configTableName) {
        this.environment = environment;
        this.configTableName = configTableName;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        if (bean instanceof DataSource) {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            Connection connection = DataSourceUtils.getConnection((DataSource) bean);

            Map<String, Object> propertySource = new HashMap<>();
            if (StringUtils.isNotEmpty(configTableName)) {
                try {
                    preparedStatement = connection.prepareStatement("SELECT name, value FROM " + configTableName);
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        String propName = resultSet.getString("name");
                        propertySource.put(propName, resultSet.getString("value"));
                    }
                    environment.getPropertySources().addFirst(new MapPropertySource("application", propertySource));
                } catch (SQLException ignored) {
                } finally {
                    try {
                        Objects.requireNonNull(preparedStatement).close();
                        Objects.requireNonNull(resultSet).close();
                    } catch (SQLException ignored) {
                    }
                }
            }

        }
        return bean;
    }

}