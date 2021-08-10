package usociety.manager.app.api;

import static java.lang.Boolean.TRUE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.javafaker.Faker;

import usociety.manager.domain.enums.UserTypeEnum;

public class UserApiFixture {

    private static UserApi defaultValue;

    public static List<CategoryApi> categoryList;
    public static LocalDateTime lastAccessAt;
    public static CategoryApi category;
    public static LocalDate createdAt;
    public static String username;
    public static String password;
    public static String email;
    public static String photo;
    public static String role;
    public static String name;
    public static Long id;

    static {
        Faker faker = new Faker();

        Date date = faker.date().past(faker.number().numberBetween(1, 28), TimeUnit.DAYS);
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

        password = faker.internet().password(8, 20, TRUE, TRUE, TRUE);

        category = new CategoryApi(17L, faker.job().field());
        categoryList = Collections.singletonList(category);

        id = faker.number().numberBetween(1L, 100L);
        createdAt = dateTime.toLocalDate().plusDays(1);
        email = faker.internet().emailAddress();
        role = UserTypeEnum.STANDARD.getValue();
        username = faker.name().username();
        photo = faker.internet().avatar();
        name = faker.name().fullName();
        lastAccessAt = dateTime;

        defaultValue = UserApi.newBuilder()
                .categoryList(Collections.emptyList())
                .lastAccessAt(lastAccessAt)
                .createdAt(createdAt)
                .username(username)
                .photo(photo)
                .email(email)
                .role(role)
                .name(name)
                .id(id)
                .build();

    }

    public static UserApi value() {
        return defaultValue;
    }

}