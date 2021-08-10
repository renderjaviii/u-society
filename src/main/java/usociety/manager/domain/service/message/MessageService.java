package usociety.manager.domain.service.message;

import java.util.List;

import org.springframework.data.domain.Pageable;

import usociety.manager.app.api.MessageApi;
import usociety.manager.domain.exception.GenericException;

public interface MessageService {

    void sendGroupMessage(String username, Long groupId, MessageApi message) throws GenericException;

    List<MessageApi> getGroupMessages(String username, Long groupId, Pageable pageable) throws GenericException;

}
