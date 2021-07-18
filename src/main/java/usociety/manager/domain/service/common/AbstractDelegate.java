package usociety.manager.domain.service.common;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.exception.GenericException;

public interface AbstractDelegate {

    UserApi getUser(String username) throws GenericException;

}
