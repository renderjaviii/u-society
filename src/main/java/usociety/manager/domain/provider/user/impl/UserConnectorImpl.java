package usociety.manager.domain.provider.user.impl;

import static org.apache.logging.log4j.util.Strings.EMPTY;

import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.service.web.impl.AbstractConnectorImpl;

@Component
@SuppressWarnings("java:S1192")
public class UserConnectorImpl extends AbstractConnectorImpl implements UserConnector {

    @Value("${web.authentication.url}")
    private String baseUrl;
    @Value("${web.authentication.users-path}")
    private String path;
    @Value("${web.read-time-out:5}")
    private int timeOut;

    @PostConstruct
    private void init() {
        setUp(baseUrl, timeOut);
    }

    @Override
    public UserDTO create(CreateUserRequest body) {
        return post(uriBuilder().path(path).build(), body, UserDTO.class);
    }

    @Override
    public void update(UserDTO body) {
        patch(uriBuilder().path(path).build(), body, Void.class);
    }

    @Override
    public UserDTO get(String username) {
        return get(uriBuilder()
                        .path(path)
                        .pathSegment(username)
                        .build(),
                UserDTO.class);
    }

    @Override
    public UserDTO get(Long id, String username, String email) {
        MultiValueMap<String, String> qParams = new LinkedMultiValueMap<>();

        qParams.add("id", Objects.isNull(id) ? EMPTY : id.toString());
        qParams.add("username", username);
        qParams.add("email", email);

        return get(uriBuilder().path(path)
                        .queryParams(qParams)
                        .build(),
                UserDTO.class);
    }

    @Override
    public void enableAccount(String username) {
        post(uriBuilder().path(path)
                        .pathSegment("{username}")
                        .pathSegment("verify-email")
                        .build(username),
                Void.class);
    }

    @Override
    public void delete(String username) {
        delete(uriBuilder().path(path).pathSegment("{username}").build(username),
                Void.class);
    }

    @Override
    public List<UserDTO> getAll() {
        return getWebClient().get()
                .uri(uriBuilder()
                        .path(path)
                        .pathSegment("all")
                        .build().toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserDTO>>() {
                })
                .block();
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest body) {
        patch(uriBuilder().path(path)
                        .pathSegment("{username}")
                        .pathSegment("change-password")
                        .build(username),
                body,
                Void.class);
    }

}
