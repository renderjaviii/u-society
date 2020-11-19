package usociety.manager.domain.service.post.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import usociety.manager.app.api.CommentApi;
import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.app.rest.request.CreatePostRequest;
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
import usociety.manager.domain.service.common.impl.CommonServiceImpl;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.post.PostService;
import usociety.manager.domain.service.post.dto.PostAdditionalData;
import usociety.manager.domain.service.post.dto.SurveyOption;
import usociety.manager.domain.service.user.UserService;
import usociety.manager.domain.util.PageableUtils;

@Service
public class PostServiceImpl extends CommonServiceImpl implements PostService {

    private static final String REACTING_IN_POST_ERROR_CODE = "ERROR_REACTING_IN_POST";
    private static final String REACTING_POST_ERROR_CODE = "ERROR_REACTING_TO_POST";
    private static final String VOTING_SURVEY_ERROR_CODE = "ERROR_VOTING_INTO_POST";
    private static final String CREATING_POST_ERROR_CODE = "ERROR_CREATING_POST";
    private static final String GETTING_POST_ERROR_CODE = "POST_NOT_FOUND";
    private static final int ZERO = 0;
    private static final int ONE = 1;

    private final UserGroupRepository userGroupRepository;
    private final CommentRepository commentRepository;
    private final SurveyRepository surveyRepository;
    private final ReactRepository reactRepository;
    private final PostRepository postRepository;
    private final GroupService groupService;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final S3Service s3Service;

    @Autowired
    public PostServiceImpl(UserGroupRepository userGroupRepository,
                           CommentRepository commentRepository,
                           SurveyRepository surveyRepository,
                           ReactRepository reactRepository,
                           PostRepository postRepository,
                           GroupService groupService,
                           UserService userService, S3Service s3Service) {
        this.userGroupRepository = userGroupRepository;
        this.commentRepository = commentRepository;
        this.surveyRepository = surveyRepository;
        this.reactRepository = reactRepository;
        this.postRepository = postRepository;
        this.groupService = groupService;
        this.userService = userService;
        this.s3Service = s3Service;
        objectMapper = new ObjectMapper();
    }

    @Override
    public PostApi create(String username, CreatePostRequest request, MultipartFile image)
            throws GenericException, JsonProcessingException {
        validateIfUserActiveIsMember(username, request.getGroupId(), CREATING_POST_ERROR_CODE);
        if (PostTypeEnum.IMAGE == request.getContent().getType() && Objects.isNull(image)) {
            throw new GenericException("Es obligatorio que envíes la imagen.", CREATING_POST_ERROR_CODE);
        }

        processContent(request, image);
        PostApi postApi = Converter.post(postRepository.save(Post.newBuilder()
                .group(groupService.get(request.getGroupId()))
                .creationDate(LocalDateTime.now(clock))
                .expirationDate(request.getExpirationDate())
                .isPublic(PostTypeEnum.SURVEY == request.getContent().getType() ? FALSE : request.isPublic())
                .content(objectMapper.writeValueAsString(request.getContent()))
                .description(request.getDescription())
                .build()));
        postApi.setGroup(null);
        return postApi;
    }

    @Override
    public List<PostApi> getAll(String username, Long groupId, int page) throws GenericException {
        UserApi user = getUser(username);
        Optional<UserGroup> optionalUserGroup = userGroupRepository
                .findByGroupIdAndUserIdAndStatus(groupId, user.getId(), ACTIVE.getCode());

        boolean isGroupMember = optionalUserGroup.isPresent();

        List<Post> posts = postRepository
                .findAllByGroupIdOrderByCreationDateDesc(groupId, PageableUtils.paginate(page));
        if (!isGroupMember) {
            posts = posts.stream()
                    .filter(post -> TRUE.equals(post.isPublic()))
                    .collect(Collectors.toList());
        }

        List<PostApi> responseList = new ArrayList<>();
        for (Post post : posts) {
            PostApi postApi = Converter.post(post);

            if (isGroupMember) {
                if (PostTypeEnum.SURVEY == postApi.getContent().getType()) {
                    Optional<Survey> optionalSurvey = surveyRepository
                            .findByPostIdAndUserId(post.getId(), user.getId());
                    if (optionalSurvey.isPresent()) {
                        Survey survey = optionalSurvey.get();
                        postApi.setSelectedOptionId(survey.getVote());
                    }
                } else {
                    processReacts(postApi, reactRepository.findAllByPostId(post.getId()), user);
                    List<CommentApi> commentApiList = new ArrayList<>();
                    for (Comment comment : commentRepository.findByPostId(post.getId())) {
                        commentApiList.add(buildCommentInfo(comment));
                    }
                    postApi.setComments(commentApiList);
                }
            }
            postApi.setGroup(null);
            responseList.add(postApi);
        }
        return responseList;
    }

    @Override
    public void react(String username, Long postId, ReactTypeEnum value) throws GenericException {
        Post post = getPost(postId);
        try {
            PostAdditionalData postAdditionalData = objectMapper.readValue(post.getContent(), PostAdditionalData.class);
            if (PostTypeEnum.SURVEY == postAdditionalData.getType()) {
                throw new GenericException("No es posible reaccionar a posts de tipo encuesta.",
                        REACTING_IN_POST_ERROR_CODE);
            }
        } catch (JsonProcessingException e) {
            throw new GenericException("Información de post corrupta.", REACTING_IN_POST_ERROR_CODE);
        }

        UserApi user = getUser(username);
        validateIfUserActiveIsMember(username, post.getGroup().getId(), REACTING_POST_ERROR_CODE);

        Optional<React> optionalReact = reactRepository.findAllByPostIdAndUserId(postId, user.getId());
        if (optionalReact.isPresent()) {
            React savedReact = optionalReact.get();
            savedReact.setValue(value.getCode());
            reactRepository.save(savedReact);
        } else {
            reactRepository.save(React.newBuilder()
                    .post(post)
                    .userId(user.getId())
                    .value(value.getCode())
                    .build());
        }
    }

    @Override
    public void comment(String username, Long postId, CommentPostRequest request) throws GenericException {
        Post post = getPost(postId);
        try {
            PostAdditionalData postAdditionalData = objectMapper.readValue(post.getContent(), PostAdditionalData.class);
            if (PostTypeEnum.SURVEY == postAdditionalData.getType()) {
                throw new GenericException("No es posible comentar en posts de tipo encuesta.",
                        REACTING_IN_POST_ERROR_CODE);
            }
        } catch (JsonProcessingException e) {
            throw new GenericException("Información de post corrupta.", REACTING_IN_POST_ERROR_CODE);
        }

        validateIfUserActiveIsMember(username, post.getGroup().getId(), REACTING_POST_ERROR_CODE);

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
        validateIfUserActiveIsMember(username, post.getGroup().getId(), VOTING_SURVEY_ERROR_CODE);

        PostApi postApi = Converter.post(post);
        PostAdditionalData postAdditionalData = postApi.getContent();

        if (PostTypeEnum.SURVEY != postAdditionalData.getType()) {
            throw new GenericException("Post no es de tipo encuesta.", VOTING_SURVEY_ERROR_CODE);
        }
        if (post.getExpirationDate().isBefore(LocalDateTime.now(clock))) {
            throw new GenericException("La votación ya se encuentra cerrada", VOTING_SURVEY_ERROR_CODE);
        }
        if (vote >= postAdditionalData.getOptions().size()) {
            throw new GenericException("Voto no válido.", VOTING_SURVEY_ERROR_CODE);
        }

        UserApi user = getUser(username);
        Optional<Survey> optionalSurvey = surveyRepository.findByPostIdAndUserId(postId, user.getId());
        if (optionalSurvey.isPresent()) {
            throw new GenericException("El usuario ya participó en esta encuesta.", VOTING_SURVEY_ERROR_CODE);
        }

        surveyRepository.save(Survey.newBuilder()
                .userId(user.getId())
                .post(post)
                .vote(vote)
                .build());

        SurveyOption surveyOption = postAdditionalData.getOptions().get(vote);
        Integer amount = surveyOption.getAmount();
        surveyOption.setAmount(amount + 1);
        postRepository.save(Converter.post(postApi));
    }

    private void processContent(CreatePostRequest request, MultipartFile image) throws GenericException {
        PostAdditionalData content = request.getContent();
        if (PostTypeEnum.SURVEY == content.getType()) {
            List<SurveyOption> surveyOptions = content.getOptions();
            for (int index = 0; index < surveyOptions.size(); index++) {
                SurveyOption surveyOption = surveyOptions.get(index);
                surveyOption.setAmount(ZERO);
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

    private void processReacts(PostApi postApi, List<React> reacts, UserApi user) {
        EnumMap<ReactTypeEnum, Integer> reactTypeMap = new EnumMap<>(ReactTypeEnum.class);
        for (React react : reacts) {
            ReactTypeEnum reactType = ReactTypeEnum.fromCode(react.getValue());

            if (Objects.isNull(postApi.getSelectedReaction()) && react.getUserId().equals(user.getId())) {
                postApi.setSelectedReaction(reactType);
            }
            switch (reactType) {
                case LIKE:
                    processReact(reactTypeMap, ReactTypeEnum.LIKE);
                    break;
                case DISLIKE:
                    processReact(reactTypeMap, ReactTypeEnum.DISLIKE);
                    break;
                case ANGRY:
                    processReact(reactTypeMap, ReactTypeEnum.ANGRY);
                    break;
                default:
                    processReact(reactTypeMap, ReactTypeEnum.LAUGH);
                    break;
            }
        }

        postApi.setReacts(reactTypeMap);
    }

    private void processReact(EnumMap<ReactTypeEnum, Integer> map, ReactTypeEnum reactType) {
        if (map.containsKey(reactType)) {
            map.put(reactType, map.get(reactType) + ONE);
        } else {
            map.put(reactType, ONE);
        }
    }

    private CommentApi buildCommentInfo(Comment comment) throws GenericException {
        UserApi commentOwner = userService.getById(comment.getUserId());
        return CommentApi.newBuilder()
                .creationDate(comment.getCreationDate())
                .value(comment.getValue())
                .user(commentOwner)
                .build();
    }

}
