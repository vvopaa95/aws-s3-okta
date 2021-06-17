package com.example.awsbased.home;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @GetMapping("/")
    public String getHomePage(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        log.info("User logged in {}", oidcUser.getFullName());
        model.addAttribute("name", oidcUser.getGivenName());
        return "home";
    }

    @PostMapping("/uploadFiles")
    public String handleFileUpload(@RequestParam("files") MultipartFile[] files,
                                   RedirectAttributes redirectAttributes) {
        log.info("Request came for uploading files");
        homeService.processFiles(files);
        redirectAttributes.addFlashAttribute("message", "Uploading files task is submitted");
        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/allFiles")
    public List<String> retrieveFileNames() {
        log.info("Request came for getting all file names");
        return homeService.getExistingFileNames();
    }
}
