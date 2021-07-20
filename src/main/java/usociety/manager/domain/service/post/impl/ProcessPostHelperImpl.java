package usociety.manager.domain.service.post.impl;

import static java.lang.Boolean.FALSE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static usociety.manager.domain.util.Constants.GROUP_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CreatePostRequest;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.PostRepository;
import usociety.manager.domain.service.common.CloudStorageService;
import usociety.manager.domain.service.common.impl.AbstractDelegateImpl;
import usociety.manager.domain.service.post.ProcessPostHelper;
import usociety.manager.domain.service.post.dto.PostAdditionalData;
import usociety.manager.domain.service.post.dto.SurveyOption;

@Component
public class ProcessPostHelperImpl extends AbstractDelegateImpl implements ProcessPostHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPostHelperImpl.class);

    private static final String CREATING_POST_ERROR_CODE = "ERROR_CREATING_POST";

    private static final int ZERO = 0;

    private final CloudStorageService cloudStorageService;
    private final GroupRepository groupRepository;
    private final PostRepository postRepository;

    @Autowired
    public ProcessPostHelperImpl(CloudStorageService cloudStorageService,
                                 GroupRepository groupRepository,
                                 PostRepository postRepository) {
        this.cloudStorageService = cloudStorageService;
        this.groupRepository = groupRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(dontRollbackOn = GenericException.class, rollbackOn = Exception.class)
    public PostApi create(UserApi user, CreatePostRequest request) throws GenericException {
        Group group = getGroup(request.getGroupId());

        validateRequest(request);
        processContent(request);

        return Converter.post(postRepository.save(Post.newBuilder()
                .expirationDate(request.getExpirationDate())
                .creationDate(LocalDateTime.now(clock))
                .description(request.getDescription())
                .isPublic(getPostVisibility(request))
                .content(parseContentToJSON(request))
                .group(group)
                .userId(user.getId())
                .build()));
    }

    @Override
    @Transactional(dontRollbackOn = GenericException.class, rollbackOn = Exception.class)
    public void update(PostApi post) {
        postRepository.save(Converter.post(post));
    }

    private Group getGroup(Long id) throws GenericException {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GenericException("Group does not exist", GROUP_NOT_FOUND));
    }

    private void validateRequest(CreatePostRequest request) throws GenericException {
        if (PostTypeEnum.IMAGE == request.getContent().getType() && StringUtils.isEmpty(request.getImage())) {
            throw new GenericException("The post's image is required", CREATING_POST_ERROR_CODE);
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
        } catch (JsonProcessingException ex) {
            LOGGER.error("Error parsing JSON value", ex);
        }
        return EMPTY;
    }

}
