package usociety.manager.domain.service.aws.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import usociety.manager.domain.service.common.CloudStorageService;

@Service
public class S3CloudStorageServiceImpl implements CloudStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3CloudStorageServiceImpl.class);

    private static final String UPLOADING_FILE_ERROR_CODE = "UPLOADING_FILE_FAILED";
    private static final String FILE_URL_FORMAT = "%s/%s";
    public static final String ENCODING_PREFIX = "base64,";

    @Value("${config.aws.access-key:accessKey}")
    private String accessKey;

    @Value("${config.aws.secret-key:secretKey}")
    private String secretKey;

    @Value("${config.aws.session-token:awsSessionToken}")
    private String sessionToken;

    @Value("${config.aws.endpoint-url:endpoint}")
    private String endpointUrl;

    @Value("${config.aws.bucket-name:bucketName}")
    private String bucketName;

    @Value("${config.aws.use-mock:true}")
    private boolean useMock;

    private final Clock clock;

    private AmazonS3 s3client;

    @Autowired
    public S3CloudStorageServiceImpl(Clock clock) {
        this.clock = clock;
    }

    @PostConstruct
    protected void init() {
        AWSCredentials credentials = new BasicSessionCredentials(accessKey, secretKey, sessionToken);
        s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    @Override
    public String uploadImage(String base64Image) throws GenericException {

        if (StringUtils.isNotEmpty(base64Image)) {
            String fileName = generateFileName();

            String fileUrl = String.format(FILE_URL_FORMAT, endpointUrl, fileName);
            if (useMock) {
                return fileUrl;
            }

            File file = convertBase64ToFile(base64Image, fileName);
            uploadFile(fileName, file);
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

    private String generateFileName() {
        return String.format("%s-image", LocalDateTime.now(clock));
    }

    private File convertBase64ToFile(String base64Image, String fileName) throws GenericException {
        File file;
        try {
            int contentStartIndex = base64Image.indexOf(ENCODING_PREFIX) + ENCODING_PREFIX.length();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image.substring(contentStartIndex));

            file = new File(fileName);
            FileUtils.writeByteArrayToFile(file, decodedBytes);
        } catch (Exception ex) {
            throw new GenericException("Error reading base64 file", UPLOADING_FILE_ERROR_CODE, ex);
        }

        return file;
    }

    private void uploadFile(String fileName, File file) throws GenericException {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
            s3client.putObject(putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception ex) {
            s3client.deleteObject(bucketName, fileName);
            throw new GenericException("Error uploading file to S3", UPLOADING_FILE_ERROR_CODE, ex);
        } finally {
            deleteLocalFile(file);
        }
    }

    private void deleteLocalFile(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException ex) {
            LOGGER.error("Error deleting local file", ex);
        }
    }

}
