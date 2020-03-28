package company.businessmanager;

import static springfox.documentation.builders.PathSelectors.regex;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableResourceServer
@EnableSwagger2
@SpringBootApplication
public class BusinessManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessManagerApplication.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public Docket swaggerUsers() {
        final String title = "Users";
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(title)
                .select()
                .paths(regex("/services/users/*.*"))
                .apis(RequestHandlerSelectors.basePackage("company.businessmanager"))
                .build()
                .apiInfo(buildApiInfo(title))
                .pathMapping("/");
    }

    private ApiInfo buildApiInfo(String title) {
        return new ApiInfoBuilder()
                .title(String.format("Rest Business Manager - %s.", title))
                .description("REST Middleware Business Manager Company.")
                .license("Apache License Version 2.0")
                .version("2.0")
                .build();
    }

}
