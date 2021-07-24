package usociety.manager.domain.service.post;

import java.util.List;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.domain.exception.GenericException;

public interface GetAllGroupPostsDelegate {

    List<PostApi> execute(UserApi user, Long groupId, int page, int pageSize) throws GenericException;

}
