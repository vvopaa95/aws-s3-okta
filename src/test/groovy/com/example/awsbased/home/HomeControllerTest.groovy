package com.example.awsbased.home

import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.ui.Model
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import spock.lang.Specification
import spock.lang.Subject

class HomeControllerTest extends Specification {
    def homeService = Mock(HomeService)
    @Subject
    def homeController = new HomeController(homeService)

    def "should get home page name"() {
        given:
            def oidcUser = Mock(OidcUser)
            def model = Mock(Model)
            def fullname = "fullName"
            def givenName = "givenName"
            def attributeKey = "name"
        when:
            def pageName = homeController.getHomePage(oidcUser, model)
        then:
            1 * oidcUser.getFullName() >> fullname
            1 * oidcUser.getGivenName() >> givenName
            1 * model.addAttribute(attributeKey, givenName)
            0 * _
        and:
            pageName == "home"
    }

    def "should get redirect command when call process files logic"() {
        given:
            def files = [ Mock(MultipartFile) ] as MultipartFile[]
            def redirectAttributes = Mock(RedirectAttributes)
            def flashAttributeKey = "message"
        when:
            def redirectCommand = homeController.handleFileUpload(files, redirectAttributes)
        then:
            1 * homeService.processFiles(files)
            1 * redirectAttributes.addFlashAttribute(flashAttributeKey, _ as String)
            0 * _
        and:
            redirectCommand == "redirect:/"
    }

    def "should retrieve existing file names"() {
        given:
            def fileNames = [ "file1", "file2" ]
        when:
            def resultNames = homeController.retrieveFileNames()
        then:
            1 * homeService.getExistingFileNames() >> fileNames
            0 * _
        and:
            fileNames == resultNames
    }
}
