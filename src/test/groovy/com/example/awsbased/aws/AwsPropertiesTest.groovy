package com.example.awsbased.aws

import com.amazonaws.regions.Regions
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.Validation

class AwsPropertiesTest extends Specification {
    @Shared
    def validator = Validation.buildDefaultValidatorFactory().getValidator()

    @Unroll
    def "should get validation error in property #propertyError"() {
        given:
            def bucketName = "bucket1"
            def awsProperties = new AwsProperties(accessKey, secretKey, region, bucketName)
        when:
            def result = validator.validate(awsProperties)
        then:
            1 == result.size()
            propertyError == result[0].getPropertyPath().toString()
        where:
            accessKey | secretKey | region               | propertyError
            ""        | "secret"  | Regions.AF_SOUTH_1   | "accessKey"
            "access"  | null      | Regions.CA_CENTRAL_1 | "secretKey"
            "access"  | "secret"  | null                 | "region"
    }

    def "should return same constructor values"() {
        given:
            def accessKey = "pref1"
            def secretKey = "suf1"
            def region = Regions.EU_WEST_3
            def bucketName = "bucket10"
        when:
            def awsProperties = new AwsProperties(accessKey, secretKey, region, bucketName)
        then:
            accessKey == awsProperties.getAccessKey()
            secretKey == awsProperties.getSecretKey()
            region == awsProperties.getRegion()
            bucketName == awsProperties.getBucketName()
    }
}
