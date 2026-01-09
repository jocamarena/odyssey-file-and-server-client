package com.example.odyssey.fileandserver.dto.courtrecord;

import lombok.Data;

@Data
public class NotifyReceiptCompleteRequest {
    private String caseTrackingId;
    private String courtId;
    private String filingId;
    private String receiptId;
}
