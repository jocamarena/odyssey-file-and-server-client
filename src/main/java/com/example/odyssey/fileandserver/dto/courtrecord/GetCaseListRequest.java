package com.example.odyssey.fileandserver.dto.courtrecord;

import lombok.Data;

@Data
public class GetCaseListRequest {
    private String courtId;
    private String caseNumber;
    private String caseTitle;
    private String personName;
    private String dateFiledFrom;
    private String dateFiledTo;
}
