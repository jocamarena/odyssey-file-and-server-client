package com.example.odyssey.fileandserver.config;

import jakarta.xml.ws.BindingProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ResourceLoader;
import tyler.efm.services.EfmUserService;
import tyler.efm.services.IEfmUserService;

import org.apache.cxf.configuration.jsse.TLSClientParameters;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "efm.enabled", havingValue = "true", matchIfMissing = true)
public class CxfClientConfig {

    private final EfmProperties efmProperties;
    private final ResourceLoader resourceLoader;
    private final Bus bus;

    @Bean
    @Lazy
    public IEfmUserService efmUserServiceSoapClient() throws Exception {
        KeystorePasswordCallback.setKeystorePassword(efmProperties.getSecurity().getKeystore().getPassword());
        EfmUserService service = new EfmUserService();
        IEfmUserService port = service.getBasicHttpBindingIEfmUserService();

        configureEndpoint(port);
        configureWsSecurity(port);
        configureHttpConduit(port);
        configureLogging(port);

        return port;
    }

    private void configureEndpoint(IEfmUserService port) {
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
            efmProperties.getUserService().getEndpointUrl());
    }

    private void configureWsSecurity(IEfmUserService port) throws Exception {
        Client client = ClientProxy.getClient(port);
        Map<String, Object> requestContext = client.getRequestContext();

        // Configure security properties for CXF's policy-based WS-Security handler
        Crypto crypto = createCrypto();
        String keyAlias = getKeyAlias();

        // Set signature crypto and username for policy-based security
        requestContext.put("security.signature.crypto", crypto);
        requestContext.put("security.signature.username", keyAlias);
        requestContext.put("security.callback-handler", new KeystorePasswordCallback());

        log.info("WS-Security configured with X509 token for EFM User Service");
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

    private void configureHttpConduit(IEfmUserService port) throws Exception {
        Client client = ClientProxy.getClient(port);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(30000);
        httpClientPolicy.setReceiveTimeout(60000);
        httpClientPolicy.setAllowChunking(true);

        httpConduit.setClient(httpClientPolicy);

        // Configure TLS with custom truststore for HTTPS
        configureTls(httpConduit);
    }

    private void configureTls(HTTPConduit httpConduit) throws Exception {
        String truststorePath = efmProperties.getSecurity().getTruststore().getPath();
        if (truststorePath == null || truststorePath.isBlank()) {
            log.warn("No truststore configured, using JVM default truststore");
            return;
        }

        // Get JVM default TrustManager
        TrustManagerFactory defaultTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        defaultTmf.init((KeyStore) null); // null loads JVM default cacerts
        X509TrustManager defaultTm = getX509TrustManager(defaultTmf);

        // Load the custom Tyler EFM root CA certificate
        KeyStore customTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        customTrustStore.load(null, null);

        try (InputStream is = resourceLoader.getResource(truststorePath).getInputStream()) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(is);
            customTrustStore.setCertificateEntry("efm-root-ca", cert);
            log.info("Loaded custom CA certificate from: {}", truststorePath);
        }

        TrustManagerFactory customTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        customTmf.init(customTrustStore);
        X509TrustManager customTm = getX509TrustManager(customTmf);

        // Create composite TrustManager that trusts both custom CA and JVM defaults
        X509TrustManager compositeTm = new CompositeX509TrustManager(defaultTm, customTm);

        // Create SSLContext with composite TrustManager and set as JVM default
        // This ensures CXF 4.x's JDK HttpClient uses our combined truststore
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{compositeTm}, new SecureRandom());
        SSLContext.setDefault(sslContext);
        log.info("Composite SSLContext set as JVM default (custom CA + JVM defaults)");

        // Also set TLSClientParameters on the conduit for CXF-level configuration
        TLSClientParameters tlsParams = new TLSClientParameters();
        tlsParams.setTrustManagers(new TrustManager[]{compositeTm});
        httpConduit.setTlsClientParameters(tlsParams);

        log.info("TLS configured with composite truststore for EFM User Service");
    }

    private X509TrustManager getX509TrustManager(TrustManagerFactory tmf) {
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                return (X509TrustManager) tm;
            }
        }
        throw new IllegalStateException("No X509TrustManager found");
    }

    /**
     * Composite TrustManager that delegates to multiple TrustManagers.
     * Trusts a certificate if ANY of the delegate TrustManagers trusts it.
     */
    private static class CompositeX509TrustManager implements X509TrustManager {
        private final List<X509TrustManager> trustManagers;

        public CompositeX509TrustManager(X509TrustManager... managers) {
            this.trustManagers = Arrays.asList(managers);
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            CertificateException lastException = null;
            for (X509TrustManager tm : trustManagers) {
                try {
                    tm.checkClientTrusted(chain, authType);
                    return; // Trusted by this manager
                } catch (CertificateException e) {
                    lastException = e;
                }
            }
            throw lastException;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            CertificateException lastException = null;
            for (X509TrustManager tm : trustManagers) {
                try {
                    tm.checkServerTrusted(chain, authType);
                    return; // Trusted by this manager
                } catch (CertificateException e) {
                    lastException = e;
                }
            }
            throw lastException;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            List<X509Certificate> certificates = new ArrayList<>();
            for (X509TrustManager tm : trustManagers) {
                certificates.addAll(Arrays.asList(tm.getAcceptedIssuers()));
            }
            return certificates.toArray(new X509Certificate[0]);
        }
    }

    private void configureLogging(IEfmUserService port) {
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

        log.info("CXF SOAP message logging enabled for EFM User Service");
    }
}
