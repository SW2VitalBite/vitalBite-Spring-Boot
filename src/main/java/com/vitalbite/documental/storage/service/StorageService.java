package com.vitalbite.documental.storage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {

    @Value("${server.port:8080}")
    private String serverPort;

    private final String uploadDir = "uploads/";

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de subidas", e);
        }
    }

    public String uploadPdf(byte[] pdfBytes, String nombreArchivo) {
        try {
            Path filePath = Paths.get(uploadDir + nombreArchivo);
            Files.write(filePath, pdfBytes);
            System.out.println(">>> [LOCAL STORAGE] Archivo guardado localmente: " + filePath.toAbsolutePath());
            return nombreArchivo;
        } catch (IOException e) {
            throw new RuntimeException("Error guardando el archivo localmente", e);
        }
    }

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    public String generatePresignedUrl(String nombreArchivo) {
        // En vez de S3, devolvemos la URL de nuestro propio controlador de descargas
        return "http://localhost:" + serverPort + contextPath + "/documents/download/" + nombreArchivo;
    }
}