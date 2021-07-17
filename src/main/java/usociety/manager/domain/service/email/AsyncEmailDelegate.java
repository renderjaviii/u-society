package usociety.manager.domain.service.email;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.Group;

public interface AsyncEmailDelegate {

    void send(UserApi user, Group group, Category category) throws GenericException;

}
