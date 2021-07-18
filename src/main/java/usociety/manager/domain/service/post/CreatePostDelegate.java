package usociety.manager.domain.service.post;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CreatePostRequest;
import usociety.manager.domain.exception.GenericException;

public interface CreatePostDelegate {

    PostApi execute(UserApi user, CreatePostRequest request) throws GenericException;

}
