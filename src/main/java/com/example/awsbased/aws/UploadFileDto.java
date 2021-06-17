package com.example.awsbased.aws;

import lombok.Data;

@Data
public class UploadFileDto {
    private String fileName;
    private String description;
    private boolean isCharged;
    private boolean uploaded;
}
