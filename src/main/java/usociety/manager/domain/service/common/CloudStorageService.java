package usociety.manager.domain.service.common;

import usociety.manager.domain.exception.GenericException;

public interface CloudStorageService {

    String uploadImage(String base64Image) throws GenericException;

    void delete(String fileUrl);

}
