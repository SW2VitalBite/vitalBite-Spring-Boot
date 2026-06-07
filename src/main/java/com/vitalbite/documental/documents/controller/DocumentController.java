package com.vitalbite.documental.documents.controller;

import com.vitalbite.documental.documents.dto.DietPdfRequestDTO;
import com.vitalbite.documental.documents.dto.InvoicePdfRequestDTO;
import com.vitalbite.documental.documents.dto.DocumentResponseDTO;
import com.vitalbite.documental.documents.service.DocumentService;
import com.vitalbite.documental.documents.service.PdfGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Tag(
        name = "Documentos",
        description = "Generación de PDFs y gestión documental"
)
public class DocumentController {

    private final DocumentService documentService;
    private final PdfGeneratorService pdfGeneratorService;

    @PostMapping("/pdf/diet")
    @Operation(
            summary = "Generar PDF de dieta",
            description =
                    "Recibe datos de dieta desde el Core NestJS, " +
                            "genera un PDF y devuelve una URL prefirmada de S3"
    )
    public ResponseEntity<DocumentResponseDTO> generateDietPdf(
            @RequestBody DietPdfRequestDTO request) {

        log.info("Request recibido para generar PDF de dieta");
        DocumentResponseDTO response =
                documentService.generateDietPdf(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pdf/invoice")
    @Operation(
            summary = "Generar PDF de factura",
            description = "Recibe datos de pago desde el microservicio .NET y genera una factura en PDF con el Hash de la Blockchain"
    )
    public ResponseEntity<DocumentResponseDTO> generateInvoicePdf(
            @RequestBody InvoicePdfRequestDTO request) {
        log.info("Request recibido para generar PDF de factura");
        DocumentResponseDTO response = documentService.generateInvoicePdf(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/pdf/diet/preview",
            produces = "application/pdf"
    )
    @Operation(
            summary = "Preview del PDF de dieta",
            description =
                    "Genera y devuelve el PDF directamente en el " +
                            "navegador para revisar el diseño. " +
                            "Solo para desarrollo, no guarda nada en base de datos."
    )
    public ResponseEntity<byte[]> previewDietPdf(
            @RequestBody DietPdfRequestDTO request) {

        log.info("Generando preview de PDF para revisión de diseño");
        byte[] pdfBytes =
                pdfGeneratorService.generateDietPdf(request);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "inline; filename=preview-dieta.pdf")
                .body(pdfBytes);
    }

    @GetMapping("/health")
    @Operation(
            summary = "Health check del módulo documental",
            description =
                    "Verifica que el módulo de documentos está activo"
    )
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(
                "Módulo documental funcionando correctamente"
        );
    }

    @GetMapping("/download/{fileName}")
    @Operation(summary = "Descargar un documento local", description = "Descarga un documento guardado localmente en el directorio uploads")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable String fileName) {
        try {
            java.nio.file.Path filePath = java.nio.file.Paths.get("uploads/" + fileName);
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (java.net.MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Listar documentos por paciente", description = "Devuelve una lista de los documentos generados para el paciente")
    public ResponseEntity<java.util.List<com.vitalbite.documental.documents.entity.DocumentMetadata>> getDocumentsByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(documentService.getDocumentsByPatientId(patientId));
    }
}