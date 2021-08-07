package usociety.manager.domain.service.group.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static usociety.manager.domain.enums.UserGroupStatusEnum.PENDING;
import static usociety.manager.domain.util.Constants.GROUP_NOT_FOUND;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserGroupApi;
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
            "<p>¡Dirígite a <a href='%s'>U - Society</a> y permítele ingresar!</p>" +
            "</body></html>";

    private static final String UPDATING_MEMBERSHIP_ERROR_CODE = "ERROR_UPDATING_MEMBERSHIP";
    private static final String JOINING_GROUP_ERROR_CODE = "ERROR_JOINING_GROUP";

    @Value("${config.app.domain:https://usociety-68208.web.app/}")
    private String applicationDomain;

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
    @Transactional(dontRollbackOn = GenericException.class, rollbackOn = Exception.class)
    public void update(UserApi user, Long id, UserGroupApi request) throws GenericException {
        UserApi member = request.getMember();
        UserGroup userGroup = validateUsersMembership(user, member, id);
        userGroup.setRole(member.getRole());

        String status = request.getStatus().getValue();
        userGroup.setStatus(StringUtils.isNotEmpty(status) ? status : member.getRole());
        userGroupRepository.save(userGroup);
    }

    @Override
    @Transactional(dontRollbackOn = GenericException.class, rollbackOn = Exception.class)
    public void join(UserApi user, Long id) throws GenericException {
        Group group = getGroup(id);

        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByGroupIdAndUserId(id, user.getId());
        if (optionalUserGroup.isPresent()) {
            throw new GenericException("User has already required to join", JOINING_GROUP_ERROR_CODE);
        }

        userGroupRepository.save(UserGroup.newBuilder()
                .status(PENDING.getValue())
                .userId(user.getId())
                .isAdmin(FALSE)
                .group(group)
                .build());

        sendJoiningRequestEmailToAdmin(id, group, user);
    }

    private UserGroup validateUsersMembership(UserApi user, UserApi member, Long id)
            throws GenericException {
        Optional<UserGroup> adminGroup = userGroupRepository.findByGroupIdAndUserIdAndIsAdmin(id, user.getId(), TRUE);
        if (!adminGroup.isPresent()) {
            throw new GenericException("You are not allowed to perform this operation", UPDATING_MEMBERSHIP_ERROR_CODE);
        }

        if (Objects.isNull(member.getId())) {
            throw new GenericException("Member id must be sent", UPDATING_MEMBERSHIP_ERROR_CODE);
        }

        return userGroupRepository.findByGroupIdAndUserId(id, member.getId())
                .orElseThrow(buildExceptionSupplier("User is not member", UPDATING_MEMBERSHIP_ERROR_CODE));
    }

    private Group getGroup(Long id) throws GenericException {
        return groupRepository.findById(id)
                .orElseThrow(buildExceptionSupplier("Group does not exist", GROUP_NOT_FOUND));
    }

    private Supplier<GenericException> buildExceptionSupplier(String message, String code) {
        return () -> new GenericException(message, code);
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
                StringUtils.capitalize(group.getName()),
                applicationDomain
        );
    }

}
