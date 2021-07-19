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
    @Transactional(rollbackOn = Exception.class)
    public void sendGroupMessage(String username, MessageApi message) throws GenericException {
        UserApi user = getUser(username);
        Group group = getGroup(message.getGroup().getId());
        validateIfUserIsMember(username, group.getId(), SENDING_MESSAGE_ERROR_CODE);

        String content = processContent(message);

        messageRepository.save(Message.newBuilder()
                .creationDate(LocalDateTime.now(clock))
                .type(message.getType().getCode())
                .userId(user.getId())
                .content(content)
                .group(group)
                .build());
    }

    @Override
    public List<MessageApi> getGroupMessages(String username, Long groupId) throws GenericException {
        validateIfUserIsMember(username, groupId, GETTING_GROUP_MESSAGES_ERROR_CODE);

        List<MessageApi> groupMessages = new ArrayList<>();
        for (Message message : messageRepository.findAllByGroupIdOrderByCreationDateDesc(groupId)) {
            groupMessages.add(buildGroupMessage(message));
        }

        return groupMessages;
    }

    private String processContent(MessageApi message) throws GenericException {
        if (MessageTypeEnum.IMAGE.equals(message.getType())) {
            return cloudStorageService.upload(message.getContent());
        }
        return message.getContent();
    }

    private MessageApi buildGroupMessage(Message message) throws GenericException {
        return MessageApi.newBuilder()
                .user(userService.getById(message.getUserId()))
                .creationDate(message.getCreationDate())
                .content(message.getContent())
                .type(MessageTypeEnum.fromCode(message.getType()))
                .build();
    }

}
