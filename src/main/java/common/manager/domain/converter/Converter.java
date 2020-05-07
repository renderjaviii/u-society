package common.manager.domain.converter;

import common.manager.app.api.OtpApi;
import common.manager.app.api.UserApi;
import common.manager.domain.model.Otp;
import common.manager.domain.model.User;

public class Converter {

    private Converter() {
        super();
    }

    public static UserApi user(User user) {
        return UserApi.newBuilder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .documentNumber(user.getDocumentNumber())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .lastAccessAt(user.getLastAccessAt())
                .build();
    }

    public static User user(UserApi user) {
        return User.newBuilder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .documentNumber(user.getDocumentNumber())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .lastAccessAt(user.getLastAccessAt())
                .build();
    }

    public static OtpApi otp(Otp otp) {
        return OtpApi.newBuilder()
                .id(otp.getId())
                .active(otp.isActive())
                .otpCode(otp.getOtpCode())
                .createdAt(otp.getCreatedAt())
                .expiresAt(otp.getExpiresAt())
                .ownerUsername(otp.getOwnerUsername())
                .build();
    }

}
