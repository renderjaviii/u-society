package usociety.manager.domain.service.post;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.domain.exception.GenericException;

public interface PostService {

    PostApi create(String username, PostApi request) throws GenericException, JsonProcessingException;

    List<PostApi> getAll(String username, Long groupId) throws GenericException;

    void react(String username, Long postId, Integer react) throws GenericException;

    void comment(String username, Long postId, CommentPostRequest request) throws GenericException;

}
