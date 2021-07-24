package usociety.manager.domain.service.user;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.UpdateUserRequest;
import usociety.manager.domain.exception.GenericException;

public interface UpdateUserDelegate {

    UserApi execute(String username, UpdateUserRequest request) throws GenericException;

}
