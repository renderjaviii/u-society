package usociety.manager.domain.service.post.impl;

import static usociety.manager.domain.util.Constants.USER_IS_NOT_MEMBER;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.app.rest.request.CreatePostRequest;
import usociety.manager.domain.enums.ReactTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.repository.PostRepository;
import usociety.manager.domain.service.comment.CommentService;
import usociety.manager.domain.service.common.impl.AbstractServiceImpl;
import usociety.manager.domain.service.post.GetAllGroupPostsDelegate;
import usociety.manager.domain.service.post.PostService;
import usociety.manager.domain.service.post.ProcessPostHelper;
import usociety.manager.domain.service.react.ReactService;
import usociety.manager.domain.service.survey.SurveyService;

@Service
public class PostServiceImpl extends AbstractServiceImpl implements PostService {

    private static final String POST_NOT_FOUND_ERROR_CODE = "POST_DOES_NOT_FOUND";

    private final GetAllGroupPostsDelegate getAllGroupPostsDelegate;
    private final ProcessPostHelper processPostHelper;
    private final CommentService commentService;
    private final PostRepository postRepository;
    private final SurveyService surveyService;
    private final ReactService reactService;

    @Autowired
    public PostServiceImpl(GetAllGroupPostsDelegate getAllGroupPostsDelegate,
                           ProcessPostHelper processPostHelper,
                           CommentService commentService,
                           PostRepository postRepository,
                           SurveyService surveyService,
                           ReactService reactService) {
        this.getAllGroupPostsDelegate = getAllGroupPostsDelegate;
        this.processPostHelper = processPostHelper;
        this.commentService = commentService;
        this.postRepository = postRepository;
        this.surveyService = surveyService;
        this.reactService = reactService;
    }

    @Override
    public PostApi create(String username, CreatePostRequest request)
            throws GenericException {
        validateIsUserIsMember(username, request.getGroupId());

        return processPostHelper.create(getUser(username), request);
    }

    @Override
    public List<PostApi> getAllByUserAndGroup(String username, Long groupId, int page) throws GenericException {
        return getAllGroupPostsDelegate.execute(getUser(username), groupId, page);
    }

    @Override
    public void update(PostApi post) {
        processPostHelper.update(post);
    }

    @Override
    public void react(String username, Long postId, ReactTypeEnum value) throws GenericException {
        Post post = getPost(postId);
        validateIsUserIsMember(username, post.getGroup().getId());

        reactService.create(username, post, value);
    }

    @Override
    public void comment(String username, Long postId, CommentPostRequest request) throws GenericException {
        commentService.create(username, getPost(postId), request);
    }

    @Override
    public void vote(String username, Long postId, Integer vote) throws GenericException {
        Post post = getPost(postId);
        validateIsUserIsMember(username, post.getGroup().getId());

        surveyService.validateIfUserHasAlreadyInteracted(username, post);
        surveyService.create(username, post, vote);
    }

    private Post getPost(Long postId) throws GenericException {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GenericException("Post does not exist", POST_NOT_FOUND_ERROR_CODE));
    }

    private void validateIsUserIsMember(String username, Long groupId) throws GenericException {
        validateIfUserIsMember(username, groupId, USER_IS_NOT_MEMBER);
    }

}
