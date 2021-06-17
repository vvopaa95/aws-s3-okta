package com.example.awsbased.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
class HomeControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    MockMvc mvc

    def "should return 401 and redirect to okta login page"() {
        expect:
            mvc.perform(get("/")).andExpect(status().isUnauthorized())
    }

    def "should return 200 when user is authenticated"() {
        expect:
            def result = mvc.perform(get("/").with(oidcLogin())).andExpect(status().isOk()).andReturn()
            result.getResponse().getContentType() == "text/html;charset=UTF-8"
    }
}
