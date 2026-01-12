package com.example.odyssey.fileandserver.service;

import com.example.odyssey.fileandserver.config.EfmProperties;
import com.example.odyssey.fileandserver.dto.EfmResponse;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@ConditionalOnProperty(name = "efm.enabled", havingValue = "true", matchIfMissing = true)
public class CodeServiceClient {

    private final EfmProperties efmProperties;
    private final ResourceLoader resourceLoader;
    private final HttpClient httpClient;

    private PrivateKey privateKey;
    private X509Certificate certificate;

    public CodeServiceClient(EfmProperties efmProperties, ResourceLoader resourceLoader) {
        this.efmProperties = efmProperties;
        this.resourceLoader = resourceLoader;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @PostConstruct
    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        loadKeyStoreCredentials();
    }

    private void loadKeyStoreCredentials() throws Exception {
        String keystorePath = efmProperties.getSecurity().getKeystore().getPath();
        String keystorePassword = efmProperties.getSecurity().getKeystore().getPassword();
        String keystoreType = efmProperties.getSecurity().getKeystore().getType();

        KeyStore keyStore = KeyStore.getInstance(keystoreType);
        try (InputStream is = resourceLoader.getResource(keystorePath).getInputStream()) {
            keyStore.load(is, keystorePassword.toCharArray());
        }

        String alias = keyStore.aliases().nextElement();
        this.privateKey = (PrivateKey) keyStore.getKey(alias, keystorePassword.toCharArray());
        this.certificate = (X509Certificate) keyStore.getCertificate(alias);

        log.info("Loaded keystore credentials for Code Service authentication, alias: {}", alias);
    }

    /**
     * Retrieves codes from the Tyler Code Service.
     *
     * @param codeType the type of code to retrieve (e.g., "location", "country", "state", "error", "filingstatus", "datafield", "versions")
     * @return EfmResponse containing the XML content or error information
     */
    public EfmResponse<String> getCodes(String codeType) {
        return getCodes(codeType, null);
    }

    /**
     * Retrieves court-specific codes from the Tyler Code Service.
     *
     * @param codeType the type of code to retrieve
     * @param courtLocation optional court location identifier for court-specific codes
     * @return EfmResponse containing the XML content or error information
     */
    public EfmResponse<String> getCodes(String codeType, String courtLocation) {
        try {
            String url = buildUrl(codeType, courtLocation);
            log.info("Fetching codes from: {}", url);

            String authHeader = generateAuthHeader();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("tyl-api-auth", authHeader)
                    .header("Accept", "application/zip, application/xml")
                    .timeout(Duration.ofSeconds(60))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                log.error("Code Service returned HTTP {}: {}", response.statusCode(),
                        new String(response.body()));
                return EfmResponse.error("HTTP_" + response.statusCode(),
                        "Code Service returned HTTP " + response.statusCode());
            }

            String contentType = response.headers().firstValue("Content-Type").orElse("");
            byte[] body = response.body();

            String xmlContent;
            if (contentType.contains("zip") || isZipContent(body)) {
                xmlContent = decompressZip(body);
            } else {
                xmlContent = new String(body);
            }

            log.info("Successfully retrieved {} codes", codeType);
            return EfmResponse.success(xmlContent);

        } catch (Exception e) {
            log.error("Error fetching codes for type {}: {}", codeType, e.getMessage(), e);
            return EfmResponse.error("CODE_SERVICE_ERROR", e.getMessage());
        }
    }

    private String buildUrl(String codeType, String courtLocation) {
        String baseUrl = efmProperties.getCodeService().getBaseUrl();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        if (courtLocation != null && !courtLocation.isBlank()) {
            // Court-specific codes: /codes/{courtLocation}/{codeType}/
            return baseUrl.replace("/codes/", "/codes/" + courtLocation + "/") + codeType + "/";
        } else {
            // System codes: /codes/{codeType}/
            return baseUrl + codeType + "/";
        }
    }

    /**
     * Generates the tyl-api-auth header value.
     * The header contains the current timestamp, signed via CMS using the X.509 certificate, then base64 encoded.
     */
    private String generateAuthHeader() throws Exception {
        // Generate timestamp in ISO 8601 format
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now().atZone(ZoneOffset.UTC));

        // Sign the timestamp using CMS (Cryptographic Message Syntax)
        byte[] signedData = signWithCms(timestamp.getBytes());

        // Base64 encode the signed data
        return Base64.getEncoder().encodeToString(signedData);
    }

    /**
     * Signs data using CMS (Cryptographic Message Syntax) with the X.509 certificate.
     */
    private byte[] signWithCms(byte[] data) throws Exception {
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC")
                .build(privateKey);

        generator.addSignerInfoGenerator(
                new JcaSignerInfoGeneratorBuilder(
                        new JcaDigestCalculatorProviderBuilder()
                                .setProvider("BC")
                                .build())
                        .build(signer, certificate));

        generator.addCertificates(new JcaCertStore(Collections.singletonList(certificate)));

        CMSProcessableByteArray content = new CMSProcessableByteArray(data);
        CMSSignedData signedData = generator.generate(content, true);

        return signedData.getEncoded();
    }

    private boolean isZipContent(byte[] data) {
        // ZIP files start with PK (0x50 0x4B)
        return data.length >= 2 && data[0] == 0x50 && data[1] == 0x4B;
    }

    private String decompressZip(byte[] zipData) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry = zis.getNextEntry();
            if (entry != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                return baos.toString();
            }
        }
        throw new IllegalStateException("ZIP file is empty");
    }
}
