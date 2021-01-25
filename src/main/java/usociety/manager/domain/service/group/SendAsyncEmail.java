package usociety.manager.domain.service.group;

import javax.mail.MessagingException;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.Group;

public interface SendAsyncEmail {

    void send(UserApi user, Group group, Category category) throws GenericException, MessagingException;

}
