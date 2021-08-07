package usociety.manager.domain.service.survey.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.model.Survey;
import usociety.manager.domain.repository.SurveyRepository;
import usociety.manager.domain.service.common.impl.AbstractServiceImpl;
import usociety.manager.domain.service.post.PostService;
import usociety.manager.domain.service.post.dto.PostAdditionalData;
import usociety.manager.domain.service.post.dto.SurveyOption;
import usociety.manager.domain.service.survey.SurveyService;

@Service
public class SurveyServiceImpl extends AbstractServiceImpl implements SurveyService {

    private static final String VOTING_SURVEY_ERROR_CODE = "ERROR_INTERACTING_WITH_SURVEY";

    private static final int ONE = 1;

    private final SurveyRepository surveyRepository;
    private final PostService postService;

    @Autowired
    public SurveyServiceImpl(SurveyRepository surveyRepository,
                             @Lazy PostService postService) {
        this.surveyRepository = surveyRepository;
        this.postService = postService;
    }

    @Override
    public void validateIfUserHasAlreadyInteracted(String username, Post post) throws GenericException {
        UserApi user = getUser(username);
        Optional<Survey> optionalSurvey = surveyRepository.findByPostIdAndUserId(post.getId(), user.getId());
        if (optionalSurvey.isPresent()) {
            throw new GenericException("User has already participated in survey", VOTING_SURVEY_ERROR_CODE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(String username, Post post, Integer option) throws GenericException {
        UserApi user = getUser(username);

        PostApi postApi = Converter.post(post);
        PostAdditionalData postAdditionalData = postApi.getContent();

        validateSurveyConstraints(option, post, postAdditionalData);

        surveyRepository.save(Survey.newBuilder()
                .userId(user.getId())
                .post(post)
                .vote(option)
                .build());

        updatePostMetadata(option, postApi, postAdditionalData);
    }

    private void validateSurveyConstraints(Integer vote, Post post, PostAdditionalData postAdditionalData)
            throws GenericException {
        if (PostTypeEnum.SURVEY != postAdditionalData.getType()) {
            throw new GenericException("Post is not survey type", VOTING_SURVEY_ERROR_CODE);
        }
        if (post.getExpirationDate().isBefore(LocalDateTime.now(clock))) {
            throw new GenericException("The voting is closed", VOTING_SURVEY_ERROR_CODE);
        }
        if (vote >= postAdditionalData.getOptions().size()) {
            throw new GenericException("Invalid value vote", VOTING_SURVEY_ERROR_CODE);
        }
    }

    private void updatePostMetadata(Integer vote, PostApi postApi, PostAdditionalData postAdditionalData) {
        SurveyOption surveyOption = postAdditionalData.getOptions().get(vote);
        Integer amount = surveyOption.getAmount();
        surveyOption.setAmount(amount + ONE);
        postService.update(postApi);
    }

}
