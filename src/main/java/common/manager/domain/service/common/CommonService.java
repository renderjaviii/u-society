package common.manager.domain.service.common;

import common.manager.domain.exception.GenericException;
import common.manager.domain.model.User;

public interface CommonService {

    User getUser(Long userId);

    User getUser(String username) throws GenericException;

}
