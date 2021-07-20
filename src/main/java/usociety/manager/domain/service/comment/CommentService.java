package usociety.manager.domain.service.comment;

import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Post;

public interface CommentService {

    void create(String username, Post post, CommentPostRequest request) throws GenericException;

}
