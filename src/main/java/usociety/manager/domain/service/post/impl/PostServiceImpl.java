package usociety.manager.domain.service.post.impl;

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
import usociety.manager.domain.service.common.impl.AbstractDelegateImpl;
import usociety.manager.domain.service.post.CreatePostDelegate;
import usociety.manager.domain.service.post.GetAllGroupPostsDelegate;
import usociety.manager.domain.service.post.PostService;
import usociety.manager.domain.service.react.ReactService;
import usociety.manager.domain.service.survey.SurveyService;

@Service
public class PostServiceImpl extends AbstractDelegateImpl implements PostService {

    private static final String POST_NOT_FOUND_ERROR_CODE = "POST_DOES_NOT_FOUND";

    private final GetAllGroupPostsDelegate getAllGroupPostsDelegate;
    private final CreatePostDelegate createPostDelegate;
    private final CommentService commentService;
    private final PostRepository postRepository;
    private final SurveyService surveyService;
    private final ReactService reactService;

    @Autowired
    public PostServiceImpl(GetAllGroupPostsDelegate getAllGroupPostsDelegate,
                           CreatePostDelegate createPostDelegate,
                           CommentService commentService,
                           PostRepository postRepository,
                           SurveyService surveyService,
                           ReactService reactService) {
        this.getAllGroupPostsDelegate = getAllGroupPostsDelegate;
        this.createPostDelegate = createPostDelegate;
        this.commentService = commentService;
        this.postRepository = postRepository;
        this.surveyService = surveyService;
        this.reactService = reactService;
    }

    @Override
    public PostApi create(String username, CreatePostRequest request)
            throws GenericException {
        return createPostDelegate.execute(username, request);
    }

    @Override
    public List<PostApi> getAllByGroup(String username, Long groupId, int page) throws GenericException {
        return getAllGroupPostsDelegate.execute(username, groupId, page);
    }

    @Override
    public void react(String username, Long postId, ReactTypeEnum value) throws GenericException {
        reactService.create(username, getPost(postId), value);
    }

    @Override
    public void comment(String username, Long postId, CommentPostRequest request) throws GenericException {
        commentService.create(username, getPost(postId), request);
    }

    @Override
    public void vote(String username, Long postId, Integer vote) throws GenericException {
        Post post = getPost(postId);
        surveyService.validateIfUserHasAlreadyInteracted(username, post);
        surveyService.create(username, post, vote);
    }

    private Post getPost(Long postId) throws GenericException {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GenericException("Post was not found", POST_NOT_FOUND_ERROR_CODE));
    }

}
