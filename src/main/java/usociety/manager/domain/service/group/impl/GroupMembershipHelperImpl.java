package usociety.manager.domain.service.group.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static usociety.manager.domain.enums.UserGroupStatusEnum.DELETED;
import static usociety.manager.domain.enums.UserGroupStatusEnum.PENDING;
import static usociety.manager.domain.enums.UserGroupStatusEnum.REJECTED;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.common.impl.CommonServiceImpl;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.group.GroupMembershipHelper;
import usociety.manager.domain.service.user.UserService;

@Component
public class GroupMembershipHelperImpl extends CommonServiceImpl implements GroupMembershipHelper {

    private static final String UPDATING_MEMBERSHIP_ERROR_CODE = "ERROR_UPDATING_MEMBERSHIP";
    private static final String JOINING_TO_GROUP_ERROR_CODE = "ERROR_JOINING_TO_GROUP";
    private static final String GETTING_GROUP_ERROR_CODE = "ERROR_GETTING_GROUP";

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
    public void update(Long id, UserGroupApi request) throws GenericException {
        Optional<UserGroup> optionalUserGroup = userGroupRepository
                .findByGroupIdAndUserId(id, request.getUser().getId());
        if (!optionalUserGroup.isPresent()) {
            throw new GenericException("No se puede realizar esta operación con este usuario.",
                    UPDATING_MEMBERSHIP_ERROR_CODE);
        }

        UserGroup userGroup = optionalUserGroup.get();
        if (!userGroup.isAdmin()) {
            if (REJECTED == request.getStatus() || DELETED == request.getStatus()) {
                userGroupRepository.delete(userGroup);
            } else {
                userGroup.setRole(request.getRole());
                userGroup.setStatus(request.getStatus().getCode());
                userGroupRepository.save(userGroup);
            }
        } else {
            throw new GenericException("El administrador del grupo no puede realizar esta operación.",
                    UPDATING_MEMBERSHIP_ERROR_CODE);
        }
    }

    @Override
    public void join(Long id, String username) throws GenericException {
        Group group = getGroup(id);
        UserApi user = getUser(username);

        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByGroupIdAndUserId(id, user.getId());
        if (optionalUserGroup.isPresent()) {
            throw new GenericException("El usuario ya solicitó ingresar al grupo o ya es miembro activo.",
                    JOINING_TO_GROUP_ERROR_CODE);
        }

        userGroupRepository.save(UserGroup.newBuilder()
                .group(group)
                .isAdmin(FALSE)
                .status(PENDING.getCode())
                .userId(user.getId())
                .build());

        Optional<UserGroup> optionalUserGroupAdmin = userGroupRepository.findByGroupIdAndIsAdmin(id, TRUE);
        if (optionalUserGroupAdmin.isPresent()) {
            UserGroup userGroupAdmin = optionalUserGroupAdmin.get();
            UserApi userAdmin = userService.getById(userGroupAdmin.getUserId());
            mailService.send(userAdmin.getEmail(), buildEmailContent(group, user, userAdmin), TRUE);
        }
    }

    private Group getGroup(Long id) throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (!optionalGroup.isPresent()) {
            throw new GenericException(String.format("Grupo con id: %s no existe.", id), GETTING_GROUP_ERROR_CODE);
        }
        return optionalGroup.get();
    }

    private String buildEmailContent(Group group, UserApi user, UserApi userAdmin) {
        return String.format("<html><body>" +
                        "<h3>Hola %s.</h3>" +
                        "<p>%s ha solicitado unirse a tu grupo: <u>%s</u></p>" +
                        "<p>¡Dirígite a <a href='https://usociety-68208.web.app/'>U - Society</a> y permítele ingresar!</p>" +
                        "</body></html>",
                StringUtils.capitalize(userAdmin.getName()),
                StringUtils.capitalize(user.getName()),
                StringUtils.capitalize(group.getName()));
    }

}
