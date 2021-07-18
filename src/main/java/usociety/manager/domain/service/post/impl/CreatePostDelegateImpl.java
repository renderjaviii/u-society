package usociety.manager.domain.service.post.impl;

import static java.lang.Boolean.FALSE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CreatePostRequest;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.repository.PostRepository;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.common.impl.AbstractDelegateImpl;
import usociety.manager.domain.service.post.CreatePostDelegate;
import usociety.manager.domain.service.post.dto.PostAdditionalData;
import usociety.manager.domain.service.post.dto.SurveyOption;

@Component
public class CreatePostDelegateImpl extends AbstractDelegateImpl implements CreatePostDelegate {

    private static final String CREATING_POST_ERROR_CODE = "ERROR_CREATING_POST";

    private static final int ZERO = 0;

    private final CloudStorageService cloudStorageService;
    private final PostRepository postRepository;

    @Autowired
    public CreatePostDelegateImpl(CloudStorageService cloudStorageService,
                                  PostRepository postRepository) {
        this.cloudStorageService = cloudStorageService;
        this.postRepository = postRepository;
    }

    @Override
    public PostApi execute(UserApi user, CreatePostRequest request) throws GenericException {
        validateRequest(request);
        processContent(request);

        return Converter.post(postRepository.save(Post.newBuilder()
                .expirationDate(request.getExpirationDate())
                .creationDate(LocalDateTime.now(clock))
                .description(request.getDescription())
                .isPublic(getPostVisibility(request))
                .content(parseContentToJSON(request))
                .group(getGroup(request.getGroupId()))
                .userId(user.getId())
                .build()));
    }

    private void validateRequest(CreatePostRequest request) throws GenericException {
        if (PostTypeEnum.IMAGE == request.getContent().getType() && StringUtils.isEmpty(request.getImage())) {
            throw new GenericException("Es obligatorio que envíes imagen.", CREATING_POST_ERROR_CODE);
        }
    }

    private void processContent(CreatePostRequest request) throws GenericException {
        PostAdditionalData content = request.getContent();
        if (PostTypeEnum.SURVEY == content.getType()) {
            List<SurveyOption> surveyOptions = content.getOptions();
            for (int index = 0; index < surveyOptions.size(); index++) {
                SurveyOption surveyOption = surveyOptions.get(index);
                surveyOption.setAmount(ZERO);
                surveyOption.setId(index);
            }
        } else if (PostTypeEnum.IMAGE == content.getType()) {
            String imageUrl = cloudStorageService.upload(request.getImage());
            content.setValue(imageUrl);
        }
    }

    private boolean getPostVisibility(CreatePostRequest request) {
        return PostTypeEnum.SURVEY == request.getContent().getType() ? FALSE : request.isPublic();
    }

    private String parseContentToJSON(CreatePostRequest request) {
        try {
            return objectMapper.writeValueAsString(request.getContent());
        } catch (JsonProcessingException ignored) {
            //Implementation is no required
        }
        return EMPTY;
    }

}
