package usociety.manager.domain.service.post.impl;

import static java.lang.Boolean.TRUE;
import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.CommentApi;
import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.UserApi;
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
import usociety.manager.domain.service.common.impl.AbstractDelegateImpl;
import usociety.manager.domain.service.post.GetAllGroupPostsDelegate;
import usociety.manager.domain.service.user.UserService;
import usociety.manager.domain.util.PageableUtils;

@Component
public class GetAllGroupPostsDelegateImpl extends AbstractDelegateImpl implements GetAllGroupPostsDelegate {

    private static final int ONE = 1;

    private final UserGroupRepository userGroupRepository;
    private final CommentRepository commentRepository;
    private final SurveyRepository surveyRepository;
    private final ReactRepository reactRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Autowired
    public GetAllGroupPostsDelegateImpl(UserGroupRepository userGroupRepository,
                                        CommentRepository commentRepository,
                                        SurveyRepository surveyRepository,
                                        ReactRepository reactRepository,
                                        PostRepository postRepository,
                                        UserService userService) {
        this.userGroupRepository = userGroupRepository;
        this.commentRepository = commentRepository;
        this.surveyRepository = surveyRepository;
        this.reactRepository = reactRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    @Override
    public List<PostApi> execute(UserApi user, Long groupId, int page) throws GenericException {
        Optional<UserGroup> optionalUserGroup = userGroupRepository
                .findByGroupIdAndUserIdAndStatus(groupId, user.getId(), ACTIVE.getValue());

        boolean isMember = optionalUserGroup.isPresent();
        List<Post> posts = getGroupPosts(groupId, isMember, page);

        List<PostApi> response = new ArrayList<>();
        for (Post post : posts) {
            PostApi postApi = Converter.post(post);
            if (isMember) {
                setPostMetadata(user, post, postApi);
            }

            response.add(postApi);
            postApi.setOwner(userService.getById(post.getUserId()));
        }
        return response;
    }

    private List<Post> getGroupPosts(Long groupId, boolean isMember, int page) {
        List<Post> posts = postRepository
                .findAllByGroupIdOrderByCreationDateDesc(groupId, PageableUtils.paginate(page));

        if (!isMember) {
            return posts.stream()
                    .filter(post -> TRUE.equals(post.isPublic()))
                    .collect(Collectors.toList());
        }
        return posts;
    }

    private void setPostMetadata(UserApi user, Post post, PostApi postApi) throws GenericException {
        if (PostTypeEnum.SURVEY == postApi.getContent().getType()) {
            Optional<Survey> optionalSurvey = surveyRepository.findByPostIdAndUserId(post.getId(), user.getId());

            if (optionalSurvey.isPresent()) {
                Survey survey = optionalSurvey.get();
                postApi.setSelectedOptionId(survey.getVote());
            }
        } else {
            processPostReacts(postApi, reactRepository.findAllByPostId(post.getId()), user);
            List<CommentApi> comments = new ArrayList<>();

            for (Comment comment : commentRepository.findByPostId(post.getId())) {
                comments.add(buildCompleteCommentInfo(comment));
            }
            postApi.setComments(comments);
        }
    }

    //TODO: Improve reacts storing and processing
    private void processPostReacts(PostApi postApi, List<React> reacts, UserApi user) {
        EnumMap<ReactTypeEnum, Integer> reactTypeMap = new EnumMap<>(ReactTypeEnum.class);
        for (React react : reacts) {
            final ReactTypeEnum reactType = ReactTypeEnum.valueOf(react.getValue());

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

    private CommentApi buildCompleteCommentInfo(Comment comment) throws GenericException {
        UserApi commentOwner = userService.getById(comment.getUserId());
        return CommentApi.newBuilder()
                .creationDate(comment.getCreationDate())
                .value(comment.getValue())
                .user(commentOwner)
                .build();
    }

}
