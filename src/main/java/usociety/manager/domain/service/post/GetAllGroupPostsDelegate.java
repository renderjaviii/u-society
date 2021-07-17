package usociety.manager.domain.service.post;

import java.util.List;

import usociety.manager.app.api.PostApi;
import usociety.manager.domain.exception.GenericException;

public interface GetAllGroupPostsDelegate {

    List<PostApi> execute(String username, Long groupId, int page) throws GenericException;

}
