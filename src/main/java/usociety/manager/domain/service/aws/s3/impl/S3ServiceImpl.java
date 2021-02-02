package usociety.manager.domain.service.aws.s3.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.aws.s3.CloudStorageService;

@Service
public class S3ServiceImpl implements CloudStorageService {

    private static final String UPLOADING_FILE_ERROR_CODE = "FILE_UPLOADING_ERROR";
    private static final String FILE_URL_FORMAT = "%s/%s";

    @Value("${config.aws.access-key}")
    private String accessKey;
    @Value("${config.aws.secret-key}")
    private String secretKey;
    @Value("${config.aws.session-token}")
    private String sessionToken;
    @Value("${config.aws.endpoint-url}")
    private String endpointUrl;
    @Value("${config.aws.bucket-name}")
    private String bucketName;

    private AmazonS3 s3client;

    @PostConstruct
    private void init() {
        AWSCredentials credentials = new BasicSessionCredentials(accessKey, secretKey, sessionToken);
        s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    @Override
    public String upload(String base64Image) throws GenericException {
        if (StringUtils.isNotEmpty(base64Image)) {
            String fileName = generateFileName();
            File file = convertBase64ToFile(base64Image, fileName);
            String fileUrl = String.format(FILE_URL_FORMAT, endpointUrl, fileName);

            try {
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
                s3client.putObject(putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (Exception e) {
                s3client.deleteObject(bucketName, fileName);
                throw new GenericException("Error deleting file.", UPLOADING_FILE_ERROR_CODE);
            } finally {
                try {
                    Files.delete(file.toPath());
                } catch (IOException ignore) {
                }
            }
            return fileUrl;
        }
        return null;
    }

    @Override
    public void delete(String fileUrl) {
        if (Objects.nonNull(fileUrl)) {
            s3client.deleteObject(bucketName, fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
        }
    }

    private File convertBase64ToFile(String base64Image, String fileName) throws GenericException {
        File file;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
            file = new File(fileName);
            FileUtils.writeByteArrayToFile(file, decodedBytes);
        } catch (Exception e) {
            throw new GenericException("Error reading base64 file.", UPLOADING_FILE_ERROR_CODE);
        }

        return file;
    }

    private String generateFileName() {
        return String.format("%s-image", new Date().getTime());
    }

}
