package usociety.manager.domain.service.message.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.MessageApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.domain.enums.MessageTypeEnum;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.Message;
import usociety.manager.domain.repository.MessageRepository;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.common.impl.AbstractDelegateImpl;
import usociety.manager.domain.service.message.MessageService;

@Service
public class MessageServiceImpl extends AbstractDelegateImpl implements MessageService {

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
    public void sendGroupMessage(String username, MessageApi request) throws GenericException {
        UserApi user = getUser(username);
        Long groupId = request.getGroup().getId();
        Group group = getGroup(groupId);

        validateIfUserIsMember(username, groupId, UserGroupStatusEnum.ACTIVE, SENDING_MESSAGE_ERROR_CODE);
        validateRequest(request);

        processContent(request, request.getImage());

        messageRepository.save(Message.newBuilder()
                .creationDate(LocalDateTime.now(clock))
                .type(request.getType().getCode())
                .content(request.getContent())
                .userId(user.getId())
                .group(group)
                .build());
    }

    @Override
    public List<MessageApi> getGroupMessages(String username, Long groupId) throws GenericException {
        validateIfUserIsMember(username, groupId, UserGroupStatusEnum.ACTIVE, GETTING_GROUP_MESSAGES_ERROR_CODE);

        List<MessageApi> groupMessages = new ArrayList<>();
        for (Message message : messageRepository.findAllByGroupIdOrderByCreationDateDesc(groupId)) {
            groupMessages.add(buildGroupMessage(message));
        }

        return groupMessages;
    }

    private void validateRequest(MessageApi request) throws GenericException {
        if (MessageTypeEnum.TEXT == request.getType() && Objects.isNull(request.getContent())) {
            throw new GenericException("Es obligatorio que envíe información.", SENDING_MESSAGE_ERROR_CODE);
        }

        if (MessageTypeEnum.IMAGE == request.getType() && StringUtils.isNotEmpty(request.getImage())) {
            throw new GenericException("Es obligatorio que envíe la imagen.", SENDING_MESSAGE_ERROR_CODE);
        }
    }

    private void processContent(MessageApi request, String image) throws GenericException {
        if (MessageTypeEnum.IMAGE == request.getType()) {
            String imageUrl = cloudStorageService.upload(image);
            request.setContent(imageUrl);
        }
    }

    private MessageApi buildGroupMessage(Message message) throws GenericException {
        return MessageApi.newBuilder()
                .type(MessageTypeEnum.fromCode(message.getType()))
                .user(userService.getById(message.getUserId()))
                .creationDate(message.getCreationDate())
                .content(message.getContent())
                .build();
    }

}
