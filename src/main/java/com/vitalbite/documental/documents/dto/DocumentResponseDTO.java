package com.vitalbite.documental.documents.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponseDTO {
    private String documentId;
    private String url;
    private String nombreArchivo;
    private Long expiresIn;
    private String mensaje;
}