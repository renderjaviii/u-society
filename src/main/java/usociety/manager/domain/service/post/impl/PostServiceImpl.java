package usociety.manager.domain.service.post.impl;

import static java.lang.Boolean.FALSE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.enums.ReactTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Comment;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.model.React;
import usociety.manager.domain.model.Survey;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.CommentRepository;
import usociety.manager.domain.repository.PostRepository;
import usociety.manager.domain.repository.ReactRepository;
import usociety.manager.domain.repository.SurveyRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.aws.s3.S3Service;
import usociety.manager.domain.service.common.CommonServiceImpl;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.post.PostService;
import usociety.manager.domain.service.post.dto.PostAdditionalData;
import usociety.manager.domain.service.post.dto.SurveyOption;

@Service
public class PostServiceImpl extends CommonServiceImpl implements PostService {

    private static final String REACTING_POST_ERROR_CODE = "ERROR_REACTING_TO_POST";
    private static final String VOTING_SURVEY_ERROR_CODE = "ERROR_VOTING_INTO_POST";
    private static final String CREATING_POST_ERROR_CODE = "ERROR_CREATING_POST";
    private static final String GETTING_POST_ERROR_CODE = "POST_NOT_FOUND";

    private final UserGroupRepository userGroupRepository;
    private final CommentRepository commentRepository;
    private final SurveyRepository surveyRepository;
    private final ReactRepository reactRepository;
    private final PostRepository postRepository;
    private final GroupService groupService;
    private final ObjectMapper objectMapper;
    private final S3Service s3Service;

    @Autowired
    public PostServiceImpl(UserGroupRepository userGroupRepository,
                           CommentRepository commentRepository,
                           SurveyRepository surveyRepository,
                           ReactRepository reactRepository,
                           PostRepository postRepository,
                           GroupService groupService,
                           S3Service s3Service) {
        this.userGroupRepository = userGroupRepository;
        this.commentRepository = commentRepository;
        this.surveyRepository = surveyRepository;
        this.reactRepository = reactRepository;
        this.postRepository = postRepository;
        this.groupService = groupService;
        this.s3Service = s3Service;
        objectMapper = new ObjectMapper();
    }

    @Override
    public PostApi create(String username, PostApi request, MultipartFile image)
            throws GenericException, JsonProcessingException {
        validateIfUserIsMember(username, request.getGroupId(), CREATING_POST_ERROR_CODE);

        processContent(request, image);
        return Converter.post(postRepository.save(Post.newBuilder()
                .group(groupService.get(request.getGroupId()))
                .creationDate(LocalDateTime.now(clock))
                .expirationDate(request.getExpirationDate())
                .isPublic(PostTypeEnum.SURVEY == request.getContent().getType() ? FALSE : request.isPublic())
                .content(objectMapper.writeValueAsString(request.getContent()))
                .description(request.getDescription())
                .build()));
    }

    @Override
    public List<PostApi> getAll(String username, Long groupId) throws GenericException {
        UserApi user = getUser(username);
        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByGroupIdAndUserId(groupId, user.getId());
        boolean isGroupMember = optionalUserGroup.isPresent();

        List<Post> posts = postRepository.findAllByGroupIdOrderByCreationDateAsc(groupId);
        if (!isGroupMember) {
            posts = posts.stream()
                    .filter(post -> Boolean.TRUE.equals(post.isPublic()))
                    .collect(Collectors.toList());
        }

        List<PostApi> responseList = new ArrayList<>();
        for (Post post : posts) {
            PostApi postApi = Converter.post(post);

            if (isGroupMember) {
                List<React> reacts = reactRepository.findAllByPostId(post.getId());
                List<Comment> comments = commentRepository.findByPostId(post.getId());
                postApi.setReacts(reacts.stream().map(Converter::react).collect(Collectors.toList()));
                postApi.setComments(comments.stream().map(Converter::comment).collect(Collectors.toList()));
            }
            responseList.add(postApi);
        }
        return responseList;
    }

    @Override
    public void react(String username, Long postId, ReactTypeEnum react) throws GenericException {
        Post post = getPost(postId);
        validateIfUserIsMember(username, post.getGroup().getId(), REACTING_POST_ERROR_CODE);
        UserApi user = getUser(username);

        Optional<React> optionalReact = reactRepository.findAllByPostIdAndUserId(postId, user.getId());
        if (optionalReact.isPresent()) {
            React savedReact = optionalReact.get();
            savedReact.setValue(react.getCode());
            reactRepository.save(savedReact);
        } else {
            reactRepository.save(React.newBuilder()
                    .post(post)
                    .userId(user.getId())
                    .value(react.getCode())
                    .build());
        }
    }

    @Override
    public void comment(String username, Long postId, CommentPostRequest request) throws GenericException {
        Post post = getPost(postId);
        validateIfUserIsMember(username, post.getGroup().getId(), REACTING_POST_ERROR_CODE);

        UserApi user = getUser(username);
        commentRepository.save(Comment.newBuilder()
                .creationDate(LocalDateTime.now(clock))
                .value(request.getComment())
                .userId(user.getId())
                .post(post)
                .build());

    }

    @Override
    public void interactWithSurvey(String username, Long postId, Integer vote) throws GenericException {
        Post post = getPost(postId);
        validateIfUserIsMember(username, post.getGroup().getId(), VOTING_SURVEY_ERROR_CODE);

        PostApi postApi = Converter.post(post);
        if (PostTypeEnum.SURVEY != postApi.getContent().getType()) {
            throw new GenericException("Post no es de tipo encuesta.", VOTING_SURVEY_ERROR_CODE);
        }
        if (vote >= postApi.getContent().getOptions().size()) {
            throw new GenericException("Voto no válido.", VOTING_SURVEY_ERROR_CODE);
        }

        UserApi user = getUser(username);
        Optional<Survey> optionalSurvey = surveyRepository.findByPostIdAndUserId(postId, user.getId());
        if (optionalSurvey.isPresent()) {
            Survey survey = optionalSurvey.get();
            survey.setVote(vote);
            surveyRepository.save(survey);
        } else {
            surveyRepository.save(Survey.newBuilder()
                    .userId(user.getId())
                    .post(post)
                    .vote(vote)
                    .build());
        }
    }

    private void processContent(PostApi request, MultipartFile image) throws GenericException {
        PostAdditionalData content = request.getContent();
        if (PostTypeEnum.SURVEY == content.getType()) {
            List<SurveyOption> surveyOptions = content.getOptions();
            for (int index = 0; index < surveyOptions.size(); index++) {
                SurveyOption surveyOption = surveyOptions.get(index);
                surveyOption.setId(index);
            }
        } else if (PostTypeEnum.IMAGE == content.getType()) {
            String imageUrl = s3Service.upload(image);
            content.setValue(imageUrl);
        }
    }

    private Post getPost(Long postId) throws GenericException {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GenericException("Post no encontrado.", GETTING_POST_ERROR_CODE));
    }

}
