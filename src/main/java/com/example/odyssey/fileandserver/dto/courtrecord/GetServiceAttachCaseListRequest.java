package com.example.odyssey.fileandserver.dto.courtrecord;

import lombok.Data;

@Data
public class GetServiceAttachCaseListRequest {
    private String courtId;
    private String caseTrackingId;
}
