package com.vitalbite.documental.storage.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Slf4j
@Service
public class StorageService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.secret-key}")
    private String secretKey;

    @Value("${aws.s3.presigned-url-expiration:15}")
    private Long expirationMinutes;

    // ── Clientes S3 ──────────────────────────────────────
    private S3Client buildS3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    private S3Presigner buildPresigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    // ── Subir PDF ────────────────────────────────────────
    public String uploadPdf(byte[] pdfBytes, String nombreArchivo) {
        String key = "pdfs/" + nombreArchivo;

        log.info("[S3] Subiendo PDF: {}", key);
        try (S3Client s3 = buildS3Client()) {
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType("application/pdf")
                            .build(),
                    RequestBody.fromBytes(pdfBytes)
            );
        }
        log.info("[S3] PDF subido exitosamente: {}", key);
        return key;
    }

    // ── Generar URL prefirmada ───────────────────────────
    public String generatePresignedUrl(String key) {
        log.info("[S3] Generando URL prefirmada: {}", key);
        try (S3Presigner presigner = buildPresigner()) {
            PresignedGetObjectRequest presigned =
                    presigner.presignGetObject(
                            GetObjectPresignRequest.builder()
                                    .signatureDuration(Duration.ofMinutes(expirationMinutes))
                                    .getObjectRequest(req -> req
                                            .bucket(bucketName)
                                            .key(key))
                                    .build()
                    );
            return presigned.url().toString();
        }
    }

    // ── Eliminar PDF de S3 ───────────────────────────────
    public void deleteFile(String key) {
        log.info("[S3] Eliminando: {}", key);
        try (S3Client s3 = buildS3Client()) {
            s3.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()
            );
        }
    }

    // ── Info del bucket actual ───────────────────────────
    public String getModoActual() {
        return "AWS S3 REAL (bucket: " + bucketName + ")";
    }
}