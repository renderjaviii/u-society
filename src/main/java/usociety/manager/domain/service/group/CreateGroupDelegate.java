package usociety.manager.domain.service.group;

import javax.mail.MessagingException;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.rest.request.CreateGroupRequest;
import usociety.manager.domain.exception.GenericException;

public interface CreateGroupDelegate {

    GroupApi execute(String username, CreateGroupRequest request) throws GenericException, MessagingException;

}
