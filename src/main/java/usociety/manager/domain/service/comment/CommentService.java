package usociety.manager.domain.service.comment;

import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.domain.exception.GenericException;

public interface CommentService {

    void create(String username, Long postId, CommentPostRequest request) throws GenericException;

}
