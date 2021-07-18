package usociety.manager.domain.service.group.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static usociety.manager.domain.enums.UserGroupStatusEnum.DELETED;
import static usociety.manager.domain.enums.UserGroupStatusEnum.PENDING;
import static usociety.manager.domain.enums.UserGroupStatusEnum.REJECTED;
import static usociety.manager.domain.util.Constants.GROUP_NOT_FOUND;

import java.util.Arrays;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.group.GroupMembershipHelper;
import usociety.manager.domain.service.user.UserService;

@Component
public class GroupMembershipHelperImpl implements GroupMembershipHelper {

    private static final String JOIN_GROUP_EMAIL_FORMAT = "<html><body>" +
            "<h3>Hola %s.</h3>" +
            "<p>%s ha solicitado unirse a tu grupo: <u>%s</u></p>" +
            "<p>¡Dirígite a <a href='https://usociety-68208.web.app/'>U - Society</a> y permítele ingresar!</p>" +
            "</body></html>";

    private static final String UPDATING_MEMBERSHIP_ERROR_CODE = "ERROR_UPDATING_MEMBERSHIP";
    private static final String JOINING_GROUP_ERROR_CODE = "ERROR_JOINING_TO_GROUP";

    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final MailService mailService;
    private final UserService userService;

    @Autowired
    public GroupMembershipHelperImpl(UserGroupRepository userGroupRepository,
                                     GroupRepository groupRepository,
                                     MailService mailService,
                                     UserService userService) {
        this.userGroupRepository = userGroupRepository;
        this.groupRepository = groupRepository;
        this.mailService = mailService;
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void update(UserApi user, Long id, UserGroupApi request) throws GenericException {
        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByGroupIdAndUserId(id, user.getId());
        if (!optionalUserGroup.isPresent()) {
            throw new GenericException("No es posible realizar la actualización.", UPDATING_MEMBERSHIP_ERROR_CODE);
        }

        UserGroup userGroup = optionalUserGroup.get();
        if (!userGroup.isAdmin()) {
            UserGroupStatusEnum status = request.getStatus();

            if (Arrays.asList(REJECTED, DELETED).contains(status)) {
                userGroupRepository.delete(userGroup);
            } else {
                userGroup.setRole(request.getRole());
                userGroup.setStatus(status.getCode());
                userGroupRepository.save(userGroup);
            }
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void join(UserApi user, Long id) throws GenericException {
        Group group = getGroup(id);

        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByGroupIdAndUserId(id, user.getId());
        if (optionalUserGroup.isPresent()) {
            throw new GenericException("El usuario ya solicitó ingresar al grupo", JOINING_GROUP_ERROR_CODE);
        }

        userGroupRepository.save(UserGroup.newBuilder()
                .status(PENDING.getCode())
                .userId(user.getId())
                .isAdmin(FALSE)
                .group(group)
                .build());

        sendJoiningRequestEmailToAdmin(id, group, user);
    }

    private void sendJoiningRequestEmailToAdmin(Long id, Group group, UserApi user) throws GenericException {
        Optional<UserGroup> optionalUserGroupAdmin = userGroupRepository.findByGroupIdAndIsAdmin(id, TRUE);
        if (optionalUserGroupAdmin.isPresent()) {
            UserGroup userGroupAdmin = optionalUserGroupAdmin.get();
            UserApi userAdmin = userService.getById(userGroupAdmin.getUserId());
            mailService.send(userAdmin.getEmail(), buildEmail(group, user, userAdmin), TRUE);
        }
    }

    private String buildEmail(Group group, UserApi user, UserApi userAdmin) {
        return String.format(JOIN_GROUP_EMAIL_FORMAT,
                StringUtils.capitalize(userAdmin.getName()),
                StringUtils.capitalize(user.getName()),
                StringUtils.capitalize(group.getName()));
    }

    private Group getGroup(Long id) throws GenericException {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GenericException("Group does not exist", GROUP_NOT_FOUND));
    }

}
