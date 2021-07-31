package usociety.manager.domain.service.group;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CreateOrUpdateGroupRequest;
import usociety.manager.domain.exception.GenericException;

public interface CreateUpdateGroupHelper {

    GroupApi create(UserApi user, CreateOrUpdateGroupRequest request) throws GenericException;

    GroupApi update(UserApi user, Long id, CreateOrUpdateGroupRequest request) throws GenericException;

}
