package usociety.manager.domain.service.message.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import usociety.manager.app.api.MessageApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.enums.MessageTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.Message;
import usociety.manager.domain.repository.MessageRepository;
import usociety.manager.domain.service.aws.s3.S3Service;
import usociety.manager.domain.service.common.CommonServiceImpl;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.message.MessageService;

@Service
public class MessageServiceImpl extends CommonServiceImpl implements MessageService {

    private static final String GETTING_GROUP_MESSAGES_ERROR_CODE = "ERROR_GETTING_GROUP_MESSAGES";
    private static final String SENDING_GROUP_MESSAGE_ERROR_CODE = "ERROR_SENDING_GROUP_MESSAGE";

    private final MessageRepository messageRepository;
    private final GroupService groupService;
    private final S3Service s3Service;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository,
                              GroupService groupService,
                              S3Service s3Service) {
        this.messageRepository = messageRepository;
        this.groupService = groupService;
        this.s3Service = s3Service;
    }

    @Override
    public void sendGroupMessage(String username,
                                 MessageApi request,
                                 MultipartFile image) throws GenericException {
        UserApi user = getUser(username);
        Group group = groupService.get(request.getGroupId());
        validateIfUserIsMember(username, request.getGroupId(), SENDING_GROUP_MESSAGE_ERROR_CODE);

        if (MessageTypeEnum.TEXT == request.getType() && Objects.isNull(request.getContent())) {
            throw new GenericException("El content es requerido para mensajes de tipo texto.",
                    SENDING_GROUP_MESSAGE_ERROR_CODE);
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
        return messageRepository.findAllByGroupIdOrderByCreationDateAsc(groupId)
                .stream()
                .map(Converter::message)
                .collect(Collectors.toList());
    }

    private void processContent(MessageApi request, MultipartFile image) throws GenericException {
        if (MessageTypeEnum.IMAGE == request.getType()) {
            String imageUrl = s3Service.upload(image);
            request.setContent(imageUrl);
        }
    }

}
