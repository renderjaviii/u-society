package usociety.manager.domain.service.survey.impl;

import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.model.Survey;
import usociety.manager.domain.repository.PostRepository;
import usociety.manager.domain.repository.SurveyRepository;
import usociety.manager.domain.service.common.impl.AbstractDelegateImpl;
import usociety.manager.domain.service.post.dto.PostAdditionalData;
import usociety.manager.domain.service.post.dto.SurveyOption;
import usociety.manager.domain.service.survey.SurveyService;

@Service
public class SurveyServiceImpl extends AbstractDelegateImpl implements SurveyService {

    private static final String VOTING_SURVEY_ERROR_CODE = "ERROR_INTERACTING_WITH_SURVEY";

    private static final int ONE = 1;

    private final SurveyRepository surveyRepository;
    private final PostRepository postRepository;

    @Autowired
    public SurveyServiceImpl(SurveyRepository surveyRepository,
                             PostRepository postRepository) {
        this.surveyRepository = surveyRepository;
        this.postRepository = postRepository;
    }

    @Override
    public void validateIfUserHasAlreadyInteracted(String username, Post post) throws GenericException {
        UserApi user = getUser(username);
        Optional<Survey> optionalSurvey = surveyRepository.findByPostIdAndUserId(post.getId(), user.getId());
        if (optionalSurvey.isPresent()) {
            throw new GenericException("El usuario ya participó en esta encuesta.", VOTING_SURVEY_ERROR_CODE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(String username, Post post, Integer vote) throws GenericException {
        UserApi user = getUser(username);
        validateIfUserIsMember(username, post.getGroup().getId(), ACTIVE, VOTING_SURVEY_ERROR_CODE);

        PostApi postApi = Converter.post(post);
        PostAdditionalData postAdditionalData = postApi.getContent();

        validateSurveyConstraints(vote, post, postAdditionalData);

        surveyRepository.save(Survey.newBuilder()
                .userId(user.getId())
                .post(post)
                .vote(vote)
                .build());

        updatePostMetadata(vote, postApi, postAdditionalData);
    }

    private void validateSurveyConstraints(Integer vote, Post post, PostAdditionalData postAdditionalData)
            throws GenericException {
        if (PostTypeEnum.SURVEY != postAdditionalData.getType()) {
            throw new GenericException("El post no es de tipo encuesta.", VOTING_SURVEY_ERROR_CODE);
        }
        if (post.getExpirationDate().isBefore(LocalDateTime.now(clock))) {
            throw new GenericException("La votación ya se encuentra cerrada", VOTING_SURVEY_ERROR_CODE);
        }
        if (vote >= postAdditionalData.getOptions().size()) {
            throw new GenericException("Voto no válido.", VOTING_SURVEY_ERROR_CODE);
        }
    }

    private void updatePostMetadata(Integer vote, PostApi postApi, PostAdditionalData postAdditionalData) {
        SurveyOption surveyOption = postAdditionalData.getOptions().get(vote);
        Integer amount = surveyOption.getAmount();
        surveyOption.setAmount(amount + ONE);
        postRepository.save(Converter.post(postApi));
    }

}
