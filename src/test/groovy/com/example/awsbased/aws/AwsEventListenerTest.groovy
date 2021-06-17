package com.example.awsbased.aws

import spock.lang.Specification
import spock.lang.Subject

class AwsEventListenerTest extends Specification {
    def awsS3Service = Mock(AwsS3Service)
    def awsProperties = Mock(AwsProperties)
    @Subject
    def awsEventListener = new AwsEventListener(awsS3Service, awsProperties)

    def "should call method to create default bucket"() {
        given:
            def bucketName = "bucket1"
        when:
            awsEventListener.createDefaultBucket()
        then:
            1 * awsProperties.getBucketName() >> bucketName
            1 * awsS3Service.createBucket(bucketName)
            0 * _
    }
}
