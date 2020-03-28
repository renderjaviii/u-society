package company.businessmanager.domain.converter;

import company.businessmanager.app.api.UserApi;
import company.businessmanager.domain.model.User;

public class Converter {

    public static UserApi converUser(User user) {
        return UserApi.newBuilder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .lastAccessAt(user.getLastAccessAt())
                .build();
    }

    public static User converUser(UserApi user) {
        return User.newBuilder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .lastAccessAt(user.getLastAccessAt())
                .build();
    }

}
