package com.example.odyssey.fileandserver.dto.courtrecord;

import lombok.Data;

@Data
public class GetDocumentRequest {
    private String documentId;
    private String caseTrackingId;
    private String courtId;
}
