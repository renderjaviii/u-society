package common.manager.domain.converter;

import org.modelmapper.ModelMapper;

import common.manager.app.api.OtpApi;
import common.manager.app.api.TokenApi;
import common.manager.app.api.UserApi;
import common.manager.domain.model.Otp;
import common.manager.domain.provider.authentication.dto.TokenDTO;
import common.manager.domain.provider.user.dto.UserDTO;

public class Converter {

    private static final ModelMapper modelMapper = new ModelMapper();

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
        return modelMapper.map(user, UserApi.class);
    }

    public static UserDTO user1(UserDTO user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public static OtpApi otp(Otp otp) {
        return modelMapper.map(otp, OtpApi.class);
    }

}
