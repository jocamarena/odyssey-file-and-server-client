package com.example.odyssey.fileandserver.dto.courtrecord;

import lombok.Data;

@Data
public class GetServiceInformationHistoryRequest {
    private String caseTrackingId;
    private String courtId;
}
