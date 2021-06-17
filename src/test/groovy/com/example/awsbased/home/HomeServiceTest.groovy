package com.example.awsbased.home

import com.amazonaws.services.s3.model.S3ObjectSummary
import com.example.awsbased.aws.AwsS3Service
import com.example.awsbased.aws.UploadFileDto
import com.example.awsbased.common.Converter
import com.example.awsbased.common.Tuple2
import com.example.awsbased.utils.FileUtils
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
import spock.lang.Subject
import spock.util.concurrent.AsyncConditions

import java.nio.file.Path

class HomeServiceTest extends Specification {
    def awsS3Service = Mock(AwsS3Service)
    def fileInformationRepository = Mock(FileInformationRepository)
    def homeProperties = Mock(HomeProperties)
    def fileUtils = Mock(FileUtils)
    def fileConverter = Mock(Converter)
    @Subject
    def homeService = new HomeService(awsS3Service, fileInformationRepository, homeProperties, fileUtils, fileConverter)

    def "should process expected files successfully"() {
        given:
            def maxConcurrentWaitSeconds = 10
            def multipartFile1 = Mock(MultipartFile)
            def multipartFile2 = Mock(MultipartFile)
            def files = [multipartFile1, multipartFile2] as MultipartFile[]
            def pathFile1 = Mock(Path)
            def prefix = "prefix"
            def suffix = "suffix"
            def fileTuple = new Tuple2<String, Path>("file1Name", pathFile1)
            def uploadFileDto = new UploadFileDto()
            uploadFileDto.setFileName("file1Name")
            def fileInformation = new FileInformation(name: uploadFileDto.getFileName())
            def fileInformationArr = [fileInformation] as List<FileInformation>
            def asyncConditions = new AsyncConditions()
        when:
            homeService.processFiles(files)
            asyncConditions.await(maxConcurrentWaitSeconds)
        then:
            2 * homeProperties.getPrefix() >> prefix
            2 * homeProperties.getSuffix() >> suffix
            1 * fileUtils.transferToTempFile(multipartFile1, prefix, suffix) >> fileTuple
            1 * fileUtils.transferToTempFile(multipartFile2, prefix, suffix)
            asyncConditions.evaluate({
                1 * awsS3Service.uploadFileToBucket(fileTuple) >> uploadFileDto
                1 * fileConverter.convert(uploadFileDto) >> fileInformation
                1 * fileInformationRepository.saveAll(fileInformationArr) >> fileInformationArr
                0 * _
            })
    }

    def "Should get existing file names from AWS s3 service"() {
        given:
            def fileNames = ["superFile1", "superFile2", "superFile3"]
            def objectSummaries = Arrays.asList(new S3ObjectSummary(key: fileNames[0]),
                    new S3ObjectSummary(key: fileNames[1]), new S3ObjectSummary(key: fileNames[2]))
        when:
            def existingNames = homeService.getExistingFileNames()
        then:
            1 * awsS3Service.getObjectSummaries() >> objectSummaries
            0 * _
        and:
            fileNames == existingNames
    }
}
