package usociety.manager.domain.service.survey.impl;

import static org.mockito.ArgumentMatchers.any;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.app.api.GroupApiFixture;
import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.UserApiFixture;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.model.Survey;
import usociety.manager.domain.repository.SurveyRepository;
import usociety.manager.domain.service.post.PostService;
import usociety.manager.domain.service.post.dto.PostAdditionalData;
import usociety.manager.domain.service.post.dto.SurveyOption;
import usociety.manager.domain.service.user.UserService;
import usociety.manager.domain.util.mapper.CustomObjectMapper;
import usociety.manager.domain.util.mapper.impl.CustomObjectMapperImpl;

@RunWith(MockitoJUnitRunner.class)
public class SurveyServiceImplTest {

    @Mock
    private SurveyRepository surveyRepository;
    @Mock
    private UserService userService;
    @Mock
    private PostService postService;
    @Mock
    private Clock clock;

    @InjectMocks
    private SurveyServiceImpl subject;

    private PostAdditionalData postAdditionalData;
    private CustomObjectMapper objectMapper;
    private Post post;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(subject, "userService", userService);
        ReflectionTestUtils.setField(subject, "clock", clock);

        Mockito.when(userService.get(any())).thenReturn(UserApiFixture.defaultValue);
        Mockito.when(clock.instant())
                .thenReturn(LocalDateTime.of(2021, 8, 5, 0, 0).toInstant(ZoneOffset.UTC));
        Mockito.when(clock.getZone()).thenReturn(ZoneId.of("GMT"));

        postAdditionalData = PostAdditionalData.newBuilder()
                .type(PostTypeEnum.SURVEY)
                .value("Survey example")
                .options(Collections.singletonList(SurveyOption.newBuilder()
                        .value("Survey choice")
                        .amount(7)
                        .id(51)
                        .build()))
                .build();
        objectMapper = new CustomObjectMapperImpl();

        String surveyContent = toJson();

        post = Post.newBuilder()
                .content(surveyContent)
                .expirationDate(LocalDateTime.of(2021, 8, 6, 0, 0))
                .creationDate(LocalDateTime.of(2021, 8, 4, 0, 0))
                .group(Group.newBuilder().id(GroupApiFixture.id).build())
                .description("Post description")
                .userId(UserApiFixture.id)
                .isPublic(Boolean.FALSE)
                .id(123L)
                .build();
    }

    @Test
    public void shouldCreateSurveyCorrectly() throws GenericException {
        Integer vote = 0;
        subject.create(UserApiFixture.username, post, vote);

        Mockito.verify(userService).get(UserApiFixture.username);

        ArgumentCaptor<Survey> argumentCaptor = ArgumentCaptor.forClass(Survey.class);
        Mockito.verify(surveyRepository).save(argumentCaptor.capture());
        Survey savedSurvey = argumentCaptor.getValue();
        Assert.assertEquals(UserApiFixture.id, savedSurvey.getUserId());
        Assert.assertEquals(post, savedSurvey.getPost());
        Assert.assertEquals(vote, savedSurvey.getVote());

        postAdditionalData.setOptions(Collections.singletonList(SurveyOption.newBuilder()
                .value("Survey choice")
                .amount(8)
                .id(51)
                .build()));
        Mockito.verify(postService).update(PostApi.newBuilder()
                .content(postAdditionalData)
                .expirationDate(LocalDateTime.of(2021, 8, 6, 0, 0))
                .creationDate(LocalDateTime.of(2021, 8, 4, 0, 0))
                .description("Post description")
                .isPublic(Boolean.FALSE)
                .id(123L)
                .build());
    }

    @Test(expected = GenericException.class)
    public void shouldFailVotingIfIsNotASurvey() throws GenericException {
        postAdditionalData.setType(PostTypeEnum.IMAGE);
        post.setContent(toJson());
        try {
            subject.create(UserApiFixture.username, post, 0);
        } catch (GenericException e) {
            Assert.assertEquals("ERROR_INTERACTING_WITH_SURVEY", e.getErrorCode());
            Assert.assertEquals("Post is not survey type", e.getMessage());
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = GenericException.class)
    public void shouldFailVotingIfIsSurveyIsClosed() throws GenericException {
        post.setExpirationDate(LocalDateTime.of(2021, 8, 4, 8, 0));
        try {
            subject.create(UserApiFixture.username, post, 0);
        } catch (GenericException e) {
            Assert.assertEquals("ERROR_INTERACTING_WITH_SURVEY", e.getErrorCode());
            Assert.assertEquals("The survey is closed", e.getMessage());
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = GenericException.class)
    public void shouldFailVotingIfIsSurveyIfVoteIsInvalid() throws GenericException {
        try {
            subject.create(UserApiFixture.username, post, 1);
        } catch (GenericException e) {
            Assert.assertEquals("ERROR_INTERACTING_WITH_SURVEY", e.getErrorCode());
            Assert.assertEquals("Invalid value vote", e.getMessage());
            throw e;
        }
        Assert.fail();
    }

    @Test
    public void shouldValidateIsUserHasAlreadyInteractedWithASurveyCorrectly() throws GenericException {
        subject.validateIfUserHasAlreadyInteracted(UserApiFixture.username, post);
        Mockito.verify(surveyRepository).findByPostIdAndUserId(post.getId(), UserApiFixture.id);
    }

    @Test(expected = GenericException.class)
    public void shouldFailValidatingIsUserHasAlreadyInteractedWithASurvey() throws GenericException {
        Mockito.when(surveyRepository.findByPostIdAndUserId(any(), any())).thenReturn(Optional.of(new Survey()));
        try {
            subject.validateIfUserHasAlreadyInteracted(UserApiFixture.username, post);
        } catch (GenericException e) {
            Assert.assertEquals("ERROR_INTERACTING_WITH_SURVEY", e.getErrorCode());
            throw e;
        }
        Assert.fail();
    }

    private String toJson() {
        try {
            return objectMapper.writeValueAsString(postAdditionalData);
        } catch (JsonProcessingException ignored) {
        }
        return null;
    }

}