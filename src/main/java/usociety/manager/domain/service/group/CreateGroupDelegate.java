package usociety.manager.domain.service.group;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CreateGroupRequest;
import usociety.manager.domain.exception.GenericException;

public interface CreateGroupDelegate {

    GroupApi execute(UserApi user, CreateGroupRequest request) throws GenericException;

}
