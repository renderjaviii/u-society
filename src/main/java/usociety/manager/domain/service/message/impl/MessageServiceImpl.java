package usociety.manager.domain.service.message.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.MessageApi;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.Message;
import usociety.manager.domain.repository.MessageRepository;
import usociety.manager.domain.service.common.CommonServiceImpl;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.message.MessageService;

@Service
public class MessageServiceImpl extends CommonServiceImpl implements MessageService {

    private static final String GETTING_GROUP_MESSAGES_ERROR_CODE = "ERROR_GETTING_GROUP_MESSAGES";
    private static final String SENDING_GROUP_MESSAGE_ERROR_CODE = "ERROR_SENDING_GROUP_MESSAGE";

    private final MessageRepository messageRepository;
    private final GroupService groupService;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, GroupService groupService) {
        this.messageRepository = messageRepository;
        this.groupService = groupService;
    }

    @Override
    public void sendGroupMessage(String username, MessageApi request) throws GenericException {
        Group group = groupService.get(request.getGroupId());
        validateIfUserIsMember(username, request.getGroupId(), SENDING_GROUP_MESSAGE_ERROR_CODE);

        messageRepository.save(Message.newBuilder()
                .content(request.getContent())
                .creationDate(LocalDateTime.now(clock))
                .type(request.getType())
                .userId(request.getUserId())
                .group(group)
                .build());
    }

    @Override
    public List<MessageApi> getGroupMessages(String username, Long groupId) throws GenericException {
        validateIfUserIsMember(username, groupId, GETTING_GROUP_MESSAGES_ERROR_CODE);
        return messageRepository.findAllByGroupIdOrderByCreationDateAsc(groupId)
                .stream()
                .map(Converter::message)
                .collect(Collectors.toList());
    }

}
