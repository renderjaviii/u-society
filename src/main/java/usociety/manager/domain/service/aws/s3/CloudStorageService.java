package usociety.manager.domain.service.aws.s3;

import org.springframework.web.multipart.MultipartFile;

import usociety.manager.domain.exception.GenericException;

public interface CloudStorageService {

    String upload(MultipartFile multipartFile) throws GenericException;

    void delete(String fileUrl);

}
