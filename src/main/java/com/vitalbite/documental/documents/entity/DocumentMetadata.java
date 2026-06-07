package com.vitalbite.documental.documents.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "document_metadata")
public class DocumentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nombreArchivo;

    @Column(nullable = false)
    private String tipoDocumento;

    // ── Trazabilidad desde el Core ───────────────────
    private String tenantId;
    private String patientId;
    private String nutritionistId;
    private String resourceId;    // ID del recurso (dieta, ficha, etc.)

    // ── Nombres legibles ─────────────────────────────
    private String pacienteNombre;
    private String nutricionistaNombre;

    // ── Almacenamiento ───────────────────────────────
    @Column(length = 500)
    private String s3Url;

    @Column(length = 500)
    private String hashDocumento;

    // ── Estado del documento ─────────────────────────
    private String estado;        // GENERADO, ENVIADO, EXPIRADO

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaExpiracion;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "GENERADO";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}