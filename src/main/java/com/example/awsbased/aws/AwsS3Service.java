package com.example.awsbased.aws;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.awsbased.common.Tuple2;
import com.example.awsbased.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class AwsS3Service {
    private final AmazonS3 s3Client;
    private final FileUtils fileUtils;
    private final AwsProperties awsProperties;

    public void createBucket(String bucketName) {
        if (s3Client.doesBucketExistV2(bucketName)) {
            log.info("Can't create new Bucket: name {}. It either exists or is access denied.", bucketName);
            return;
        }
        var bucket = s3Client.createBucket(bucketName);
        log.info("Successfully created new bucket {}", bucket);
    }

    public List<S3ObjectSummary> getObjectSummaries() {
        return s3Client.listObjectsV2(awsProperties.getBucketName()).getObjectSummaries();
    }

    public UploadFileDto uploadFileToBucket(Tuple2<String, Path> fileTuple) {
        UploadFileDto uploadFile;
        var fileName = fileTuple.getFirst();
        var tmpFilePath = fileTuple.getSecond();
        log.info("Uploading {} to AWS S3", fileName);
        try {
            PutObjectResult result = s3Client.putObject(awsProperties.getBucketName(), fileName, tmpFilePath.toFile());
            uploadFile = getSuccessfulUploadDto(fileName, result);
        } catch (SdkClientException e) {
            log.error("Error occurred while trying to upload file {} to AWS S3: {}", fileName, e.getMessage());
            log.debug("Stack trace", e);
            uploadFile = getFailedUploadDto(fileName, e.getMessage());
        } finally {
            fileUtils.deleteFile(tmpFilePath);
        }
        return uploadFile;
    }

    private UploadFileDto getSuccessfulUploadDto(String fileName, PutObjectResult putObjectResult) {
        var uploadFile = new UploadFileDto();
        uploadFile.setFileName(fileName);
        uploadFile.setUploaded(true);
        uploadFile.setCharged(putObjectResult.isRequesterCharged());
        uploadFile.setDescription(putObjectResult.getETag());
        return uploadFile;
    }

    private UploadFileDto getFailedUploadDto(String fileName, String errorMessage) {
        var uploadFile = new UploadFileDto();
        uploadFile.setFileName(fileName);
        uploadFile.setDescription(errorMessage);
        return uploadFile;
    }
}
