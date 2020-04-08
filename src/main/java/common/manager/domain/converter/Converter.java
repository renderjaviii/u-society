package common.manager.domain.converter;

import common.manager.app.api.UserApi;
import common.manager.domain.model.User;

public class Converter {

    public static UserApi converUser(User user) {
        return UserApi.newBuilder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .documentNumber(user.getDocumentNumber())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .lastAccessAt(user.getLastAccessAt())
                .build();
    }

    public static User converUser(UserApi user) {
        return User.newBuilder()
                .firstName(user.getFirstName())
                .documentNumber(user.getDocumentNumber())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .lastAccessAt(user.getLastAccessAt())
                .build();
    }

}
