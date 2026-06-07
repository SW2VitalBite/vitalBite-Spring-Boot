package com.vitalbite.documental.documents.service;

import com.vitalbite.documental.documents.dto.DietPdfRequestDTO;
import com.vitalbite.documental.documents.dto.DocumentResponseDTO;
import com.vitalbite.documental.documents.entity.DocumentMetadata;
import com.vitalbite.documental.documents.entity.DocumentMetadataRepository;
import com.vitalbite.documental.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final PdfGeneratorService pdfGeneratorService;
    private final StorageService storageService;
    private final DocumentMetadataRepository repository;

    public DocumentResponseDTO generateDietPdf(
            DietPdfRequestDTO request) {

        log.info("Generando PDF de dieta para paciente: {}",
                request.getPatientFullName());

        // 1. Generar PDF en memoria
        byte[] pdfBytes =
                pdfGeneratorService.generateDietPdf(request);
        log.debug("PDF generado: {} bytes", pdfBytes.length);

        // 2. Nombre único para el archivo
        String nombreBase = request.getPatientFullName() != null
                ? request.getPatientFullName()
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                : "paciente";

        String nombreArchivo = "dieta-"
                + nombreBase + "-"
                + UUID.randomUUID().toString().substring(0, 8)
                + ".pdf";

        // 3. Subir a S3 (mock por ahora)
        storageService.uploadPdf(pdfBytes, nombreArchivo);
        String url = storageService
                .generatePresignedUrl(nombreArchivo);
        log.info("PDF disponible en: {}", url);

        // 4. Hash SHA-256 para auditoría
        String hash = generarHash(pdfBytes);

        // 5. Guardar metadatos en PostgreSQL
        DocumentMetadata metadata = new DocumentMetadata();
        metadata.setNombreArchivo(nombreArchivo);
        metadata.setTipoDocumento("DIETA_PDF");
        metadata.setTenantId(request.getTenantId());
        metadata.setPatientId(request.getPatientId());
        metadata.setNutritionistId(
                request.getNutritionistId());
        metadata.setResourceId(request.getId());
        metadata.setPacienteNombre(
                request.getPatientFullName());
        metadata.setNutricionistaNombre(
                request.getNutritionistFullName());
        metadata.setS3Url(url);
        metadata.setHashDocumento(hash);
        metadata.setEstado("GENERADO");

        DocumentMetadata saved = repository.save(metadata);
        log.info("Metadatos guardados con ID: {}",
                saved.getId());

        // 6. Respuesta al Core
        return new DocumentResponseDTO(
                saved.getId(),
                url,
                nombreArchivo,
                900L,
                "PDF de dieta generado correctamente"
        );
    }

    private String generarHash(byte[] data) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data);
            return Base64.getEncoder()
                    .encodeToString(hashBytes);
        } catch (Exception e) {
            log.error("Error generando hash: {}",
                    e.getMessage());
            return "hash-no-disponible";
        }
    }

    public java.util.List<DocumentMetadata> getDocumentsByPatientId(String patientId) {
        return repository.findByPatientId(patientId);
    }
}