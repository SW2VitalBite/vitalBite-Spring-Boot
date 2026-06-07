package com.vitalbite.documental.documents.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentMetadataRepository
        extends JpaRepository<DocumentMetadata, String> {

    // Buscar todos los documentos de un paciente
    List<DocumentMetadata> findByPatientId(String patientId);

    // Buscar por tipo de documento
    List<DocumentMetadata> findByTipoDocumento(
            String tipoDocumento);

    // Buscar por tenant (para multi-tenancy)
    List<DocumentMetadata> findByTenantId(String tenantId);

    // Buscar por tenant y paciente combinados
    List<DocumentMetadata> findByTenantIdAndPatientId(
            String tenantId, String patientId);

    // Buscar por recurso específico (ID de dieta, ficha, etc.)
    List<DocumentMetadata> findByResourceId(String resourceId);

    // Buscar documentos por estado
    List<DocumentMetadata> findByEstado(String estado);

    // Contar documentos generados por tenant
    @Query("SELECT COUNT(d) FROM DocumentMetadata d " +
            "WHERE d.tenantId = :tenantId " +
            "AND d.tipoDocumento = :tipo")
    Long countByTenantIdAndTipo(
            @Param("tenantId") String tenantId,
            @Param("tipo") String tipo);
}