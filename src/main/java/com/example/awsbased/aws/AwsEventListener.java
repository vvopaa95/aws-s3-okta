package com.example.awsbased.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "bucket-auto-creation", havingValue = "true")
public class AwsEventListener {
    private final AwsS3Service awsS3Service;
    private final AwsProperties awsProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void createDefaultBucket() {
        awsS3Service.createBucket(awsProperties.getBucketName());
    }
}
