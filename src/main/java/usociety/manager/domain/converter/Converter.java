package usociety.manager.domain.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.app.api.CategoryApi;
import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.OtpApi;
import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.TokenApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.Otp;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.provider.authentication.dto.TokenDTO;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.service.post.dto.PostAdditionalData;
import usociety.manager.domain.util.mapper.CustomObjectMapper;
import usociety.manager.domain.util.mapper.impl.CustomObjectMapperImpl;

public class Converter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Converter.class);

    private static CustomObjectMapper objectMapper = new CustomObjectMapperImpl();

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
                .lastAccessAt(user.getLastAccessAt())
                .createdAt(user.getCreatedAt())
                .username(user.getUsername())
                .photo(user.getPhoto())
                .email(user.getEmail())
                .name(user.getName())
                .id(user.getId())
                .build();
    }

    public static UserDTO user(UserApi user) {
        return UserDTO.newBuilder()
                .lastAccessAt(user.getLastAccessAt())
                .createdAt(user.getCreatedAt())
                .username(user.getUsername())
                .photo(user.getPhoto())
                .email(user.getEmail())
                .name(user.getName())
                .id(user.getId())
                .build();
    }

    public static OtpApi otp(Otp otp) {
        return OtpApi.newBuilder()
                .userEmailOwner(otp.getEmailOwner())
                .usernameOwner(otp.getUsernameOwner())
                .createdAt(otp.getCreatedAt())
                .expiresAt(otp.getExpiresAt())
                .otpCode(otp.getOtpCode())
                .active(otp.isActive())
                .id(otp.getId())
                .build();
    }

    public static CategoryApi category(Category category) {
        return new CategoryApi(category.getId(), category.getName());
    }

    public static GroupApi group(Group group) {
        return GroupApi.newBuilder()
                .category(category(group.getCategory()))
                .description(group.getDescription())
                .objectives(group.getObjectives())
                .rules(group.getRules())
                .photo(group.getPhoto())
                .name(group.getName())
                .slug(group.getSlug())
                .id(group.getId())
                .build();
    }

    public static PostApi post(Post post) {
        PostAdditionalData postAdditionalData = null;
        try {
            postAdditionalData = objectMapper.readValue(post.getContent(), PostAdditionalData.class);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Error reading post's content", ex);
        }

        return PostApi.newBuilder()
                .expirationDate(post.getExpirationDate())
                .creationDate(post.getCreationDate())
                .description(post.getDescription())
                .content(postAdditionalData)
                .isPublic(post.isPublic())
                .id(post.getId())
                .build();
    }

    public static Post post(PostApi postApi) {
        String postContent = null;
        try {
            postContent = objectMapper.writeValueAsString(postApi.getContent());
        } catch (JsonProcessingException ex) {
            LOGGER.error("Error writing post's content", ex);
        }

        return Post.newBuilder()
                .expirationDate(postApi.getExpirationDate())
                .creationDate(postApi.getCreationDate())
                .description(postApi.getDescription())
                .isPublic(postApi.isPublic())
                .content(postContent)
                .id(postApi.getId())
                .build();
    }

}
