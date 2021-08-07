package usociety.manager.domain.service.aws.storage;

import static org.mockito.ArgumentMatchers.any;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import usociety.manager.domain.exception.GenericException;

@RunWith(MockitoJUnitRunner.class)
public class S3CloudStorageServiceImplTest {

    private static final String BASE_64_IMAGE =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAPCAIAAABfg7keAAAA6UlEQVQo" +
                    "FWP4TwRgIELNf+oren9xdXmUh729m7+/v7+7h72Fmb1f9PIrEMeArbu/OkpdWDZjzZ3v/+BO/Hy8zi5jD1zRiyV+MgwaJaeQV" +
                    "IDlLk2bfhSm6PlcJ3YmhorTcDMwGQz/9xdIMLCbrfiMKQcVuT+X4e+6RHYGPrsdv3EpetrnzPB/WwZ+k47FqTH8B7uJD7ebjq" +
                    "apM/z//2KtnyyDRu5BdN/dnJg/7c7//2Dr/v///3xzsrowfwJKOP0+P8XFY8qT////358Li7v3Nze3J7m7u/uFh8fHh0e4O9j" +
                    "Zh8VNP/MN7B0Axt/EzfGRdTEAAAAASUVORK5CYII=";
    @Mock
    private Clock clock;
    @Mock
    private AmazonS3 s3client;

    @InjectMocks
    private S3CloudStorageServiceImpl subject;
    private String bucketName = "bucketName";

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(subject, "sessionToken", "sessionToken");
        ReflectionTestUtils.setField(subject, "endpointUrl", "endpointUrl");
        ReflectionTestUtils.setField(subject, "accessKey", "accessKey");
        ReflectionTestUtils.setField(subject, "secretKey", "secretKey");
        ReflectionTestUtils.setField(subject, "useMock", Boolean.FALSE);
        ReflectionTestUtils.setField(subject, "bucketName", bucketName);
        ReflectionTestUtils.setField(subject, "s3client", s3client);

        Mockito.when(clock.getZone()).thenReturn(ZoneId.of("GMT"));
        Mockito.when(clock.instant()).thenReturn(Instant.now());
    }

    @Test
    public void shouldUploadImageMockedCorrectly() throws GenericException {
        ReflectionTestUtils.setField(subject, "useMock", Boolean.TRUE);

        String executed = subject.uploadImage("base64Image");

        String expected = "endpointUrl/" + LocalDateTime.now(clock) + "-image";
        Assert.assertEquals(expected, executed);
    }

    @Test
    public void shouldUploadImageCorrectly() throws GenericException {
        String executed = subject.uploadImage(BASE_64_IMAGE);

        String objectName = LocalDateTime.now(clock) + "-image";
        String expected = "endpointUrl/" + objectName;
        Assert.assertEquals(expected, executed);

        ArgumentCaptor<PutObjectRequest> argumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        Mockito.verify(s3client).putObject(argumentCaptor.capture());
        PutObjectRequest uploadedObject = argumentCaptor.getValue();
        Assert.assertEquals(CannedAccessControlList.PublicRead.name(), uploadedObject.getCannedAcl().name());
        Assert.assertEquals(bucketName, uploadedObject.getBucketName());
        Assert.assertEquals(bucketName, uploadedObject.getBucketName());
    }

    @Test(expected = GenericException.class)
    public void shouldFailUploadingImageIfItIsInvalid() throws GenericException {
        try {
            subject.uploadImage("data:image/png;base64");
        } catch (GenericException e) {
            Assert.assertEquals("Error reading base64 file", e.getMessage());
            Assert.assertEquals("UPLOADING_FILE_FAILED", e.getErrorCode());
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = GenericException.class)
    public void shouldFailUploadingImageIfProviderFails() throws GenericException {
        Mockito.when(s3client.putObject(any())).thenThrow(new AmazonServiceException("Error"));
        try {
            subject.uploadImage(BASE_64_IMAGE);
        } catch (GenericException e) {
            Assert.assertEquals("Error uploading file to S3", e.getMessage());
            Assert.assertEquals("UPLOADING_FILE_FAILED", e.getErrorCode());
            Mockito.verify(s3client).deleteObject(bucketName, LocalDateTime.now(clock) + "-image");
            throw e;
        }
        Assert.fail();
    }

    @Test
    public void shouldReturnNullIfAnyImageIsSent() throws GenericException {
        Assert.assertNull(subject.uploadImage(StringUtils.EMPTY));
    }

    @Test
    public void shouldDeleteFileCorrectly() {
        String fileName = LocalDateTime.now(clock) + "-image";
        subject.delete("endpointUrl/" + fileName);
        Mockito.verify(s3client).deleteObject(bucketName, fileName);
    }

    @Test
    @SuppressWarnings( { "java:S2699" })
    public void shouldCallInitMethodCorrectly() {
        ReflectionTestUtils.invokeMethod(subject, "init");
    }

}