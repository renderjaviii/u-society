package usociety.manager.domain.service.post;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.domain.enums.ReactTypeEnum;
import usociety.manager.domain.exception.GenericException;

public interface PostService {

    PostApi create(String username, PostApi request, MultipartFile image)
            throws GenericException, JsonProcessingException;

    List<PostApi> getAll(String username, Long groupId, int page) throws GenericException;

    void react(String username, Long postId, ReactTypeEnum react) throws GenericException;

    void comment(String username, Long postId, CommentPostRequest request) throws GenericException;

    void interactWithSurvey(String username, Long postId, Integer vote) throws GenericException;

}
