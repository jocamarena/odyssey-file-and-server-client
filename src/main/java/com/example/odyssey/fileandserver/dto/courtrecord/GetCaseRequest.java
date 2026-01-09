package com.example.odyssey.fileandserver.dto.courtrecord;

import lombok.Data;

@Data
public class GetCaseRequest {
    private String caseTrackingId;
    private String courtId;
    private boolean includeDocketEntry;
    private boolean includeCalendarEvents;
    private boolean includeParticipants;
}
