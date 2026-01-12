package com.example.odyssey.fileandserver.controller;

import com.example.odyssey.fileandserver.dto.EfmResponse;
import com.example.odyssey.fileandserver.service.CodeServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/efm/codes")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "efm.enabled", havingValue = "true", matchIfMissing = true)
public class CodeServiceController {

    private final CodeServiceClient codeServiceClient;

    /**
     * Retrieves system-wide codes from the Tyler Code Service.
     *
     * <p>Available system code types:</p>
     * <ul>
     *   <li><code>location</code> - Court locations</li>
     *   <li><code>versions</code> - Code version checksums</li>
     *   <li><code>error</code> - Error codes</li>
     *   <li><code>country</code> - Country codes</li>
     *   <li><code>state</code> - State codes</li>
     *   <li><code>filingstatus</code> - Filing status codes</li>
     *   <li><code>datafield</code> - Data field configuration codes</li>
     * </ul>
     *
     * @param codeType the type of codes to retrieve
     * @return XML content containing the requested codes in OASIS Genericode format
     */
    @GetMapping(value = "/{codeType}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSystemCodes(@PathVariable("codeType") String codeType) {
        log.info("REST request to get system codes: {}", codeType);
        EfmResponse<String> response = codeServiceClient.getCodes(codeType);

        if (!response.isSuccess()) {
            log.error("Failed to retrieve codes {}: {} - {}", codeType, response.getErrorCode(), response.getErrorMessage());
            return ResponseEntity.internalServerError()
                    .body("<error><code>" + response.getErrorCode() + "</code><message>" +
                            response.getErrorMessage() + "</message></error>");
        }

        return ResponseEntity.ok(response.getData());
    }

    /**
     * Retrieves court-specific codes from the Tyler Code Service.
     *
     * <p>Court-specific code types include:</p>
     * <ul>
     *   <li><code>casecategory</code> - Case category codes</li>
     *   <li><code>casetype</code> - Case type codes</li>
     *   <li><code>partytype</code> - Party type codes</li>
     *   <li><code>filingcode</code> - Filing codes</li>
     *   <li><code>documenttype</code> - Document type codes</li>
     *   <li>And many more court-specific configurations</li>
     * </ul>
     *
     * @param courtLocation the court location identifier (e.g., "tyler", "tyler:dc")
     * @param codeType the type of codes to retrieve
     * @return XML content containing the requested codes in OASIS Genericode format
     */
    @GetMapping(value = "/{courtLocation}/{codeType}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getCourtSpecificCodes(
            @PathVariable("courtLocation") String courtLocation,
            @PathVariable("codeType") String codeType) {
        log.info("REST request to get court-specific codes: {} for location: {}", codeType, courtLocation);
        EfmResponse<String> response = codeServiceClient.getCodes(codeType, courtLocation);

        if (!response.isSuccess()) {
            log.error("Failed to retrieve codes {} for location {}: {} - {}",
                    codeType, courtLocation, response.getErrorCode(), response.getErrorMessage());
            return ResponseEntity.internalServerError()
                    .body("<error><code>" + response.getErrorCode() + "</code><message>" +
                            response.getErrorMessage() + "</message></error>");
        }

        return ResponseEntity.ok(response.getData());
    }

    /**
     * Retrieves codes and returns them wrapped in an EfmResponse JSON structure.
     *
     * @param codeType the type of codes to retrieve
     * @return JSON response with success/error status and XML content as string
     */
    @GetMapping(value = "/{codeType}/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EfmResponse<String>> getSystemCodesAsJson(@PathVariable("codeType") String codeType) {
        log.info("REST request to get system codes as JSON: {}", codeType);
        EfmResponse<String> response = codeServiceClient.getCodes(codeType);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves court-specific codes and returns them wrapped in an EfmResponse JSON structure.
     *
     * @param courtLocation the court location identifier
     * @param codeType the type of codes to retrieve
     * @return JSON response with success/error status and XML content as string
     */
    @GetMapping(value = "/{courtLocation}/{codeType}/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EfmResponse<String>> getCourtSpecificCodesAsJson(
            @PathVariable("courtLocation") String courtLocation,
            @PathVariable("codeType") String codeType) {
        log.info("REST request to get court-specific codes as JSON: {} for location: {}", codeType, courtLocation);
        EfmResponse<String> response = codeServiceClient.getCodes(codeType, courtLocation);
        return ResponseEntity.ok(response);
    }
}
