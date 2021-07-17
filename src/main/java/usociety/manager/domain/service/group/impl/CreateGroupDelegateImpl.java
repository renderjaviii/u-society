package usociety.manager.domain.service.group.impl;

import static java.lang.Boolean.TRUE;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import usociety.manager.domain.service.common.impl.CommonServiceImpl;
import usociety.manager.domain.service.email.AsyncEmailDelegate;
import usociety.manager.domain.service.group.CreateGroupDelegate;

@Component
public class CreateGroupDelegateImpl extends CommonServiceImpl implements CreateGroupDelegate {

    private static final String GROUP_NAME_ERROR_FORMAT = "Grupo con nombre: %s ya existe, prueba un nombre diferente.";
    private static final String CREATING_GROUP_ERROR_CODE = "ERROR_CREATING_GROUP";
    private static final String ADMINISTRATOR_ROLE = "Administrador";

    private final UserGroupRepository userGroupRepository;
    private final CloudStorageService cloudStorageService;
    private final AsyncEmailDelegate asyncEmailDelegate;
    private final CategoryService categoryService;
    private final GroupRepository groupRepository;
    private final Slugify slugify;

    @Autowired
    public CreateGroupDelegateImpl(UserGroupRepository userGroupRepository,
                                   CloudStorageService cloudStorageService,
                                   AsyncEmailDelegate asyncEmailDelegate,
                                   CategoryService categoryService,
                                   GroupRepository groupRepository,
                                   Slugify slugify) {
        this.userGroupRepository = userGroupRepository;
        this.cloudStorageService = cloudStorageService;
        this.asyncEmailDelegate = asyncEmailDelegate;
        this.categoryService = categoryService;
        this.groupRepository = groupRepository;
        this.slugify = slugify;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public GroupApi execute(String username, CreateGroupRequest request)
            throws GenericException {
        validateExistingGroup(request);

        Category category = categoryService.get(request.getCategory().getId());
        String photoUrl = cloudStorageService.upload(request.getPhoto());
        UserApi userApi = getUser(username);

        Group savedGroup;
        try {
            savedGroup = saveGroup(request, category, photoUrl);
            associateGroupWithUser(userApi, savedGroup);
        } catch (Exception ex) {
            cloudStorageService.delete(photoUrl);
            throw new GenericException("Error general creando grupo.", CREATING_GROUP_ERROR_CODE);
        }

        asyncEmailDelegate.send(userApi, savedGroup, category);
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
                .description(request.getDescription())
                .objectives(request.getObjectives().stream().map(this::removeCommas).collect(Collectors.toList()))
                .rules(request.getRules().stream().map(this::removeCommas).collect(Collectors.toList()))
                .slug(slugify.slugify(request.getName()))
                .name(request.getName())
                .category(category)
                .photo(photoUrl)
                .build());
    }

    private String removeCommas(String value) {
        return value.replace(",", EMPTY);
    }

    private void associateGroupWithUser(UserApi userApi, Group savedGroup) {
        userGroupRepository.save(UserGroup.newBuilder()
                .userId(userApi.getId())
                .role(ADMINISTRATOR_ROLE)
                .status(ACTIVE.getCode())
                .group(savedGroup)
                .isAdmin(TRUE)
                .build());
    }

}
