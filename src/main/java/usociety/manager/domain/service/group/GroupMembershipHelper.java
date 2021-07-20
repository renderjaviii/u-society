package usociety.manager.domain.service.group;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.domain.exception.GenericException;

public interface GroupMembershipHelper {

    void update(UserApi user, Long id, UserGroupApi request) throws GenericException;

    void join(UserApi user, Long id) throws GenericException;

}
