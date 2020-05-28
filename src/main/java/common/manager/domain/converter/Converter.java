package common.manager.domain.converter;

import common.manager.app.api.OtpApi;
import common.manager.app.api.TokenApi;
import common.manager.app.api.UserApi;
import common.manager.domain.model.Otp;
import common.manager.domain.provider.authentication.dto.TokenDTO;
import common.manager.domain.provider.authentication.dto.UserDTO;

public class Converter {

    private Converter() {
        super();
    }

    public static TokenApi token(TokenDTO tokenDTO) {
        return TokenApi.newBuilder()
                .refreshToken(tokenDTO.getRefreshToken())
                .accessToken(tokenDTO.getAccessToken())
                .expiresIn(tokenDTO.getExpiresIn())
                .tokenType(tokenDTO.getTokenType())
                .scope(tokenDTO.getScope())
                .jti(tokenDTO.getJti())
                .build();
    }

    public static UserApi user(UserDTO user) {
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

    public static UserDTO user(UserApi user) {
        return UserDTO.newBuilder()
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
