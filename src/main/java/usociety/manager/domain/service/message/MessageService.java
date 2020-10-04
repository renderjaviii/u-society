package usociety.manager.domain.service.message;

import java.util.List;

import usociety.manager.app.api.MessageApi;
import usociety.manager.domain.exception.GenericException;

public interface MessageService {

    void sendGroupMessage(String username, MessageApi request) throws GenericException;

    List<MessageApi> getGroupMessages(String username, Long groupId) throws GenericException;

}
