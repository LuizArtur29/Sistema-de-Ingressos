package com.vendaingressos.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;
    @Value("${minio.bucket.name}")
    private String bucketName;

    public String uploadArquivo(MultipartFile arquivo) {

        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                System.out.println("Bucket não existe. Criando: " + bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            String nomeArquivo = UUID.randomUUID() + "-" + arquivo.getOriginalFilename();

            try (InputStream inputStream = arquivo.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(nomeArquivo)
                                .stream(inputStream, arquivo.getSize(), -1)
                                .contentType(arquivo.getContentType())
                                .build()
                );
            }

            return nomeArquivo;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao fazer upload: " + e.getMessage());
        }
    }

    public String getUrlArquivo(String nomeArquivo) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(nomeArquivo)
                            .expiry(2, TimeUnit.HOURS)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar URL: " + e.getMessage());
        }
    }

    public void deletarArquivo(String nomeArquivo) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(nomeArquivo)
                            .build()
            );
            System.out.println("Arquivo deletado do MinIO: " + nomeArquivo);
        } catch (Exception e) {
            System.err.println("Erro ao deletar arquivo antigo: " + e.getMessage());
            // Opcional: throw new RuntimeException("Erro ao deletar...");
        }
    }
}