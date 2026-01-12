package com.example.odyssey.fileandserver.service;

import com.example.odyssey.fileandserver.dto.EfmResponse;
import com.example.odyssey.fileandserver.dto.courtrecord.*;
import jakarta.xml.ws.WebServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.legalxml_courtfiling.wsdl.webservicemessagingprofile_definitions_4.*;
import oasis.names.tc.legalxml_courtfiling.wsdl.webservicemessagingprofile_definitions_4_0.CourtRecordMDEService;
import org.apache.cxf.transport.http.HTTPException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "efm.enabled", havingValue = "true", matchIfMissing = true)
public class CourtRecordMdeServiceClient {

    @Lazy
    private final CourtRecordMDEService courtRecordMdeServiceSoapClient;

    public EfmResponse<Map<String, Object>> getCase(GetCaseRequest request) {
        try {
            log.info("Getting case: {} from court: {}", request.getCaseTrackingId(), request.getCourtId());

            GetCase.GetCaseRequest soapRequest = new GetCase.GetCaseRequest();
            // Note: The actual request content uses xs:any, so data is passed as raw XML
            // The specific field mapping depends on the court's implementation

            GetCaseResponse.GetCaseResponseMessage response = courtRecordMdeServiceSoapClient.getCase(soapRequest);

            Map<String, Object> data = new HashMap<>();
            if (response != null && response.getAny() != null) {
                data.put("response", response.getAny());
            }

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("getting case", e);
        }
    }

    public EfmResponse<Map<String, Object>> getCaseList(GetCaseListRequest request) {
        try {
            log.info("Getting case list for court: {}", request.getCourtId());

            GetCaseList.GetCaseListRequest soapRequest = new GetCaseList.GetCaseListRequest();

            GetCaseListResponse.GetCaseListResponseMessage response = courtRecordMdeServiceSoapClient.getCaseList(soapRequest);

            Map<String, Object> data = new HashMap<>();
            if (response != null && response.getAny() != null) {
                data.put("response", response.getAny());
            }

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("getting case list", e);
        }
    }

    public EfmResponse<Map<String, Object>> getDocument(GetDocumentRequest request) {
        try {
            log.info("Getting document: {} for case: {}", request.getDocumentId(), request.getCaseTrackingId());

            GetDocument.GetDocumentRequest soapRequest = new GetDocument.GetDocumentRequest();

            GetDocumentResponse.GetDocumentResponseMessage response = courtRecordMdeServiceSoapClient.getDocument(soapRequest);

            Map<String, Object> data = new HashMap<>();
            if (response != null && response.getAny() != null) {
                data.put("response", response.getAny());
            }

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("getting document", e);
        }
    }

    public EfmResponse<Map<String, Object>> getServiceInformation(GetServiceInformationRequest request) {
        try {
            log.info("Getting service information for case: {}", request.getCaseTrackingId());

            GetServiceInformation.GetServiceInformationRequest soapRequest = new GetServiceInformation.GetServiceInformationRequest();

            GetServiceInformationResponse.GetServiceInformationResponseMessage response =
                courtRecordMdeServiceSoapClient.getServiceInformation(soapRequest);

            Map<String, Object> data = new HashMap<>();
            if (response != null && response.getAny() != null) {
                data.put("response", response.getAny());
            }

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("getting service information", e);
        }
    }

    public EfmResponse<Map<String, Object>> getServiceInformationHistory(GetServiceInformationHistoryRequest request) {
        try {
            log.info("Getting service information history for case: {}", request.getCaseTrackingId());

            GetServiceInformationHistory.GetServiceInformationHistoryRequest soapRequest =
                new GetServiceInformationHistory.GetServiceInformationHistoryRequest();

            GetServiceInformationHistoryResponse.GetServiceInformationHistoryResponseMessage response =
                courtRecordMdeServiceSoapClient.getServiceInformationHistory(soapRequest);

            Map<String, Object> data = new HashMap<>();
            if (response != null && response.getAny() != null) {
                data.put("response", response.getAny());
            }

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("getting service information history", e);
        }
    }

    public EfmResponse<Map<String, Object>> getServiceAttachCaseList(GetServiceAttachCaseListRequest request) {
        try {
            log.info("Getting service attach case list for court: {}", request.getCourtId());

            GetServiceAttachCaseList.GetServiceAttachCaseListRequest soapRequest =
                new GetServiceAttachCaseList.GetServiceAttachCaseListRequest();

            GetServiceAttachCaseListResponse.GetServiceAttachCaseListResponseMessage response =
                courtRecordMdeServiceSoapClient.getServiceAttachCaseList(soapRequest);

            Map<String, Object> data = new HashMap<>();
            if (response != null && response.getAny() != null) {
                data.put("response", response.getAny());
            }

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("getting service attach case list", e);
        }
    }

    public EfmResponse<Map<String, Object>> recordFiling(RecordFilingRequest request) {
        try {
            log.info("Recording filing: {} for case: {}", request.getFilingId(), request.getCaseTrackingId());

            RecordFiling.RecordFilingRequest soapRequest = new RecordFiling.RecordFilingRequest();

            RecordFilingResponse.RecordFilingResponseMessage response =
                courtRecordMdeServiceSoapClient.recordFiling(soapRequest);

            Map<String, Object> data = new HashMap<>();
            if (response != null && response.getAny() != null) {
                data.put("response", response.getAny());
            }

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("recording filing", e);
        }
    }

    public EfmResponse<Map<String, Object>> createCase(CreateCaseRequest request) {
        try {
            log.info("Creating case in court: {} with type: {}", request.getCourtId(), request.getCaseTypeCode());

            CreateCase.CreateCaseRequest soapRequest = new CreateCase.CreateCaseRequest();

            CreateCaseResponse.CreateCaseResponseMessage response =
                courtRecordMdeServiceSoapClient.createCase(soapRequest);

            Map<String, Object> data = new HashMap<>();
            if (response != null && response.getAny() != null) {
                data.put("response", response.getAny());
            }

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("creating case", e);
        }
    }

    public EfmResponse<Map<String, Object>> notifyReceiptComplete(NotifyReceiptCompleteRequest request) {
        try {
            log.info("Notifying receipt complete for filing: {} case: {}",
                request.getFilingId(), request.getCaseTrackingId());

            NotifyReceiptComplete.NotifyReceiptCompleteRequest soapRequest =
                new NotifyReceiptComplete.NotifyReceiptCompleteRequest();

            NotifyReceiptCompleteResponse.NotifyReceiptCompleteResponseMessage response =
                courtRecordMdeServiceSoapClient.notifyReceiptComplete(soapRequest);

            Map<String, Object> data = new HashMap<>();
            if (response != null && response.getAny() != null) {
                data.put("response", response.getAny());
            }

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("notifying receipt complete", e);
        }
    }

    private EfmResponse<Map<String, Object>> handleException(String operation, Exception e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof HTTPException httpException) {
                int statusCode = httpException.getResponseCode();
                String errorCode = "HTTP_" + statusCode;
                String message = getHttpErrorMessage(statusCode, operation);
                log.error("HTTP error {} while {}: {}", statusCode, operation, httpException.getMessage());
                return EfmResponse.error(errorCode, message);
            }
            cause = cause.getCause();
        }

        if (e instanceof WebServiceException) {
            log.error("Web service error while {}: {}", operation, e.getMessage(), e);
            return EfmResponse.error("SERVICE_UNAVAILABLE",
                "Unable to connect to Court Record MDE service. Please try again later.");
        }

        log.error("Error {}: {}", operation, e.getMessage(), e);
        return EfmResponse.error("SYSTEM_ERROR", e.getMessage());
    }

    private String getHttpErrorMessage(int statusCode, String operation) {
        return switch (statusCode) {
            case 503 -> "Court Record MDE service is temporarily unavailable. Please try again later.";
            case 502 -> "Court Record MDE service gateway error. Please try again later.";
            case 504 -> "Court Record MDE service request timed out. Please try again later.";
            case 500 -> "Court Record MDE service encountered an internal error.";
            case 401 -> "Authentication failed with Court Record MDE service.";
            case 403 -> "Access denied to Court Record MDE service.";
            case 404 -> "Court Record MDE service endpoint not found.";
            default -> "Court Record MDE service returned HTTP " + statusCode + " while " + operation;
        };
    }
}
