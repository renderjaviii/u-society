package usociety.manager.domain.service.react;

import usociety.manager.domain.enums.ReactTypeEnum;
import usociety.manager.domain.exception.GenericException;

public interface ReactService {

    void create(String username, Long postId, ReactTypeEnum value) throws GenericException;

}
