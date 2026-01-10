package com.example.odyssey.fileandserver.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import tyler.efm.services.IEfmFirmService;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "efm.enabled", havingValue = "true", matchIfMissing = true)
public class CxfFirmServiceClientConfig {

    private final EfmProperties efmProperties;
    private final ResourceLoader resourceLoader;

    @Bean
    public IEfmFirmService efmFirmServiceSoapClient() throws Exception {
        KeystorePasswordCallback.setKeystorePassword(efmProperties.getSecurity().getKeystore().getPassword());

        // Use JaxWsProxyFactoryBean to avoid runtime WSDL parsing
        // This prevents CXF from fetching external XSD schemas from the remote server
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(IEfmFirmService.class);
        factory.setAddress(efmProperties.getFirmService().getEndpointUrl());

        IEfmFirmService port = (IEfmFirmService) factory.create();

        configureWsSecurity(port);
        configureHttpConduit(port);
        configureLogging(port);

        return port;
    }

    private void configureWsSecurity(IEfmFirmService port) throws Exception {
        Client client = ClientProxy.getClient(port);
        Map<String, Object> requestContext = client.getRequestContext();

        Crypto crypto = createCrypto();
        String keyAlias = getKeyAlias();

        requestContext.put("security.signature.crypto", crypto);
        requestContext.put("security.signature.username", keyAlias);
        requestContext.put("security.callback-handler", new KeystorePasswordCallback());

        log.info("WS-Security configured with X509 token for EFM Firm Service");
    }

    private Crypto createCrypto() throws Exception {
        KeyStore keyStore = loadKeyStore();
        Merlin merlin = new Merlin();
        merlin.setKeyStore(keyStore);
        return merlin;
    }

    private String getKeyAlias() throws Exception {
        KeyStore keyStore = loadKeyStore();
        return keyStore.aliases().nextElement();
    }

    private KeyStore loadKeyStore() throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance(efmProperties.getSecurity().getKeystore().getType());
        String keystorePath = efmProperties.getSecurity().getKeystore().getPath();

        try (var is = resourceLoader.getResource(keystorePath).getInputStream()) {
            keyStore.load(is, efmProperties.getSecurity().getKeystore().getPassword().toCharArray());
        }
        return keyStore;
    }

    private void configureHttpConduit(IEfmFirmService port) {
        Client client = ClientProxy.getClient(port);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(30000);
        httpClientPolicy.setReceiveTimeout(60000);
        httpClientPolicy.setAllowChunking(true);

        httpConduit.setClient(httpClientPolicy);
    }

    private void configureLogging(IEfmFirmService port) {
        Client client = ClientProxy.getClient(port);

        LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
        loggingInInterceptor.setPrettyLogging(true);
        loggingInInterceptor.setLogBinary(false);
        loggingInInterceptor.setLogMultipart(true);

        LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
        loggingOutInterceptor.setPrettyLogging(true);
        loggingOutInterceptor.setLogBinary(false);
        loggingOutInterceptor.setLogMultipart(true);

        client.getInInterceptors().add(loggingInInterceptor);
        client.getOutInterceptors().add(loggingOutInterceptor);

        log.info("CXF SOAP message logging enabled for EFM Firm Service");
    }
}
