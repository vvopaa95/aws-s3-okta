package com.example.awsbased.home

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.Validation

class HomePropertiesTest extends Specification {

    @Shared
    def validator = Validation.buildDefaultValidatorFactory().getValidator()

    @Unroll
    def "should get validation error in property #propertyError"() {
        given:
            def homeProperties = new HomeProperties(prefix, suffix)
        when:
            def result = validator.validate(homeProperties)
        then:
            1 == result.size()
            propertyError == result[0].getPropertyPath().toString()
        where:
            prefix | suffix | propertyError
            "pref" | null   | "suffix"
            ""     | "suf"  | "prefix"
    }

    def "should return same constructor values"() {
        given:
            def prefix = "pref1"
            def suffix = "suf1"
        when:
            def homeProperties = new HomeProperties(prefix, suffix)
        then:
            prefix == homeProperties.getPrefix()
            suffix == homeProperties.getSuffix()
    }
}
