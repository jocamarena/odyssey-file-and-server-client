package com.example.odyssey.fileandserver.controller;

import com.example.odyssey.fileandserver.dto.EfmResponse;
import com.example.odyssey.fileandserver.dto.courtrecord.*;
import com.example.odyssey.fileandserver.service.CourtRecordMdeServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/efm/court-record")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "efm.enabled", havingValue = "true", matchIfMissing = true)
public class CourtRecordMdeController {

    private final CourtRecordMdeServiceClient courtRecordMdeServiceClient;

    @PostMapping("/case")
    public ResponseEntity<EfmResponse<Map<String, Object>>> createCase(
            @RequestBody CreateCaseRequest request) {
        log.info("REST request to create case in court: {}", request.getCourtId());
        EfmResponse<Map<String, Object>> response = courtRecordMdeServiceClient.createCase(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/case")
    public ResponseEntity<EfmResponse<Map<String, Object>>> getCase(
            @RequestParam("caseTrackingId") String caseTrackingId,
            @RequestParam("courtId") String courtId,
            @RequestParam(value = "includeDocketEntry", defaultValue = "false") boolean includeDocketEntry,
            @RequestParam(value = "includeCalendarEvents", defaultValue = "false") boolean includeCalendarEvents,
            @RequestParam(value = "includeParticipants", defaultValue = "false") boolean includeParticipants) {
        log.info("REST request to get case: {} from court: {}", caseTrackingId, courtId);
        GetCaseRequest request = new GetCaseRequest();
        request.setCaseTrackingId(caseTrackingId);
        request.setCourtId(courtId);
        request.setIncludeDocketEntry(includeDocketEntry);
        request.setIncludeCalendarEvents(includeCalendarEvents);
        request.setIncludeParticipants(includeParticipants);
        EfmResponse<Map<String, Object>> response = courtRecordMdeServiceClient.getCase(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/case-list")
    public ResponseEntity<EfmResponse<Map<String, Object>>> getCaseList(
            @RequestParam("courtId") String courtId,
            @RequestParam(value = "caseNumber", required = false) String caseNumber,
            @RequestParam(value = "caseTitle", required = false) String caseTitle,
            @RequestParam(value = "personName", required = false) String personName,
            @RequestParam(value = "dateFiledFrom", required = false) String dateFiledFrom,
            @RequestParam(value = "dateFiledTo", required = false) String dateFiledTo) {
        log.info("REST request to get case list for court: {}", courtId);
        GetCaseListRequest request = new GetCaseListRequest();
        request.setCourtId(courtId);
        request.setCaseNumber(caseNumber);
        request.setCaseTitle(caseTitle);
        request.setPersonName(personName);
        request.setDateFiledFrom(dateFiledFrom);
        request.setDateFiledTo(dateFiledTo);
        EfmResponse<Map<String, Object>> response = courtRecordMdeServiceClient.getCaseList(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/document")
    public ResponseEntity<EfmResponse<Map<String, Object>>> getDocument(
            @RequestParam("documentId") String documentId,
            @RequestParam("caseTrackingId") String caseTrackingId,
            @RequestParam("courtId") String courtId) {
        log.info("REST request to get document: {} for case: {}", documentId, caseTrackingId);
        GetDocumentRequest request = new GetDocumentRequest();
        request.setDocumentId(documentId);
        request.setCaseTrackingId(caseTrackingId);
        request.setCourtId(courtId);
        EfmResponse<Map<String, Object>> response = courtRecordMdeServiceClient.getDocument(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/service-information")
    public ResponseEntity<EfmResponse<Map<String, Object>>> getServiceInformation(
            @RequestParam("caseTrackingId") String caseTrackingId,
            @RequestParam("courtId") String courtId) {
        log.info("REST request to get service information for case: {}", caseTrackingId);
        GetServiceInformationRequest request = new GetServiceInformationRequest();
        request.setCaseTrackingId(caseTrackingId);
        request.setCourtId(courtId);
        EfmResponse<Map<String, Object>> response = courtRecordMdeServiceClient.getServiceInformation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/service-information-history")
    public ResponseEntity<EfmResponse<Map<String, Object>>> getServiceInformationHistory(
            @RequestParam("caseTrackingId") String caseTrackingId,
            @RequestParam("courtId") String courtId) {
        log.info("REST request to get service information history for case: {}", caseTrackingId);
        GetServiceInformationHistoryRequest request = new GetServiceInformationHistoryRequest();
        request.setCaseTrackingId(caseTrackingId);
        request.setCourtId(courtId);
        EfmResponse<Map<String, Object>> response = courtRecordMdeServiceClient.getServiceInformationHistory(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/service-attach-case-list")
    public ResponseEntity<EfmResponse<Map<String, Object>>> getServiceAttachCaseList(
            @RequestParam("courtId") String courtId,
            @RequestParam(value = "caseTrackingId", required = false) String caseTrackingId) {
        log.info("REST request to get service attach case list for court: {}", courtId);
        GetServiceAttachCaseListRequest request = new GetServiceAttachCaseListRequest();
        request.setCourtId(courtId);
        request.setCaseTrackingId(caseTrackingId);
        EfmResponse<Map<String, Object>> response = courtRecordMdeServiceClient.getServiceAttachCaseList(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/record-filing")
    public ResponseEntity<EfmResponse<Map<String, Object>>> recordFiling(
            @RequestBody RecordFilingRequest request) {
        log.info("REST request to record filing: {} for case: {}", request.getFilingId(), request.getCaseTrackingId());
        EfmResponse<Map<String, Object>> response = courtRecordMdeServiceClient.recordFiling(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/notify-receipt-complete")
    public ResponseEntity<EfmResponse<Map<String, Object>>> notifyReceiptComplete(
            @RequestBody NotifyReceiptCompleteRequest request) {
        log.info("REST request to notify receipt complete for filing: {}", request.getFilingId());
        EfmResponse<Map<String, Object>> response = courtRecordMdeServiceClient.notifyReceiptComplete(request);
        return ResponseEntity.ok(response);
    }
}
