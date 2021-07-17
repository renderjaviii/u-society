package usociety.manager.domain.service.group.impl;

import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;
import static usociety.manager.domain.enums.UserGroupStatusEnum.PENDING;

import java.util.ArrayList;
import java.util.Arrays;
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
import usociety.manager.domain.service.common.impl.CommonServiceImpl;
import usociety.manager.domain.service.group.GetGroupHelper;
import usociety.manager.domain.service.user.UserService;

@Component
public class GetGroupHelperImpl extends CommonServiceImpl implements GetGroupHelper {

    private static final String GETTING_GROUP_ERROR_CODE = "ERROR_GETTING_GROUP";

    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final UserService userService;

    @Autowired
    public GetGroupHelperImpl(UserGroupRepository userGroupRepository,
                              GroupRepository groupRepository,
                              UserService userService) {
        this.userGroupRepository = userGroupRepository;
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

    @Override
    public Group getById(Long id) throws GenericException {
        return getGroup(id);
    }

    @Override
    public GetGroupResponse get(String username, Long id) throws GenericException {
        Group group = getGroup(id);
        return buildGetGroupResponse(group, username);
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
    public GetGroupResponse getBySlug(String user, String slug) throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findBySlug(slug);
        if (!optionalGroup.isPresent()) {
            throw new GenericException("Grupo no encontrado.", GETTING_GROUP_ERROR_CODE);
        }

        Group group = optionalGroup.get();
        return buildGetGroupResponse(group, user);
    }

    @Override
    public List<GroupApi> getAllUserGroups(String username) throws GenericException {
        UserApi user = getUser(username);
        return userGroupRepository
                .findAllByUserIdAndStatusIn(user.getId(), Arrays.asList(ACTIVE.getCode(), PENDING.getCode()))
                .stream()
                .map(userGroup -> buildGroupResponse(userGroup.getGroup()))
                .collect(Collectors.toList());
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

    private Group getGroup(Long id) throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (!optionalGroup.isPresent()) {
            throw new GenericException(String.format("Grupo con id: %s no existe.", id), GETTING_GROUP_ERROR_CODE);
        }
        return optionalGroup.get();
    }

}
