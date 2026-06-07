package com.vitalbite.documental.documents.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoicePdfRequestDTO {
    private String tenantId;
    private String clinicName;
    private String planName;
    private Double amount;
    private String billingPeriod;
    private String transactionHash;
    private String date;
}
