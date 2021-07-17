package usociety.manager.domain.service.group.impl;

import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;
import static usociety.manager.domain.enums.UserGroupStatusEnum.PENDING;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.slugify.Slugify;

import usociety.manager.app.api.UserApi;
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
import usociety.manager.domain.service.group.UpdateGroupDelegate;
import usociety.manager.domain.service.user.UserService;

@Component
public class UpdateGroupDelegateImpl extends CommonServiceImpl implements UpdateGroupDelegate {

    private static final String ERROR_UPDATING_MEMBERSHIP_ERROR_CODE = "ERROR_UPDATING_MEMBERSHIP";
    private static final String GETTING_GROUP_ERROR_CODE = "ERROR_GETTING_GROUP";

    private final UserGroupRepository userGroupRepository;
    private final CloudStorageService cloudStorageService;
    private final CategoryService categoryService;
    private final GroupRepository groupRepository;
    private final UserService userService;
    private final Slugify slugify;

    @Autowired
    public UpdateGroupDelegateImpl(UserGroupRepository userGroupRepository,
                                   CloudStorageService cloudStorageService,
                                   CategoryService categoryService,
                                   GroupRepository groupRepository,
                                   UserService userService,
                                   Slugify slugify) {
        this.userGroupRepository = userGroupRepository;
        this.cloudStorageService = cloudStorageService;
        this.categoryService = categoryService;
        this.groupRepository = groupRepository;
        this.userService = userService;
        this.slugify = slugify;
    }

    @Override
    public GetGroupResponse execute(String username, UpdateGroupRequest request) throws GenericException {
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
                .photo(Objects.nonNull(request.getPhoto()) && !request.getPhoto().equals(group.getPhoto())
                        ? cloudStorageService.upload(request.getPhoto()) : group.getPhoto())
                .name(request.getName())
                .category(category)
                .id(group.getId())
                .build());

        return buildGetGroupResponse(updatedGroup, username);
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

}
