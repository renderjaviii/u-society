package usociety.manager.domain.service.user;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.domain.exception.GenericException;

public interface CreateUserDelegate {

    UserApi execute(CreateUserRequest request) throws GenericException;

}
