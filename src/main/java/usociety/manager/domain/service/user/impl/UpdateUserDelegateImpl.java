package usociety.manager.domain.service.user.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.UpdateUserRequest;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.UserCategory;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.repository.CategoryRepository;
import usociety.manager.domain.repository.UserCategoryRepository;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.user.UpdateUserDelegate;

@Component
public class UpdateUserDelegateImpl implements UpdateUserDelegate {

    private final UserCategoryRepository userCategoryRepository;
    private final CloudStorageService cloudStorageService;
    private final CategoryRepository categoryRepository;
    private final UserConnector userConnector;

    @Autowired
    public UpdateUserDelegateImpl(UserCategoryRepository userCategoryRepository,
                                  CloudStorageService cloudStorageService,
                                  CategoryRepository categoryRepository,
                                  UserConnector userConnector) {
        this.userCategoryRepository = userCategoryRepository;
        this.cloudStorageService = cloudStorageService;
        this.categoryRepository = categoryRepository;
        this.userConnector = userConnector;
    }

    @Override
    public UserApi execute(String username, UpdateUserRequest request) throws GenericException {
        UserDTO user = userConnector.get(username);
        user.setName(StringUtils.defaultString(request.getName(), user.getName()));
        user.setPhoto(processPhotoAndGetUrl(user, request));

        Set<Long> categorySet = request.getCategoryList();
        if (!CollectionUtils.isEmpty(categorySet)) {
            List<UserCategory> userCategoryList = userCategoryRepository.findAllByUserId(user.getId());
            updateUserCategories(user, categorySet, userCategoryList);
        }

        return Converter.user(userConnector.update(user));
    }

    private String processPhotoAndGetUrl(UserDTO user, UpdateUserRequest request)
            throws GenericException {
        String currentUserPhoto = user.getPhoto();
        if (StringUtils.isNotEmpty(request.getPhoto()) && !request.getPhoto().equals(user.getPhoto())) {
            if (StringUtils.isNotEmpty(currentUserPhoto)) {
                cloudStorageService.delete(currentUserPhoto);
            }
            return cloudStorageService.uploadImage(request.getPhoto());
        }
        return currentUserPhoto;
    }

    private void updateUserCategories(UserDTO user, Set<Long> categorySet, List<UserCategory> userCategoryList)
            throws GenericException {
        userCategoryList.forEach(userCategory -> {
            Long id = userCategory.getCategory().getId();
            if (categorySet.contains(id)) {
                categorySet.remove(id);
            } else {
                userCategoryRepository.delete(userCategory);
            }
        });

        for (Long categoryId : categorySet) {
            userCategoryRepository.save(new UserCategory(user.getId(), getCategoryEntity(categoryId)));
        }
    }

    private Category getCategoryEntity(Long id) throws GenericException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new GenericException(String.format("Category with code: %s does not exist", id)));
    }

}
