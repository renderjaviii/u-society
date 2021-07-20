package usociety.manager.domain.service.group;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.UpdateGroupRequest;
import usociety.manager.domain.exception.GenericException;

public interface UpdateGroupDelegate {

    void execute(UserApi user, UpdateGroupRequest request) throws GenericException;

}
