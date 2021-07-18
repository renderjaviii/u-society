package usociety.manager.domain.service.group.impl;

import static com.amazonaws.util.StringUtils.COMMA_SEPARATOR;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;
import static usociety.manager.domain.enums.UserGroupStatusEnum.PENDING;
import static usociety.manager.domain.util.Constant.FORBIDDEN_ACCESS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

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
import usociety.manager.domain.service.category.CategoryService;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.common.impl.AbstractDelegateImpl;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.group.UpdateGroupDelegate;

@Component
public class UpdateGroupDelegateImpl extends AbstractDelegateImpl implements UpdateGroupDelegate {

    private static final String ERROR_UPDATING_MEMBERSHIP_ERROR_CODE = "ERROR_UPDATING_MEMBERSHIP";

    private final UserGroupRepository userGroupRepository;
    private final CloudStorageService cloudStorageService;
    private final CategoryService categoryService;
    private final GroupRepository groupRepository;
    private final GroupService groupService;
    private final Slugify slugify;

    @Autowired
    public UpdateGroupDelegateImpl(UserGroupRepository userGroupRepository,
                                   CloudStorageService cloudStorageService,
                                   CategoryService categoryService,
                                   GroupRepository groupRepository,
                                   GroupService groupService, Slugify slugify) {
        this.userGroupRepository = userGroupRepository;
        this.cloudStorageService = cloudStorageService;
        this.categoryService = categoryService;
        this.groupRepository = groupRepository;
        this.groupService = groupService;
        this.slugify = slugify;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public GetGroupResponse execute(String username, UpdateGroupRequest request) throws GenericException {
        Group group = groupService.get(request.getId());

        UserApi user = getUser(username);
        UserGroup userGroup = getUserGroup(user.getId(), request.getId());
        if (!userGroup.isAdmin()) {
            throw new GenericException("No eres administrador de este grupo.", FORBIDDEN_ACCESS);
        }

        Category category = categoryService.get(request.getCategory().getId());
        Group updatedGroup = groupRepository.save(Group.newBuilder()
                .objectives(removeCommas(request.getObjectives()))
                .slug(slugify.slugify(request.getName()))
                .rules(removeCommas(request.getRules()))
                .description(request.getDescription())
                .photo(getNewPhoto(request, group))
                .name(request.getName())
                .category(category)
                .id(group.getId())
                .build());

        return buildResponse(user, updatedGroup);
    }

    private UserGroup getUserGroup(Long userId, Long groupId) throws GenericException {
        return userGroupRepository.findByGroupIdAndUserIdAndStatus(groupId, userId, ACTIVE.getCode())
                .orElseThrow(() -> new GenericException("El usuario no es miembro activo del grupo.",
                        ERROR_UPDATING_MEMBERSHIP_ERROR_CODE));
    }

    private String getNewPhoto(UpdateGroupRequest request, Group group) throws GenericException {
        boolean photoHasNotChanged = Objects.nonNull(request.getPhoto())
                && !request.getPhoto().equals(group.getPhoto());

        return photoHasNotChanged ? cloudStorageService.upload(request.getPhoto()) : group.getPhoto();
    }

    private List<String> removeCommas(List<String> values) {
        return values.stream()
                .map(value -> value.replace(COMMA_SEPARATOR, EMPTY))
                .collect(Collectors.toList());
    }

    private GetGroupResponse buildResponse(UserApi user, Group group) throws GenericException {
        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByGroupIdAndUserId(group.getId(), user.getId());

        GetGroupResponse.Builder builder = GetGroupResponse.newBuilder();

        if (optionalUserGroup.isPresent()) {
            UserGroup userGroup = optionalUserGroup.get();

            UserGroupStatusEnum userGroupStatus = UserGroupStatusEnum.fromCode(userGroup.getStatus());
            if (ACTIVE == userGroupStatus) {
                List<UserGroup> groupMembers = userGroupRepository
                        .findAllByGroupIdAndUserIdNot(group.getId(), user.getId());

                builder.pendingMembers(getPendingMembers(userGroup, groupMembers))
                        .activeMembers(getActiveMembers(groupMembers))
                        .isAdmin(userGroup.isAdmin());
            }
            builder.membershipStatus(userGroupStatus);
        }

        return builder
                .group(Converter.group(group))
                .build();
    }

    private List<UserApi> getPendingMembers(UserGroup userGroup, List<UserGroup> groupMembers) throws GenericException {
        return userGroup.isAdmin()
                ? getMembersFilteredByStatus(groupMembers, PENDING)
                : Collections.emptyList();
    }

    private List<UserApi> getActiveMembers(List<UserGroup> groupMembers) throws GenericException {
        return getMembersFilteredByStatus(groupMembers, ACTIVE);
    }

    private List<UserApi> getMembersFilteredByStatus(List<UserGroup> userGroupList, UserGroupStatusEnum status)
            throws GenericException {
        List<UserGroup> membersGroup = userGroupList
                .stream()
                .filter(userGroup -> status.getCode() == userGroup.getStatus())
                .collect(Collectors.toList());

        List<UserApi> users = new ArrayList<>();
        for (UserGroup userGroup : membersGroup) {
            UserApi user = userService.getById(userGroup.getUserId());
            user.setRole(userGroup.getRole());
            users.add(user);
        }
        return users;
    }

}
