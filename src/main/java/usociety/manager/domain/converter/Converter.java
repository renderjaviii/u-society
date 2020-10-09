package usociety.manager.domain.converter;

import static java.lang.Boolean.TRUE;

import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import usociety.manager.app.api.CategoryApi;
import usociety.manager.app.api.CommentApi;
import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.MessageApi;
import usociety.manager.app.api.OtpApi;
import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.ReactApi;
import usociety.manager.app.api.TokenApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.domain.enums.MessageTypeEnum;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.Comment;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.Message;
import usociety.manager.domain.model.Otp;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.model.React;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.provider.authentication.dto.TokenDTO;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.service.post.dto.PostAdditionalData;

public class Converter {

    private static final ModelMapper modelMapper;
    private static final ObjectMapper objectMapper;

    private Converter() {
        super();
    }

    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(TRUE);
        objectMapper = new ObjectMapper();
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

    public static CategoryApi category(Category category) {
        return modelMapper.map(category, CategoryApi.class);
    }

    public static GroupApi group(Group group) {
        GroupApi groupApi = modelMapper.map(group, GroupApi.class);
        try {
            if (!Objects.isNull(group.getObjectives())) {
                groupApi.setObjectives(objectMapper.readValue(group.getObjectives(), String[].class));
            }
            if (!Objects.isNull(group.getRules())) {
                groupApi.setRules(objectMapper.readValue(group.getRules(), String[].class));
            }
        } catch (JsonProcessingException ignored) {
            //It's no necessary.
        }
        return groupApi;
    }

    public static UserGroupApi userGroup(UserGroup userGroup) {
        return modelMapper.map(userGroup, UserGroupApi.class);
    }

    public static MessageApi message(Message message) {
        MessageApi messageApi = modelMapper.map(message, MessageApi.class);
        messageApi.setType(MessageTypeEnum.fromCode(message.getType()));
        return messageApi;
    }

    public static PostApi post(Post post) {
        PostApi postApi = modelMapper.map(post, PostApi.class);
        try {
            postApi.setContent(objectMapper.readValue(post.getContent(), PostAdditionalData.class));
        } catch (JsonProcessingException ignored) {
            //It's no necessary.
        }
        return postApi;
    }

    public static ReactApi react(React react) {
        return modelMapper.map(react, ReactApi.class);
    }

    public static CommentApi comment(Comment comment) {
        return modelMapper.map(comment, CommentApi.class);
    }

}
