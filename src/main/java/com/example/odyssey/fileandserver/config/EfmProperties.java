package com.example.odyssey.fileandserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "efm")
public class EfmProperties {

    private UserService userService = new UserService();
    private CourtRecordService courtRecordService = new CourtRecordService();
    private FirmService firmService = new FirmService();
    private CodeService codeService = new CodeService();
    private Security security = new Security();

    @Data
    public static class UserService {
        private String wsdlLocation;
        private String endpointUrl;
    }

    @Data
    public static class CourtRecordService {
        private String wsdlLocation;
        private String endpointUrl;
    }

    @Data
    public static class FirmService {
        private String wsdlLocation;
        private String endpointUrl;
    }

    @Data
    public static class CodeService {
        private String baseUrl;
    }

    @Data
    public static class Security {
        private Keystore keystore = new Keystore();
        private Truststore truststore = new Truststore();
    }

    @Data
    public static class Keystore {
        private String path;
        private String password;
        private String type = "PKCS12";
    }

    @Data
    public static class Truststore {
        private String path;
    }
}
