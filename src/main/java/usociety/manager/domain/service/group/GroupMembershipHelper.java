package usociety.manager.domain.service.group;

import usociety.manager.app.api.UserGroupApi;
import usociety.manager.domain.exception.GenericException;

public interface GroupMembershipHelper {

    void update(String username, Long id, UserGroupApi request) throws GenericException;

    void join(String username, Long id) throws GenericException;

}
