package usociety.manager.domain.service.group.impl;

import static com.amazonaws.util.StringUtils.COMMA_SEPARATOR;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;
import static usociety.manager.domain.util.Constants.FORBIDDEN_ACCESS;
import static usociety.manager.domain.util.Constants.GROUP_NOT_FOUND;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.slugify.Slugify;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.UpdateGroupRequest;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.category.CategoryService;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.group.UpdateGroupDelegate;

@Component
public class UpdateGroupDelegateImpl implements UpdateGroupDelegate {

    private static final String UPDATING_MEMBERSHIP_ERROR_CODE = "ERROR_UPDATING_MEMBERSHIP";

    private final UserGroupRepository userGroupRepository;
    private final CloudStorageService cloudStorageService;
    private final CategoryService categoryService;
    private final GroupRepository groupRepository;
    private final Slugify slugify;

    @Autowired
    public UpdateGroupDelegateImpl(UserGroupRepository userGroupRepository,
                                   CloudStorageService cloudStorageService,
                                   CategoryService categoryService,
                                   GroupRepository groupRepository,
                                   Slugify slugify) {
        this.userGroupRepository = userGroupRepository;
        this.cloudStorageService = cloudStorageService;
        this.categoryService = categoryService;
        this.groupRepository = groupRepository;
        this.slugify = slugify;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void execute(UserApi user, UpdateGroupRequest request) throws GenericException {
        Group group = getGroup(request.getId());

        UserGroup userGroup = getUserGroup(user.getId(), request.getId());
        if (!userGroup.isAdmin()) {
            throw new GenericException("Only admins can update groups", FORBIDDEN_ACCESS);
        }

        Category category = categoryService.get(request.getCategory().getId());
        groupRepository.save(Group.newBuilder()
                .objectives(removeCommas(request.getObjectives()))
                .slug(slugify.slugify(request.getName()))
                .rules(removeCommas(request.getRules()))
                .description(request.getDescription())
                .photo(getNewPhoto(request, group))
                .name(request.getName())
                .category(category)
                .id(group.getId())
                .build());

    }

    private UserGroup getUserGroup(Long userId, Long groupId) throws GenericException {
        return userGroupRepository
                .findByGroupIdAndUserIdAndStatus(groupId, userId, ACTIVE.getCode())
                .orElseThrow(
                        () -> new GenericException("User is not an active member", UPDATING_MEMBERSHIP_ERROR_CODE));
    }

    private String getNewPhoto(UpdateGroupRequest request, Group group) throws GenericException {
        boolean photoHasChanged = Objects.nonNull(request.getPhoto())
                && !request.getPhoto().equals(group.getPhoto());

        return photoHasChanged ? cloudStorageService.upload(request.getPhoto()) : group.getPhoto();
    }

    private List<String> removeCommas(List<String> values) {
        return values.stream()
                .map(value -> value.replace(COMMA_SEPARATOR, EMPTY))
                .collect(Collectors.toList());
    }

    private Group getGroup(Long id) throws GenericException {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GenericException("Group does not exist", GROUP_NOT_FOUND));
    }

}
