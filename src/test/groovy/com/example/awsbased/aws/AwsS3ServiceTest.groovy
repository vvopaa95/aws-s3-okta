package com.example.awsbased.aws

import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ListObjectsV2Result
import com.amazonaws.services.s3.model.PutObjectResult
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.example.awsbased.common.Tuple2
import com.example.awsbased.utils.FileUtils
import spock.lang.Specification
import spock.lang.Subject

import java.nio.file.Path

class AwsS3ServiceTest extends Specification {
    def s3Client = Mock(AmazonS3)
    def fileUtils = Mock(FileUtils)
    def awsProperties = Mock(AwsProperties)
    @Subject
    def awsS3Service = new AwsS3Service(s3Client, fileUtils, awsProperties)

    def "should create bucket with expected result"() {
        given:
            def bucketName = "bucket1"
        when:
            awsS3Service.createBucket(bucketName)
        then:
            1 * s3Client.doesBucketExistV2(bucketName) >> bucketExists
            createBucketInvocations * s3Client.createBucket(bucketName)
        where:
            bucketExists | createBucketInvocations
            false        | 1
            true         | 0
    }

    def "should get object summaries from s3Client"() {
        given:
            def bucketName = "bucket9"
            List<S3ObjectSummary> objectSummaries = Arrays.asList(
                    new S3ObjectSummary(key: "key1"), new S3ObjectSummary(key: "key2"))
            def objectsV2Result = new ListObjectsV2Result(objectSummaries: objectSummaries)
        when:
            def result = awsS3Service.getObjectSummaries()
        then:
            1 * awsProperties.getBucketName() >> bucketName
            1 * s3Client.listObjectsV2(bucketName) >> objectsV2Result
        and:
            objectSummaries == result
    }

    def "should return successful result when upload file to bucket"() {
        given:
            def bucketName = "bucket5"
            def file = Mock(File)
            def filePath = Mock(Path)
            def fileTuple = new Tuple2<String, Path>("fileName5", filePath)
            def putObjectResult = new PutObjectResult(eTag: "ETAG", requesterCharged: true)
        when:
            def uploadResult = awsS3Service.uploadFileToBucket(fileTuple)
        then:
            1 * filePath.toFile() >> file
            1 * awsProperties.getBucketName() >> bucketName
            1 * s3Client.putObject(bucketName, fileTuple.getFirst(), file) >> putObjectResult
            1 * fileUtils.deleteFile(filePath)
            0 * _
        and:
            fileTuple.getFirst() == uploadResult.getFileName()
            putObjectResult.getETag() == uploadResult.getDescription()
            putObjectResult.isRequesterCharged() == uploadResult.isCharged()
            uploadResult.isCharged()
    }

    def "should return failed result when upload file to bucket throws SdkClientException"() {
        given:
            def bucketName = "bucket5"
            def errorMessage = "ERROR"
            def file = Mock(File)
            def filePath = Mock(Path)
            def fileTuple = new Tuple2<String, Path>("fileName3", filePath)
        when:
            def uploadResult = awsS3Service.uploadFileToBucket(fileTuple)
        then:
            1 * filePath.toFile() >> file
            1 * awsProperties.getBucketName() >> bucketName
            1 * s3Client.putObject(bucketName, fileTuple.getFirst(), file) >> {
                throw new SdkClientException(errorMessage)
            }
            1 * fileUtils.deleteFile(filePath)
            0 * _
        and:
            fileTuple.getFirst() == uploadResult.getFileName()
            errorMessage == uploadResult.getDescription()
            !uploadResult.isUploaded()
    }
}
