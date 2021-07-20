package usociety.manager.domain.service.common;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;

public interface AbstractService {

    UserApi getUser(String username) throws GenericException;

    Group getGroup(Long id) throws GenericException;

    void validateIfUserIsMember(String username, Long groupId, String errorCode) throws GenericException;

}
