package usociety.manager.domain.service.group.impl;

import static com.amazonaws.util.StringUtils.COMMA_SEPARATOR;
import static java.lang.Boolean.TRUE;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;
import static usociety.manager.domain.util.Constants.FORBIDDEN_ACCESS;
import static usociety.manager.domain.util.Constants.GROUP_NOT_FOUND;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.slugify.Slugify;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CreateOrUpdateGroupRequest;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.category.CategoryService;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.common.impl.AbstractDelegateImpl;
import usociety.manager.domain.service.email.SendAsyncEmailDelegate;
import usociety.manager.domain.service.group.CreateUpdateGroupHelper;

@Component
public class CreateUpdateGroupHelperImpl extends AbstractDelegateImpl implements CreateUpdateGroupHelper {

    private static final String GROUP_NAME_ERROR_FORMAT = "Group with name: %s already exists";
    private static final String UPDATING_MEMBERSHIP_ERROR_CODE = "ERROR_UPDATING_MEMBERSHIP";
    private static final String CREATING_GROUP_ERROR_CODE = "ERROR_CREATING_GROUP";
    private static final String ADMINISTRATOR_ROLE = "Administrator";

    private final SendAsyncEmailDelegate sendAsyncEmailDelegate;
    private final UserGroupRepository userGroupRepository;
    private final CloudStorageService cloudStorageService;
    private final CategoryService categoryService;
    private final GroupRepository groupRepository;
    private final Slugify slugify;

    @Autowired
    public CreateUpdateGroupHelperImpl(UserGroupRepository userGroupRepository,
                                       CloudStorageService cloudStorageService,
                                       SendAsyncEmailDelegate sendAsyncEmailDelegate,
                                       CategoryService categoryService,
                                       GroupRepository groupRepository,
                                       Slugify slugify) {
        this.userGroupRepository = userGroupRepository;
        this.cloudStorageService = cloudStorageService;
        this.sendAsyncEmailDelegate = sendAsyncEmailDelegate;
        this.categoryService = categoryService;
        this.groupRepository = groupRepository;
        this.slugify = slugify;
    }

    @Override
    @Transactional(dontRollbackOn = GenericException.class, rollbackOn = Exception.class)
    public GroupApi create(UserApi user, CreateOrUpdateGroupRequest request)
            throws GenericException {
        validateExistingGroup(request);

        Category category = categoryService.get(request.getCategory().getId());
        String photoUrl = cloudStorageService.upload(request.getPhoto());

        Group savedGroup;
        try {
            Group.Builder groupBuilder = Group.newBuilder().createdAt(LocalDate.now(clock));
            savedGroup = saveGroup(request, groupBuilder, category, photoUrl);

            associateUserToGroup(user, savedGroup);
        } catch (Exception ex) {
            cloudStorageService.delete(photoUrl);
            throw new GenericException("Unexpected error creating group", CREATING_GROUP_ERROR_CODE, ex);
        }

        sendAsyncEmailDelegate.execute(user, savedGroup, category);
        return Converter.group(savedGroup);
    }

    @Override
    @Transactional(dontRollbackOn = GenericException.class, rollbackOn = Exception.class)
    public GroupApi update(UserApi user, Long id, CreateOrUpdateGroupRequest request) throws GenericException {
        Group group = getGroup(id);
        validateExistingGroup(request);

        UserGroup userGroup = getUserGroup(user.getId(), id);
        if (!userGroup.isAdmin()) {
            throw new GenericException("Only admins can perform this operation", FORBIDDEN_ACCESS);
        }

        Category category = categoryService.get(request.getCategory().getId());
        String photoUrl = getPhoto(request, group);
        Group.Builder groupBuilder = Group.newBuilder().id(group.getId()).updatedAt(LocalDate.now(clock));
        Group updatedGroup = saveGroup(request,
                groupBuilder,
                category,
                photoUrl);

        return Converter.group(updatedGroup);
    }

    private void validateExistingGroup(CreateOrUpdateGroupRequest request) throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findByName(request.getName());
        if (optionalGroup.isPresent()) {
            String errorMessage = String.format(GROUP_NAME_ERROR_FORMAT, request.getName());
            throw new GenericException(errorMessage, CREATING_GROUP_ERROR_CODE);
        }
    }

    private Group saveGroup(CreateOrUpdateGroupRequest request,
                            Group.Builder builder,
                            Category category,
                            String photoUrl) {
        return groupRepository.save(builder
                .objectives(removeCommasAndCapitalize(request.getObjectives()))
                .rules(removeCommasAndCapitalize(request.getRules()))
                .slug(slugify.slugify(request.getName()))
                .description(request.getDescription())
                .name(request.getName())
                .category(category)
                .photo(photoUrl)
                .build());
    }

    //This is made due to database's column format
    private List<String> removeCommasAndCapitalize(List<String> values) {
        return values.stream()
                .map(value -> value.replace(COMMA_SEPARATOR, EMPTY))
                .map(StringUtils::capitalize)
                .filter(value -> !StringUtils.isEmpty(value))
                .collect(Collectors.toList());
    }

    private void associateUserToGroup(UserApi userApi, Group savedGroup) {
        userGroupRepository.save(UserGroup.newBuilder()
                .status(ACTIVE.getValue())
                .role(ADMINISTRATOR_ROLE)
                .userId(userApi.getId())
                .group(savedGroup)
                .isAdmin(TRUE)
                .build());
    }

    private UserGroup getUserGroup(Long userId, Long groupId) throws GenericException {
        return userGroupRepository
                .findByGroupIdAndUserIdAndStatus(groupId, userId, ACTIVE.getValue())
                .orElseThrow(
                        () -> new GenericException("User is not an active member", UPDATING_MEMBERSHIP_ERROR_CODE));
    }

    private String getPhoto(CreateOrUpdateGroupRequest request, Group group) throws GenericException {
        if (Objects.nonNull(request.getPhoto()) && !request.getPhoto().equals(group.getPhoto())) {
            cloudStorageService.delete(group.getPhoto());
            return cloudStorageService.upload(request.getPhoto());
        }
        return group.getPhoto();
    }

    private Group getGroup(Long id) throws GenericException {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GenericException("Group does not exist", GROUP_NOT_FOUND));
    }

}
