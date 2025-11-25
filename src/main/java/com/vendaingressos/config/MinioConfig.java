package com.vendaingressos.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String url;

    @Value("${minio.access.key}")
    private String acessKey;

    @Value("${minio.secret.key}")
    private String secretKey;

    @Value("${minio.bucket.name}")
    private String bucketName;

    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(url).credentials(acessKey, secretKey).build();
    }

    public String bucketName() {
        return bucketName;
    }

}
