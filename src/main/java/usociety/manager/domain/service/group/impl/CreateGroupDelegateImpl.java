package usociety.manager.domain.service.group.impl;

import static com.amazonaws.util.StringUtils.COMMA_SEPARATOR;
import static java.lang.Boolean.TRUE;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.slugify.Slugify;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CreateGroupRequest;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.category.CategoryService;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.email.SendAsyncEmailDelegate;
import usociety.manager.domain.service.group.CreateGroupDelegate;

@Component
public class CreateGroupDelegateImpl implements CreateGroupDelegate {

    private static final String GROUP_NAME_ERROR_FORMAT = "Group with name: %s already exists";
    private static final String CREATING_GROUP_ERROR_CODE = "ERROR_CREATING_GROUP";
    private static final String ADMINISTRATOR_ROLE = "Administrator";

    private final SendAsyncEmailDelegate sendAsyncEmailDelegate;
    private final UserGroupRepository userGroupRepository;
    private final CloudStorageService cloudStorageService;
    private final CategoryService categoryService;
    private final GroupRepository groupRepository;
    private final Slugify slugify;

    @Autowired
    public CreateGroupDelegateImpl(UserGroupRepository userGroupRepository,
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
    public GroupApi execute(UserApi user, CreateGroupRequest request)
            throws GenericException {
        validateExistingGroup(request);

        Category category = categoryService.get(request.getCategory().getId());
        String photoUrl = cloudStorageService.upload(request.getPhoto());

        Group savedGroup;
        try {
            savedGroup = saveGroup(request, category, photoUrl);
            associateUserToGroup(user, savedGroup);
        } catch (Exception ex) {
            cloudStorageService.delete(photoUrl);
            throw new GenericException("Unexpected error creating group", CREATING_GROUP_ERROR_CODE);
        }

        sendAsyncEmailDelegate.execute(user, savedGroup, category);
        return Converter.group(savedGroup);
    }

    private void validateExistingGroup(CreateGroupRequest request) throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findByName(request.getName());
        if (optionalGroup.isPresent()) {
            String errorMessage = String.format(GROUP_NAME_ERROR_FORMAT, request.getName());
            throw new GenericException(errorMessage, CREATING_GROUP_ERROR_CODE);
        }
    }

    private Group saveGroup(CreateGroupRequest request, Category category, String photoUrl) {
        return groupRepository.save(Group.newBuilder()
                .objectives(removeCommasAndCapitalize(request.getObjectives()))
                .slug(slugify.slugify(request.getName()))
                .rules(removeCommasAndCapitalize(request.getRules()))
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

}
