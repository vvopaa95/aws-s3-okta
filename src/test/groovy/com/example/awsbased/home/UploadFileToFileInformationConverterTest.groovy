package com.example.awsbased.home

import com.example.awsbased.aws.UploadFileDto
import spock.lang.Specification
import spock.lang.Subject

class UploadFileToFileInformationConverterTest extends Specification {
    @Subject
    def converter = new UploadFileToFileInformationConverter()

    def "should convert upload file to file information with expected fields"() {
        given:
            def uploadFileDto = new UploadFileDto()
            uploadFileDto.setFileName("test.txt")
            uploadFileDto.setDescription("file info")
            uploadFileDto.setUploaded(true)
            uploadFileDto.setCharged(true)
        when:
            def result = converter.convert(uploadFileDto)
        then:
            result
            !result.getId()
            result.getName() == uploadFileDto.getFileName()
            result.getDescription() == uploadFileDto.getDescription()
            result.isRequestCharged() == uploadFileDto.isCharged()
            result.isUploaded() == uploadFileDto.isUploaded()
    }

    def "should return null when upload file param is null"() {
        when:
            def result = converter.convert(null)
        then:
            !result
    }
}
