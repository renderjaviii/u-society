package usociety.manager.domain.service.group.impl;

import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;
import static usociety.manager.domain.enums.UserGroupStatusEnum.PENDING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.common.impl.AbstractDelegateImpl;
import usociety.manager.domain.service.group.GetGroupHelper;

@Component
public class GetGroupHelperImpl extends AbstractDelegateImpl implements GetGroupHelper {

    private static final String GETTING_GROUP_ERROR_CODE = "ERROR_GETTING_GROUP";

    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public GetGroupHelperImpl(UserGroupRepository userGroupRepository,
                              GroupRepository groupRepository) {
        this.userGroupRepository = userGroupRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public Group byId(Long id) throws GenericException {
        return getGroupById(id);
    }

    @Override
    public Optional<UserGroup> byIdAndUser(Long id, String username) throws GenericException {
        UserApi user = getUser(username);
        return userGroupRepository.findByGroupIdAndUserId(id, user.getId());
    }

    @Override
    public GetGroupResponse byUserAndId(String username, Long id) throws GenericException {
        UserApi user = getUser(username);
        return buildCompleteGroupResponse(user, getGroupById(id));
    }

    @Override
    public List<GroupApi> byFilters(String name, Long categoryId) throws GenericException {
        validateFields(name, categoryId);

        return groupRepository.findDistinctByCategoryIdOrNameContainingIgnoreCase(categoryId, name)
                .stream()
                .distinct()
                .map(this::buildBasicGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GetGroupResponse byUserAndSlug(String username, String slug) throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findBySlug(slug);
        if (!optionalGroup.isPresent()) {
            throw new GenericException("Grupo no encontrado.", GETTING_GROUP_ERROR_CODE);
        }

        UserApi user = getUser(username);
        return buildCompleteGroupResponse(user, optionalGroup.get());
    }

    @Override
    public List<GroupApi> allUserGroups(String username) throws GenericException {
        UserApi user = getUser(username);

        return userGroupRepository
                .findAllByUserIdAndStatusIn(user.getId(), Arrays.asList(ACTIVE.getCode(), PENDING.getCode()))
                .stream()
                .map(userGroup -> buildBasicGroupResponse(userGroup.getGroup()))
                .collect(Collectors.toList());
    }

    private GetGroupResponse buildCompleteGroupResponse(UserApi user, Group group) throws GenericException {
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

    private List<UserApi> getMembersFilteredByStatus(List<UserGroup> userList, UserGroupStatusEnum userGroupStatus)
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

    private void validateFields(String name, Long categoryId) throws GenericException {
        if (StringUtils.isEmpty(name) && Objects.isNull(categoryId)) {
            throw new GenericException("Debe enviar el nombre del grupo y/o la categoría.", GETTING_GROUP_ERROR_CODE);
        }
    }

    private GroupApi buildBasicGroupResponse(Group group) {
        return GroupApi.newBuilder()
                .category(Converter.category(group.getCategory()))
                .description(group.getDescription())
                .photo(group.getPhoto())
                .name(group.getName())
                .slug(group.getSlug())
                .id(group.getId())
                .build();
    }

    @Override
    public Optional<UserGroup> validateIfUserIsMember(String username,
                                                      Long groupId,
                                                      UserGroupStatusEnum status,
                                                      String errorCode)
            throws GenericException {
        UserApi user = getUser(username);

        Optional<UserGroup> optionalUserGroup = userGroupRepository
                .findByGroupIdAndUserIdAndStatus(groupId, user.getId(), status.getCode());
        if (StringUtils.isNotEmpty(errorCode) && !optionalUserGroup.isPresent()) {
            throw new GenericException("User is not an active member", errorCode);
        }

        return optionalUserGroup;
    }

    private Group getGroupById(Long id) throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (!optionalGroup.isPresent()) {
            String errorMessage = String.format("Group with id: %s does not exist.", id);
            throw new GenericException(errorMessage, GETTING_GROUP_ERROR_CODE);
        }
        return optionalGroup.get();
    }

}
