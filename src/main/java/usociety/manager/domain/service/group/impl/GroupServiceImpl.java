package usociety.manager.domain.service.group.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;
import static usociety.manager.domain.enums.UserGroupStatusEnum.DELETED;
import static usociety.manager.domain.enums.UserGroupStatusEnum.PENDING;
import static usociety.manager.domain.enums.UserGroupStatusEnum.REJECTED;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.slugify.Slugify;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.app.rest.request.CreateGroupRequest;
import usociety.manager.app.rest.request.UpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.aws.s3.CloudStorageService;
import usociety.manager.domain.service.category.CategoryService;
import usociety.manager.domain.service.common.impl.CommonServiceImpl;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.group.SendAsyncEmail;
import usociety.manager.domain.service.user.UserService;

@Service
public class GroupServiceImpl extends CommonServiceImpl implements GroupService {

    private static final String GROUP_NAME_ERROR_FORMAT = "Grupo con nombre: %s ya existe, prueba un nombre diferente.";

    private static final String ERROR_UPDATING_MEMBERSHIP_ERROR_CODE = "ERROR_UPDATING_MEMBERSHIP";
    private static final String UPDATING_MEMBERSHIP_ERROR_CODE = "ERROR_UPDATING_MEMBERSHIP";
    private static final String JOINING_TO_GROUP_ERROR_CODE = "ERROR_JOINING_TO_GROUP";
    private static final String CREATING_GROUP_ERROR_CODE = "ERROR_CREATING_GROUP";
    private static final String GETTING_GROUP_ERROR_CODE = "ERROR_GETTING_GROUP";
    private static final String ADMINISTRATOR_ROLE = "Administrador";

    private final UserGroupRepository userGroupRepository;
    private final CategoryService categoryService;
    private final GroupRepository groupRepository;
    private final SendAsyncEmail sendAsyncEmail;
    private final UserService userService;
    private final MailService mailService;
    private final CloudStorageService cloudStorageService;
    private final Slugify slugify;

    @Autowired
    public GroupServiceImpl(UserGroupRepository userGroupRepository,
                            CategoryService categoryService,
                            GroupRepository groupRepository,
                            UserService userService,
                            MailService mailService,
                            CloudStorageService cloudStorageService,
                            SendAsyncEmail sendAsyncEmail, Slugify slugify) {
        this.categoryService = categoryService;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.cloudStorageService = cloudStorageService;
        this.sendAsyncEmail = sendAsyncEmail;
        this.slugify = slugify;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public GroupApi create(String username, CreateGroupRequest request)
            throws GenericException, MessagingException {
        Optional<Group> optionalGroup = groupRepository.findByName(request.getName());
        if (optionalGroup.isPresent()) {
            String errorMessage = String.format(GROUP_NAME_ERROR_FORMAT, request.getName());
            throw new GenericException(errorMessage, CREATING_GROUP_ERROR_CODE);
        }

        Category category = categoryService.get(request.getCategory().getId());
        String photoUrl = cloudStorageService.upload(request.getPhoto());
        UserApi userApi = getUser(username);

        Group savedGroup;
        try {
            savedGroup = groupRepository.save(Group.newBuilder()
                    .description(request.getDescription())
                    .objectives(request.getObjectives().stream().map(this::removeCommas).collect(Collectors.toList()))
                    .rules(request.getRules().stream().map(this::removeCommas).collect(Collectors.toList()))
                    .slug(slugify.slugify(request.getName()))
                    .name(request.getName())
                    .category(category)
                    .photo(photoUrl)
                    .build());

            userGroupRepository.save(UserGroup.newBuilder()
                    .userId(userApi.getId())
                    .role(ADMINISTRATOR_ROLE)
                    .status(ACTIVE.getCode())
                    .group(savedGroup)
                    .isAdmin(TRUE)
                    .build());
        } catch (Exception ex) {
            cloudStorageService.delete(photoUrl);
            throw new GenericException("Error general creando grupo.", CREATING_GROUP_ERROR_CODE);
        }
        sendAsyncEmail.send(userApi, savedGroup, category);

        return Converter.group(savedGroup);
    }

    @Override
    public GetGroupResponse get(Long id, String username) throws GenericException {
        Group group = getGroup(id);
        return buildGetGroupResponse(group, username);
    }

    @Override
    public Group get(Long id) throws GenericException {
        return getGroup(id);
    }

    @Override
    public List<GroupApi> getAllUserGroups(String username) throws GenericException {
        UserApi user = getUser(username);
        return userGroupRepository
                .findAllByUserIdAndStatus(user.getId(), ACTIVE.getCode())
                .stream()
                .map(userGroup -> buildGroupResponse(userGroup.getGroup()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateMembership(Long id, UserGroupApi request) throws GenericException {
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
    @Transactional(rollbackOn = Exception.class)
    public GetGroupResponse update(UpdateGroupRequest request, String username)
            throws GenericException {
        Group group = getGroup(request.getId());

        UserApi user = userService.get(username);
        UserGroup userGroup = getUserGroup(request.getId(), user.getId());
        if (!userGroup.isAdmin()) {
            throw new GenericException("No eres administrador de este grupo.", "FORBIDDEN_ACCESS");
        }

        Category category = categoryService.get(request.getCategory().getId());
        Group updatedGroup = groupRepository.save(Group.newBuilder()
                .slug(slugify.slugify(request.getName()))
                .description(request.getDescription())
                .objectives(request.getObjectives())
                .rules(request.getRules())
               .photo(StringUtils.isNotEmpty(request.getPhoto())
                        ? cloudStorageService.upload(request.getPhoto()): group.getPhoto())
                .name(request.getName())
                .category(category)
                .id(group.getId())
                .build());

        return buildGetGroupResponse(updatedGroup, username);
    }

    @Override
    public List<GroupApi> getByFilters(String name, Long categoryId) throws GenericException {
        if (StringUtils.isEmpty(name) && Objects.isNull(categoryId)) {
            throw new GenericException("Debe enviar ya sea el nombre y/o la categoría.", GETTING_GROUP_ERROR_CODE);
        }

        List<Group> groupList = groupRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, name);
        groupList.addAll(groupRepository.findByCategoryIdOrNameContainingIgnoreCase(categoryId, name));
        return groupList.stream()
                .distinct()
                .map(this::buildGroupResponse)
                .collect(Collectors.toList());
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

    @Override
    public GetGroupResponse getBySlug(String slug, String user) throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findBySlug(slug);
        if (!optionalGroup.isPresent()) {
            throw new GenericException("Groupo no encontrado.", GETTING_GROUP_ERROR_CODE);
        }

        Group group = optionalGroup.get();
        return buildGetGroupResponse(group, user);
    }

    private GetGroupResponse buildGetGroupResponse(Group group, String username) throws GenericException {
        UserApi user = getUser(username);
        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByGroupIdAndUserId(group.getId(), user.getId());

        UserGroupStatusEnum membershipStatus = null;
        if (optionalUserGroup.isPresent()) {
            UserGroup userGroup = optionalUserGroup.get();

            UserGroupStatusEnum userGroupStatusEnum = UserGroupStatusEnum.fromCode(userGroup.getStatus());
            if (ACTIVE == userGroupStatusEnum) {
                List<UserGroup> groupMembers = userGroupRepository
                        .findAllByGroupIdAndUserIdNot(group.getId(), user.getId());

                return GetGroupResponse.newBuilder()
                        .pendingMembers(userGroup.isAdmin() ? getMembersDataByStatus(groupMembers, PENDING) : null)
                        .activeMembers(getMembersDataByStatus(groupMembers, ACTIVE))
                        .group(Converter.group(group))
                        .membershipStatus(ACTIVE)
                        .isAdmin(userGroup.isAdmin())
                        .build();
            }
            membershipStatus = userGroupStatusEnum;
        }

        group.setRules(null);
        return GetGroupResponse.newBuilder()
                .membershipStatus(membershipStatus)
                .group(Converter.group(group))
                .build();
    }

    private String removeCommas(String value) {
        return value.replace(",", EMPTY);
    }

    private Group getGroup(Long id) throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (!optionalGroup.isPresent()) {
            throw new GenericException(String.format("Grupo con id: %s no existe.", id), GETTING_GROUP_ERROR_CODE);
        }
        return optionalGroup.get();
    }

    private UserGroup getUserGroup(Long groupId, Long userId) throws GenericException {
        return userGroupRepository.findByGroupIdAndUserIdAndStatus(groupId, userId, ACTIVE.getCode())
                .orElseThrow(() -> new GenericException("El usario no es miembro activo del grupo.",
                        ERROR_UPDATING_MEMBERSHIP_ERROR_CODE));
    }

    private List<UserApi> getMembersDataByStatus(List<UserGroup> userList, UserGroupStatusEnum userGroupStatus)
            throws GenericException {
        List<UserGroup> membersGroup = userList
                .stream()
                .filter(userGroup -> userGroupStatus.getCode() == userGroup.getStatus())
                .collect(Collectors.toList());

        List<UserApi> usersDataList = new ArrayList<>();
        for (UserGroup userGroup : membersGroup) {
            UserApi memberUser = userService.getById(userGroup.getUserId());
            memberUser.setRole(userGroup.getRole());
            usersDataList.add(memberUser);
        }
        return usersDataList;
    }

    private GroupApi buildGroupResponse(Group group) {
        return GroupApi.newBuilder()
                .id(group.getId())
                .category(Converter.category(group.getCategory()))
                .description(group.getDescription())
                .photo(group.getPhoto())
                .name(group.getName())
                .slug(group.getSlug())
                .build();
    }

    private String buildEmailContent(Group group, UserApi user, UserApi userAdmin) {
        return String.format("<html><body>" +
                        "<h3>Hola %s.</h3>" +
                        "<p>%s ha solicitado unirse a tu grupo: <u>%s</u></p>" +
                        "<p>¡Dirígite a <b>U - Society</b> y permítele ingresar!</p>" +
                        "</body></html>",
                StringUtils.capitalize(userAdmin.getName()),
                StringUtils.capitalize(user.getName()),
                StringUtils.capitalize(group.getName()));
    }

}
