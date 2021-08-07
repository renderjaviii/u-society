package usociety.manager.domain.service.react;

import usociety.manager.domain.enums.ReactTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Post;

public interface ReactService {

    void createOrUpdate(String username, Post post, ReactTypeEnum value) throws GenericException;

}
