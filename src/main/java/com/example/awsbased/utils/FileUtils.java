package com.example.awsbased.utils;

import com.example.awsbased.common.Tuple2;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Log4j2
@Component
public class FileUtils {

    public Tuple2<String, Path> transferToTempFile(MultipartFile multipartFile, String prefix, String suffix) {
        Tuple2<String, Path> result = null;
        try {
            var fileName = Optional.ofNullable(multipartFile.getOriginalFilename())
                    .filter(s -> !s.isEmpty()).orElseGet(multipartFile::getName);
            log.info("Transferring file {} to temp file", fileName);
            Path tempFile = Files.createTempFile(prefix, suffix);
            multipartFile.transferTo(tempFile);
            result = new Tuple2<>(fileName, tempFile);
        } catch (IOException e) {
            log.info("Exception occurred while transferring multipartFile to temp file {}", e.getMessage());
            log.debug("Stack trace", e);
        }
        return result;
    }

    public void deleteFile(Path filePath) {
        try {
            boolean isDeleted = Files.deleteIfExists(filePath);
            log.info("File on path {} is deleted with flag {}", filePath, isDeleted);
        } catch (IOException e) {
            log.info("Can't delete file on path {} with message {}", filePath, e.getMessage());
            log.debug("Stack trace", e);
        }
    }
}
