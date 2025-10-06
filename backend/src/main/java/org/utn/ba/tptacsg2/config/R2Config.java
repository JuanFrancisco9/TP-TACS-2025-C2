package org.utn.ba.tptacsg2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class R2Config {

    @Value("${cloudflare.r2.endpoint}")
    private String endpoint;
    @Value("${cloudflare.r2.access-key}")
    private String accessKey;
    @Value("${cloudflare.r2.secret-key}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        S3Configuration s3Config = S3Configuration.builder()
                .pathStyleAccessEnabled(true) // importante para R2
                .build();
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("auto")) // R2 no usa regiones AWS, poné auto
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .serviceConfiguration(s3Config)
                .build();
    }
}

