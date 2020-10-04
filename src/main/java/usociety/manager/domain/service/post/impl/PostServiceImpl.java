package usociety.manager.domain.service.post.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.SurveyOption;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Comment;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.model.React;
import usociety.manager.domain.repository.CommentRepository;
import usociety.manager.domain.repository.PostRepository;
import usociety.manager.domain.repository.ReactRepository;
import usociety.manager.domain.service.common.CommonServiceImpl;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.post.PostService;
import usociety.manager.domain.service.post.dto.PostAdditionalData;
import usociety.manager.domain.service.user.UserService;

@Service
public class PostServiceImpl extends CommonServiceImpl implements PostService {

    private static final String CREATING_POST_ERROR_CODE = "ERROR_CREATING_POST";
    private static final String GETTING_POST_ERROR_CODE = "ERROR_GETTING_POSTS";
    private static final String REACTING_POST_ERROR_CODE = "ERROR_REACTING_TO_POST";

    private final CommentRepository commentRepository;
    private final ReactRepository reactRepository;
    private final PostRepository postRepository;
    private final GroupService groupService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Autowired
    public PostServiceImpl(CommentRepository commentRepository,
                           ReactRepository reactRepository,
                           PostRepository postRepository,
                           GroupService groupService,
                           UserService userService) {
        this.commentRepository = commentRepository;
        this.reactRepository = reactRepository;
        this.postRepository = postRepository;
        this.groupService = groupService;
        this.userService = userService;
        objectMapper = new ObjectMapper();
    }

    @Override
    public PostApi create(String username, PostApi request) throws GenericException, JsonProcessingException {
        validateIfUserIsMember(username, request.getGroupId(), CREATING_POST_ERROR_CODE);
        processContent(request);

        return Converter.post(postRepository.save(Post.newBuilder()
                .group(groupService.get(request.getGroupId()))
                .creationDate(LocalDateTime.now(clock))
                .expirationDate(request.getExpirationDate())
                .isPublic(request.isPublic())
                .content(objectMapper.writeValueAsString(request.getContent()))
                .build()));
    }

    @Override
    public List<PostApi> getAll(String username, Long groupId) throws GenericException {
        validateIfUserIsMember(username, groupId, GETTING_POST_ERROR_CODE);
        List<Post> posts = postRepository.findAllByGroupIdOrderByCreationDateAsc(groupId);

        List<PostApi> responseList = new ArrayList<>();
        for (Post post : posts) {
            List<React> reacts = reactRepository.findAllByPostId(post.getId());
            List<Comment> comments = commentRepository.findByPostId(post.getId());
            PostApi postApi = Converter.post(post);
            postApi.setReacts(reacts.stream().map(Converter::react).collect(Collectors.toList()));
            postApi.setComments(comments.stream().map(Converter::comment).collect(Collectors.toList()));
            responseList.add(postApi);
        }
        return responseList;
    }

    @Override
    public void react(String username, Long postId, Integer react) throws GenericException {
        Post post = getPost(postId);
        validateIfUserIsMember(username, post.getGroup().getId(), REACTING_POST_ERROR_CODE);
        UserApi user = userService.get(username);

        Optional<React> optionalReact = reactRepository.findAllByPostIdAndUserId(postId, user.getId());
        if (optionalReact.isPresent()) {
            React savedReact = optionalReact.get();
            savedReact.setValue(react);
            reactRepository.save(savedReact);
        } else {
            reactRepository.save(React.newBuilder()
                    .post(post)
                    .userId(user.getId())
                    .value(react)
                    .build());
        }
    }

    @Override
    public void comment(String username, Long postId, CommentPostRequest request) throws GenericException {
        Post post = getPost(postId);
        validateIfUserIsMember(username, post.getGroup().getId(), REACTING_POST_ERROR_CODE);

        UserApi user = userService.get(username);
        commentRepository.save(Comment.newBuilder()
                .creationDate(LocalDateTime.now(clock))
                .value(request.getComment())
                .userId(user.getId())
                .post(post)
                .build());

    }

    private void processContent(PostApi request) {
        PostAdditionalData content = request.getContent();
        if (PostTypeEnum.SURVEY.getCode() == content.getType()) {
            List<SurveyOption> surveyOptions = content.getOptions();
            for (int index = 0; index < surveyOptions.size(); index++) {
                SurveyOption surveyOption = surveyOptions.get(index);
                surveyOption.setId(index);
            }
        }
    }

    private Post getPost(Long postId) throws GenericException {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GenericException("Post no encontrado.", GETTING_POST_ERROR_CODE));
    }

}
