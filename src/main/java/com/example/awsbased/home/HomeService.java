package com.example.awsbased.home;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.awsbased.aws.AwsS3Service;
import com.example.awsbased.aws.UploadFileDto;
import com.example.awsbased.common.Converter;
import com.example.awsbased.common.Tuple2;
import com.example.awsbased.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class HomeService {
    private final AwsS3Service awsS3Service;
    private final FileInformationRepository fileInformationRepository;
    private final HomeProperties homeProperties;
    private final FileUtils fileUtils;
    private final Converter<UploadFileDto, FileInformation> converter;

    public void processFiles(MultipartFile[] files) {
        List<Tuple2<String, Path>> fileTuples = Arrays.stream(files).parallel()
                .map(multipartFile -> fileUtils.transferToTempFile(
                        multipartFile, homeProperties.getPrefix(), homeProperties.getSuffix()))
                .filter(Objects::nonNull).collect(Collectors.toList());
        CompletableFuture.supplyAsync(() ->
                fileTuples.parallelStream()
                        .map(awsS3Service::uploadFileToBucket)
                        .map(converter::convert)
                        .collect(Collectors.toList()))
                .thenAccept(this::saveUploadedFile);
    }

    private void saveUploadedFile(List<FileInformation> fileInfo) {
        Iterable<FileInformation> fileInfos = fileInformationRepository.saveAll(fileInfo);
        log.info("Saved array with file information to db");
        log.debug("Saved files {}", fileInfos);
    }

    public List<String> getExistingFileNames() {
        return awsS3Service.getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }
}
