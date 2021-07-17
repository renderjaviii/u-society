package usociety.manager.domain.service.comment.impl;

import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Comment;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.repository.CommentRepository;
import usociety.manager.domain.service.comment.CommentService;
import usociety.manager.domain.service.common.impl.AbstractDelegateImpl;
import usociety.manager.domain.service.post.dto.PostAdditionalData;

@Service
public class CommentServiceImpl extends AbstractDelegateImpl implements CommentService {

    private static final String COMMENTING_POST_ERROR_CODE = "ERROR_COMMENTING_POST";

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void create(String username, Post post, CommentPostRequest request) throws GenericException {
        UserApi user = getUser(username);
        validateIfUserIsMember(username, post.getGroup().getId(), ACTIVE, COMMENTING_POST_ERROR_CODE);

        validatePostType(post);

        commentRepository.save(Comment.newBuilder()
                .creationDate(LocalDateTime.now(clock))
                .value(request.getComment())
                .userId(user.getId())
                .post(post)
                .build());

    }

    private void validatePostType(Post post) throws GenericException {
        try {
            PostAdditionalData postAdditionalData = objectMapper.readValue(post.getContent(), PostAdditionalData.class);
            if (PostTypeEnum.SURVEY == postAdditionalData.getType()) {
                throw new GenericException("No es posible comentar en encuestas.", COMMENTING_POST_ERROR_CODE);
            }
        } catch (JsonProcessingException e) {
            throw new GenericException("Información de post corrupta.", COMMENTING_POST_ERROR_CODE);
        }
    }

}
