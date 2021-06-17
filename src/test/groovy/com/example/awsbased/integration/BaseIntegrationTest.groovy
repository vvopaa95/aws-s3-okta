package com.example.awsbased.integration

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BaseIntegrationTest extends WireMockTest {

    static server = wireMockServer()

    def cleanupSpec() {
        server.resetAll()
    }
}
