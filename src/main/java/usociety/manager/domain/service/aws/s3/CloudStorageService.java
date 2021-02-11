package usociety.manager.domain.service.aws.s3;

import usociety.manager.domain.exception.GenericException;

public interface CloudStorageService {

    String upload(String base64Image) throws GenericException;

    void delete(String fileUrl);

}
