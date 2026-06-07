package com.vitalbite.documental.storage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${aws.s3.mock-enabled:true}")
    private boolean mockEnabled;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.presigned-url-expiration:15}")
    private Long expirationMinutes;

    public String uploadPdf(byte[] pdfBytes,
                            String nombreArchivo) {

        if (mockEnabled) {
            System.out.println(
                    ">>> [MOCK S3] Subiendo: " + nombreArchivo
                            + " | Tamaño: " + pdfBytes.length + " bytes"
            );
            return bucketName + "/" + nombreArchivo;
        }

        throw new UnsupportedOperationException(
                "S3 real no configurado todavía"
        );
    }

    public String generatePresignedUrl(String nombreArchivo) {

        if (mockEnabled) {
            String token = UUID.randomUUID()
                    .toString().substring(0, 12);
            return "https://mock-s3.local/"
                    + bucketName + "/" + nombreArchivo
                    + "?X-Amz-Expires="
                    + (expirationMinutes * 60)
                    + "&token=" + token;
        }

        throw new UnsupportedOperationException(
                "S3 real no configurado todavía"
        );
    }
}