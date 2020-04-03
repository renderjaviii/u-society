package common.manager;

import static springfox.documentation.builders.PathSelectors.regex;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class CommonManager {

    public static void main(String[] args) {
        SpringApplication.run(CommonManager.class, args);
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

