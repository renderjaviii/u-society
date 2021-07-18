package usociety.manager.domain.service.message.impl;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static usociety.manager.app.api.MessageApi.TextMessageApi;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.MessageApi;
import usociety.manager.app.api.MessageApi.ImageMessageApi;
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
    public void sendGroupMessage(String username, MessageApi message) throws GenericException {
        UserApi user = getUser(username);
        Group group = getGroup(message.getGroup().getId());
        validateIfUserIsMember(username, group.getId(), SENDING_MESSAGE_ERROR_CODE);

        String content = processContent(message);

        messageRepository.save(Message.newBuilder()
                .description(getMessageDescription(message))
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
        if (message instanceof ImageMessageApi) {
            ImageMessageApi imageMessage = (ImageMessageApi) message;
            return cloudStorageService.upload(imageMessage.getContent());
        }
        if (message instanceof TextMessageApi) {
            TextMessageApi textMessage = (TextMessageApi) message;
            return textMessage.getContent();
        }
        throw new UnsupportedOperationException("Message type is not supported");
    }

    private String getMessageDescription(MessageApi message) {
        if (message instanceof ImageMessageApi) {
            return ((ImageMessageApi) message).getDescription();
        }
        return EMPTY;
    }

    private MessageApi buildGroupMessage(Message message) throws GenericException {
        if (MessageTypeEnum.IMAGE.getCode() == message.getType()) {
            return ImageMessageApi.newBuilder()
                    .user(userService.getById(message.getUserId()))
                    .creationDate(message.getCreationDate())
                    .description(message.getDescription())
                    .content(message.getContent())
                    .build();

        }
        return TextMessageApi.newBuilder()
                .user(userService.getById(message.getUserId()))
                .creationDate(message.getCreationDate())
                .content(message.getContent())
                .build();
    }

}
