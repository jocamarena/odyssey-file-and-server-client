package com.example.odyssey.fileandserver.dto.courtrecord;

import lombok.Data;

@Data
public class CreateCaseRequest {
    private String courtId;
    private String caseTypeCode;
    private String filingId;
}
