package usociety.manager.domain.service.message.impl;

import static usociety.manager.app.api.MessageApi.TextMessageApi;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.MessageApi;
import usociety.manager.app.api.MessageApi.ImageMessageApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.ImageMessage;
import usociety.manager.domain.model.Message;
import usociety.manager.domain.model.TextMessage;
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
    public void sendGroupMessage(String username, MessageApi message) throws GenericException {
        UserApi user = getUser(username);
        Group group = getGroup(message.getGroup().getId());
        validateIfUserIsMember(username, group.getId(), SENDING_MESSAGE_ERROR_CODE);

        save(message, user, group);
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
        if (message instanceof ImageMessageApi) {
            ImageMessageApi imageMessage = (ImageMessageApi) message;
            return cloudStorageService.upload(imageMessage.getContent());
        }
        TextMessageApi textMessage = (TextMessageApi) message;
        return textMessage.getContent();
    }

    private void save(MessageApi message, UserApi user, Group group) throws GenericException {
        Message entity = convertFromApiToEntity(message);

        entity.setContent(processContent(message));
        entity.setUserId(user.getId());
        entity.setGroup(group);
        messageRepository.save(entity);
    }

    private Message convertFromApiToEntity(MessageApi message) {
        if (message instanceof ImageMessageApi) {
            return objectMapper.convertValue(message, ImageMessage.class);
        }
        return objectMapper.convertValue(message, TextMessage.class);

    }

    private MessageApi buildGroupMessage(Message message) throws GenericException {
        if (message instanceof ImageMessage) {
            ImageMessage imageMessage = ((ImageMessage) message);
            return ImageMessageApi.newBuilder()
                    .user(userService.getById(imageMessage.getUserId()))
                    .creationDate(imageMessage.getCreationDate())
                    .description(imageMessage.getDescription())
                    .content(imageMessage.getContent())
                    .build();

        }
        return TextMessageApi.newBuilder()
                .user(userService.getById(message.getUserId()))
                .creationDate(message.getCreationDate())
                .content(message.getContent())
                .build();
    }

}
