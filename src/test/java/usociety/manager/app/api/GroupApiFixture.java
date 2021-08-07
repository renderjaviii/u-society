package usociety.manager.app.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javafaker.Faker;

public class GroupApiFixture {

    public static GroupApi defaultValue;

    public static List<String> objectives;
    public static CategoryApi category;
    public static String description;
    public static List<String> rules;
    public static String photo;
    public static String name;
    public static String slug;
    public static Long id;

    static {
        Faker faker = new Faker();

        slug = faker.internet().slug(Arrays.asList("fake", "group", "name"), "-");
        category = new CategoryApi(123L, "Category ABC");
        objectives = Arrays.asList("Main", "Secondary");
        rules = Collections.singletonList("Important");
        id = faker.number().numberBetween(1L, 100);
        description = "Group description";
        photo = faker.avatar().image();
        name = "Fake group name";

        defaultValue = GroupApi.newBuilder()
                .description(description)
                .objectives(objectives)
                .category(category)
                .rules(rules)
                .photo(photo)
                .slug(slug)
                .name(name)
                .id(id)
                .build();
    }
}