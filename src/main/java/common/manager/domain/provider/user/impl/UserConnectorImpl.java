package common.manager.domain.provider.user.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import common.manager.app.rest.request.ChangePasswordRequest;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.domain.provider.user.UserConnector;
import common.manager.domain.provider.user.dto.UserDTO;
import common.manager.domain.service.web.impl.AbstractConnectorImpl;

@Component
public class UserConnectorImpl extends AbstractConnectorImpl implements UserConnector {

    @Value("${web.authentication.url}")
    private String baseUrl;
    @Value("${web.authentication.users-path}")
    private String path;
    @Value("${web.time-out:5}")
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
    public UserDTO get(String username) {
        return get(uriBuilder()
                        .path(path)
                        .pathSegment(username)
                        .build(),
                UserDTO.class);
    }

    @Override
    public UserDTO get(String username, String documentNumber, String email, String phoneNumber) {
        MultiValueMap<String, String> qParams = new LinkedMultiValueMap<>();
        qParams.add("documentNumber", documentNumber);
        qParams.add("phoneNumber", phoneNumber);
        qParams.add("username", username);
        qParams.add("email", email);

        return get(uriBuilder().path(path)
                        .pathSegment("findByFilters")
                        .queryParams(qParams)
                        .build(),
                UserDTO.class);
    }

    @Override
    public void enableAccount(String username) {
        post(uriBuilder().path(path)
                        .pathSegment("{username}")
                        .pathSegment("verifyEmail")
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
        return getList(uriBuilder().path(path)
                        .pathSegment("getAll")
                        .build(),
                UserDTO.class);
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest body) {
        patch(uriBuilder().path(path)
                        .pathSegment("{username}")
                        .pathSegment("changePassword")
                        .build(username),
                body,
                Void.class);
    }

    /*MultiValueMap<String, String> qParams = new LinkedMultiValueMap<>();
        qParams.add("description", "1234");
        Map<String, String> pParams = new HashMap<>();
        pParams.put("id", "1");
        get(uriBuilder()
                        .path("/test/{id}")
                        .queryParams(qParams)
                        .build(pParams),
                UserDTO.class);*/
}
