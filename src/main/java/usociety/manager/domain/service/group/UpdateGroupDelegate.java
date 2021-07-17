package usociety.manager.domain.service.group;

import usociety.manager.app.rest.request.UpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.exception.GenericException;

public interface UpdateGroupDelegate {

    GetGroupResponse execute(String username, UpdateGroupRequest request) throws GenericException;

}
