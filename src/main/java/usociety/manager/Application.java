package usociety.manager;

import static springfox.documentation.builders.PathSelectors.regex;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableAsync
@EnableSwagger2
@SpringBootApplication
public class Application {

    private static final String BASE_PACKAGE = Application.class.getPackage().getName();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
                .build()
                .apiInfo(buildApiInfo(title))
                .pathMapping("/");
    }

    @Bean
    public Docket swaggerOTP() {
        final String title = "OTP";
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(title)
                .select()
                .paths(regex("/services/otp/*.*"))
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
                .build()
                .apiInfo(buildApiInfo(title))
                .pathMapping("/");
    }

    @Bean
    public Docket swaggerCategory() {
        final String title = "Category";
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(title)
                .select()
                .paths(regex("/services/categories/*.*"))
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
                .build()
                .apiInfo(buildApiInfo(title))
                .pathMapping("/");
    }

    @Bean
    public Docket swaggerGroup() {
        final String title = "Group";
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(title)
                .select()
                .paths(regex("/services/groups/*.*"))
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
                .build()
                .apiInfo(buildApiInfo(title))
                .pathMapping("/");
    }

    @Bean
    public Docket swaggerMessage() {
        final String title = "Message";
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(title)
                .select()
                .paths(regex("/services/messages/*.*"))
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
                .build()
                .apiInfo(buildApiInfo(title))
                .pathMapping("/");
    }

    @Bean
    public Docket swaggerPost() {
        final String title = "Post";
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(title)
                .select()
                .paths(regex("/services/posts/*.*"))
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
                .build()
                .apiInfo(buildApiInfo(title))
                .pathMapping("/");
    }

    private ApiInfo buildApiInfo(String title) {
        return new ApiInfoBuilder()
                .title(String.format("Rest Manager Base - %s.", title))
                .description("REST Middleware Manager Base.")
                .version("1.0")
                .build();
    }

}

