package com.example.awsbased.home;

import com.example.awsbased.aws.UploadFileDto;
import com.example.awsbased.common.Converter;
import org.springframework.stereotype.Component;

@Component
public class UploadFileToFileInformationConverter implements Converter<UploadFileDto, FileInformation> {

    @Override
    public FileInformation convert(UploadFileDto uploadFileDto) {
        if(uploadFileDto == null) return null;
        var fileInformation = new FileInformation();
        fileInformation.setName(uploadFileDto.getFileName());
        fileInformation.setUploaded(uploadFileDto.isUploaded());
        fileInformation.setRequestCharged(uploadFileDto.isCharged());
        fileInformation.setDescription(uploadFileDto.getDescription());
        return fileInformation;
    }
}
