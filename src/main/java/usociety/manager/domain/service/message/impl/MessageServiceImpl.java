package usociety.manager.domain.service.message.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.MessageApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.domain.enums.MessageTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.Message;
import usociety.manager.domain.repository.MessageRepository;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.common.impl.AbstractServiceImpl;
import usociety.manager.domain.service.message.MessageService;
import usociety.manager.domain.util.PageableUtils;

@Service
public class MessageServiceImpl extends AbstractServiceImpl implements MessageService {

    private static final String GETTING_GROUP_MESSAGES_ERROR_CODE = "ERROR_GETTING_GROUP_MESSAGES";
    private static final String SENDING_MESSAGE_ERROR_CODE = "ERROR_SENDING_GROUP_MESSAGE";

    private final CloudStorageService cloudStorageService;
    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(CloudStorageService cloudStorageService,
                              MessageRepository messageRepository) {
        this.cloudStorageService = cloudStorageService;
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional(dontRollbackOn = GenericException.class, rollbackOn = Exception.class)
    public void sendGroupMessage(String username, Long groupId, MessageApi message) throws GenericException {
        UserApi user = getUser(username);
        Group group = getGroup(groupId);
        validateIfUserIsMember(username, groupId, SENDING_MESSAGE_ERROR_CODE);

        String content = processContent(message);

        messageRepository.save(Message.newBuilder()
                .creationDate(LocalDateTime.now(clock))
                .type(message.getType().getValue())
                .userId(user.getId())
                .content(content)
                .group(group)
                .build());
    }

    @Override
    public List<MessageApi> getGroupMessages(String username, Long groupId, int page, int pageSize)
            throws GenericException {
        validateIfUserIsMember(username, groupId, GETTING_GROUP_MESSAGES_ERROR_CODE);

        List<Message> messages = messageRepository
                .findAllByGroupIdOrderByCreationDateDesc(groupId, PageableUtils.paginate(page, pageSize));

        List<MessageApi> response = new ArrayList<>();
        for (Message message : messages) {
            response.add(buildGroupMessage(message));
        }

        return response;
    }

    private String processContent(MessageApi message) throws GenericException {
        if (MessageTypeEnum.IMAGE.equals(message.getType())) {
            return cloudStorageService.uploadImage(message.getContent());
        }
        return message.getContent();
    }

    private MessageApi buildGroupMessage(Message message) throws GenericException {
        return MessageApi.newBuilder()
                .type(MessageTypeEnum.valueOf(message.getType()))
                .user(userService.getById(message.getUserId()))
                .creationDate(message.getCreationDate())
                .content(message.getContent())
                .build();
    }

}
