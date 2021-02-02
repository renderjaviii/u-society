package usociety.manager.domain.service.message.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import usociety.manager.app.api.MessageApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.domain.enums.MessageTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.Message;
import usociety.manager.domain.repository.MessageRepository;
import usociety.manager.domain.service.aws.s3.CloudStorageService;
import usociety.manager.domain.service.common.impl.CommonServiceImpl;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.message.MessageService;
import usociety.manager.domain.service.user.UserService;

@Service
public class MessageServiceImpl extends CommonServiceImpl implements MessageService {

    private static final String GETTING_GROUP_MESSAGES_ERROR_CODE = "ERROR_GETTING_GROUP_MESSAGES";
    private static final String SENDING_GROUP_MESSAGE_ERROR_CODE = "ERROR_SENDING_GROUP_MESSAGE";

    private final MessageRepository messageRepository;
    private final GroupService groupService;
    private final UserService userService;
    private final CloudStorageService cloudStorageService;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository,
                              GroupService groupService,
                              UserService userService, CloudStorageService cloudStorageService) {
        this.messageRepository = messageRepository;
        this.groupService = groupService;
        this.userService = userService;
        this.cloudStorageService = cloudStorageService;
    }

    @Override
    public void sendGroupMessage(String username, MessageApi request, MultipartFile image) throws GenericException {
        UserApi user = getUser(username);
        Long groupId = request.getGroup().getId();
        Group group = groupService.get(groupId);
        validateIfUserActiveIsMember(username, groupId, SENDING_GROUP_MESSAGE_ERROR_CODE);

        if (MessageTypeEnum.TEXT == request.getType() && Objects.isNull(request.getContent())) {
            throw new GenericException("El content es requerido para mensajes de tipo texto.",
                    SENDING_GROUP_MESSAGE_ERROR_CODE);
        }

        if (MessageTypeEnum.IMAGE == request.getType() && Objects.nonNull(image) && image.isEmpty()) {
            throw new GenericException("Es obligatorio que envíe la imagen.", SENDING_GROUP_MESSAGE_ERROR_CODE);
        }
        processContent(request, image);

        messageRepository.save(Message.newBuilder()
                .content(request.getContent())
                .creationDate(LocalDateTime.now(clock))
                .type(request.getType().getCode())
                .userId(user.getId())
                .group(group)
                .build());
    }

    @Override
    public List<MessageApi> getGroupMessages(String username, Long groupId) throws GenericException {
        validateIfUserActiveIsMember(username, groupId, GETTING_GROUP_MESSAGES_ERROR_CODE);

        List<MessageApi> messageApiList = new ArrayList<>();
        for (Message message : messageRepository.findAllByGroupIdOrderByCreationDateDesc(groupId)) {
            messageApiList.add(buildGroupMessage(message));
        }
        return messageApiList;
    }

    private void processContent(MessageApi request, MultipartFile image) throws GenericException {
        if (MessageTypeEnum.IMAGE == request.getType()) {
           // String imageUrl = cloudStorageService.upload(image);
         //   request.setContent(imageUrl);
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
