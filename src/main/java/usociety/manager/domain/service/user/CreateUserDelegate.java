package usociety.manager.domain.service.user;

import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.domain.exception.GenericException;

public interface CreateUserDelegate {

    void execute(CreateUserRequest request) throws GenericException;

}
