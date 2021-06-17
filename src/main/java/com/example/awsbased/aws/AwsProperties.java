package com.example.awsbased.aws;

import com.amazonaws.regions.Regions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsProperties {
    @NotEmpty
    private final String accessKey;
    @NotEmpty
    private final String secretKey;
    @NotNull
    private final Regions region;
    private final String bucketName;
}
