# Odyssey - EFM User Service SOAP Client

A Spring Boot application that provides REST API endpoints to interact with the Tyler Technologies EFM (Electronic Filing Manager) User Service via SOAP with X509 certificate authentication.

## Prerequisites

- Java 17+
- Maven 3.6+
- Valid X509 certificate (bitlink.pfx) in the `src/main/resources/certs/` directory

## Configuration

### Certificate Password

Set the keystore password as an environment variable:

```bash
export EFM_KEYSTORE_PASSWORD=your_certificate_password
```

Or update `application.yaml`:

```yaml
efm:
  security:
    keystore:
      password: your_certificate_password
```

### Service Endpoint

The default endpoint is configured for the staging environment. To change it, update `application.yaml`:

```yaml
efm:
  user-service:
    endpoint-url: https://california-efm-stage.tylertech.cloud/EFM/EFMUserService.svc
```

## Building the Project

```bash
mvn clean install
```

This will:
1. Generate SOAP client stubs from the WSDL file
2. Compile the application
3. Package the application

## Running the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/odyssey-0.0.1-SNAPSHOT.jar
```

The application will start on port 8080 by default.

## Available REST API Operations

All endpoints are prefixed with `/api/efm/user`

### 1. Authenticate User

Authenticates a user with email and password.

**Endpoint:** `POST /api/efm/user/authenticate`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "userpassword"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "userID": "12345",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

---

### 2. Get User

Retrieves user information by user ID.

**Endpoint:** `GET /api/efm/user/{userId}`

**Response:**
```json
{
  "success": true,
  "data": {
    "userID": "12345",
    "firmID": "67890",
    "email": "user@example.com",
    "firstName": "John",
    "middleName": "M",
    "lastName": "Doe",
    "isActive": true,
    "isApproved": true
  }
}
```

---

### 3. Update User

Updates user information.

**Endpoint:** `PUT /api/efm/user/{userId}`

**Request Body:**
```json
{
  "firstName": "John",
  "middleName": "M",
  "lastName": "Doe",
  "email": "john.doe@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "message": "User updated successfully"
  }
}
```

---

### 4. Change Password

Changes the user's password (requires current session authentication).

**Endpoint:** `POST /api/efm/user/change-password`

**Request Body:**
```json
{
  "email": "user@example.com",
  "oldPassword": "currentpassword",
  "newPassword": "newpassword"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "message": "Password changed successfully"
  }
}
```

---

### 5. Reset Password

Resets the user's password using security question answer.

**Endpoint:** `POST /api/efm/user/reset-password`

**Request Body:**
```json
{
  "email": "user@example.com",
  "passwordAnswer": "security answer"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "message": "Password reset successfully"
  }
}
```

---

### 6. Get Password Question

Retrieves the password security question for a user.

**Endpoint:** `GET /api/efm/user/password-question?email={email}`

**Response:**
```json
{
  "success": true,
  "data": {
    "passwordQuestion": "What is your mother's maiden name?"
  }
}
```

---

### 7. Get Notification Preferences

Retrieves notification preferences for the current user.

**Endpoint:** `GET /api/efm/user/{userId}/notification-preferences`

**Response:**
```json
{
  "success": true,
  "data": {
    "notifications": [...]
  }
}
```

---

### 8. Update Notification Preferences

Updates notification preferences for a user.

**Endpoint:** `PUT /api/efm/user/{userId}/notification-preferences`

**Request Body:**
```json
{
  "preferences": [
    {
      "notificationType": "EMAIL",
      "enabled": true
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "message": "Notification preferences updated successfully"
  }
}
```

---

### 9. Resend Activation Email

Resends the activation email to a user.

**Endpoint:** `POST /api/efm/user/resend-activation-email`

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "message": "Activation email sent successfully"
  }
}
```

## Error Response Format

When an error occurs, the response will have the following format:

```json
{
  "success": false,
  "errorCode": "ERROR_CODE",
  "errorMessage": "Description of the error"
}
```

## Project Structure

```
src/main/java/com/example/odyssey/fileandserver/
├── OdysseyApplication.java          # Main application entry point
├── config/
│   ├── CxfClientConfig.java         # CXF SOAP client configuration with X509 security
│   ├── EfmProperties.java           # Configuration properties
│   └── KeystorePasswordCallback.java # WS-Security password callback
├── controller/
│   └── EfmUserController.java       # REST controller
├── dto/
│   ├── AuthenticateUserRequest.java
│   ├── ChangePasswordRequest.java
│   ├── EfmResponse.java
│   ├── GetNotificationPreferencesRequest.java
│   ├── GetPasswordQuestionRequest.java
│   ├── GetUserRequest.java
│   ├── ResetPasswordRequest.java
│   ├── SelfResendActivationEmailRequest.java
│   ├── UpdateNotificationPreferencesRequest.java
│   └── UpdateUserRequest.java
└── service/
    └── EfmUserServiceClient.java    # SOAP service client wrapper

src/main/resources/
├── application.yaml                  # Application configuration
├── certs/
│   ├── bitlink.pfx                  # X509 certificate (PKCS12)
│   └── tylerofsefmrootsha2.crt      # Trust certificate
└── wsdl/
    └── EfmUserService.wsdl          # WSDL file for code generation

target/generated-sources/cxf/        # Generated SOAP client stubs (after build)
└── tyler/efm/services/              # Generated service interfaces and types
```

## Security

This application uses WS-Security with X509 token authentication. The SOAP messages are signed using the private key from the provided PKCS12 certificate file.

The security configuration includes:
- Message signing with X509 certificate
- Timestamp in SOAP header
- Direct reference key identifier

### WS-Security Policies (from WSDL)

The WS-Security header structure is determined by policies embedded in each service's WSDL file. All three services (`EfmUserService.wsdl`, `EFMFirmService.wsdl`, `CourtRecordMDEService.wsdl`) use identical security policies.

#### Primary Binding Policy

The primary binding (e.g., `BasicHttpBinding_IEfmUserService_policy`) requires X509 token authentication:

```xml
<wsp:Policy wsu:Id="BasicHttpBinding_IEfmUserService_policy">
  <wsp:ExactlyOne>
    <wsp:All>
      <!-- Transport security via HTTPS -->
      <sp:TransportBinding xmlns:sp="http://schemas.xmlsoap.org/ws/2005/07/securitypolicy">
        <wsp:Policy>
          <sp:TransportToken>
            <wsp:Policy>
              <sp:HttpsToken RequireClientCertificate="false"/>
            </wsp:Policy>
          </sp:TransportToken>
          <sp:AlgorithmSuite>
            <wsp:Policy>
              <sp:Basic256/>  <!-- SHA-256/AES-256 algorithms -->
            </wsp:Policy>
          </sp:AlgorithmSuite>
          <sp:Layout>
            <wsp:Policy>
              <sp:Lax/>  <!-- Flexible header ordering -->
            </wsp:Policy>
          </sp:Layout>
          <sp:IncludeTimestamp/>  <!-- Requires wsu:Timestamp -->
        </wsp:Policy>
      </sp:TransportBinding>

      <!-- X509 token requirement -->
      <sp:EndorsingSupportingTokens xmlns:sp="http://schemas.xmlsoap.org/ws/2005/07/securitypolicy">
        <wsp:Policy>
          <sp:X509Token sp:IncludeToken=".../IncludeToken/AlwaysToRecipient">
            <wsp:Policy>
              <sp:WssX509V3Token10/>  <!-- X.509 v3 certificate -->
            </wsp:Policy>
          </sp:X509Token>
        </wsp:Policy>
      </sp:EndorsingSupportingTokens>

      <!-- WSS 1.0 token reference requirements -->
      <sp:Wss10 xmlns:sp="http://schemas.xmlsoap.org/ws/2005/07/securitypolicy">
        <wsp:Policy>
          <sp:MustSupportRefKeyIdentifier/>
          <sp:MustSupportRefIssuerSerial/>
        </wsp:Policy>
      </sp:Wss10>
    </wsp:All>
  </wsp:ExactlyOne>
</wsp:Policy>
```

| Policy Element | Purpose |
|----------------|---------|
| `sp:TransportBinding` | Security at transport layer (HTTPS) |
| `sp:HttpsToken` | Requires HTTPS; `RequireClientCertificate="false"` means TLS client cert is optional |
| `sp:Basic256` | Algorithm suite: RSA-SHA256 signatures, AES-256 encryption |
| `sp:IncludeTimestamp` | Requires `wsu:Timestamp` in Security header |
| `sp:EndorsingSupportingTokens` | X509 token signs the message timestamp |
| `sp:X509Token` with `AlwaysToRecipient` | Certificate must be included in every request |
| `sp:WssX509V3Token10` | X.509 v3 certificate format required |
| `sp:MustSupportRefKeyIdentifier` | Must support Key Identifier references |
| `sp:MustSupportRefIssuerSerial` | Must support Issuer/Serial references |

#### Secondary Binding Policy (Test Endpoints)

The secondary binding (e.g., `BasicHttpBinding_IEfmUserService1_policy`) is used for `/test` endpoints and requires only HTTPS transport without X509 token signing:

```xml
<wsp:Policy wsu:Id="BasicHttpBinding_IEfmUserService1_policy">
  <wsp:ExactlyOne>
    <wsp:All>
      <sp:TransportBinding xmlns:sp="http://schemas.xmlsoap.org/ws/2005/07/securitypolicy">
        <wsp:Policy>
          <sp:TransportToken>
            <wsp:Policy>
              <sp:HttpsToken RequireClientCertificate="false"/>
            </wsp:Policy>
          </sp:TransportToken>
          <sp:AlgorithmSuite>
            <wsp:Policy>
              <sp:Basic256/>
            </wsp:Policy>
          </sp:AlgorithmSuite>
          <sp:Layout>
            <wsp:Policy>
              <sp:Strict/>  <!-- Strict header ordering -->
            </wsp:Policy>
          </sp:Layout>
          <!-- No sp:IncludeTimestamp -->
          <!-- No sp:EndorsingSupportingTokens -->
        </wsp:Policy>
      </sp:TransportBinding>
    </wsp:All>
  </wsp:ExactlyOne>
</wsp:Policy>
```

| Difference from Primary | Description |
|------------------------|-------------|
| No `sp:IncludeTimestamp` | Timestamp not required |
| No `sp:EndorsingSupportingTokens` | X509 signing not required |
| `sp:Strict` layout | Strict ordering of security header elements |

### Test Endpoints (`/test`)

Each WSDL defines two service ports with different endpoints and security policies:

| Service | Production Endpoint | Test Endpoint |
|---------|---------------------|---------------|
| EFM User Service | `.../EFMUserService.svc` | `.../EFMUserService.svc/test` |
| EFM Firm Service | `.../EFMFirmService.svc` | `.../EFMFirmService.svc/test` |
| Court Record MDE Service | `.../CourtRecordMDEService.svc` | `.../CourtRecordMDEService.svc/test` |

**Purpose:** The `/test` endpoints allow testing SOAP API requests **without requiring X509 certificate authentication**. They use the secondary binding policy which only requires HTTPS transport security.

**When to use `/test` endpoints:**
- Validating SOAP request/response structure without certificate setup
- Testing during development before obtaining production certificates
- Debugging API integration issues in isolation from security concerns
- Verifying service connectivity and message format

**Security difference:**
```
Production endpoint: HTTPS + X509 Token Signature + Timestamp
Test endpoint:       HTTPS only (no WS-Security header required)
```

**Example WSDL port definitions:**
```xml
<!-- Production port - requires X509 authentication -->
<wsdl:port name="BasicHttpBinding_IEfmUserService"
           binding="tns:BasicHttpBinding_IEfmUserService">
  <soap:address location="https://california-efm.tylertech.cloud/EFM/EFMUserService.svc"/>
</wsdl:port>

<!-- Test port - HTTPS only, no X509 required -->
<wsdl:port name="BasicHttpBinding_IEfmUserService1"
           binding="tns:BasicHttpBinding_IEfmUserService1">
  <soap:address location="https://california-efm.tylertech.cloud/EFM/EFMUserService.svc/test"/>
</wsdl:port>
```

**To use test endpoints in this application**, update the endpoint URL in `application.yaml`:

```yaml
efm:
  user-service:
    endpoint-url: https://california-efm-stage.tylertech.cloud/EFM/EFMUserService.svc/test
  firm-service:
    endpoint-url: https://california-efm-stage.tylertech.cloud/EFM/EFMFirmService.svc/test
  court-record-service:
    endpoint-url: https://california-efm-stage.tylertech.cloud/EFM/CourtRecordMDEService.svc/test
```

> **Note:** When using `/test` endpoints, the WS-Security configuration in the CXF client configs will still add security headers, but the server will accept requests without validating them. For true "no security" testing, you would need to modify the client configuration to skip WS-Security header generation.

### WS-Security Header Example

Based on the WSDL security policy (`TransportBinding` with `EndorsingSupportingTokens` using `X509Token`), the SOAP security header sent to EFM services follows this structure:

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Header>
    <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
                   xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">

      <!-- Timestamp: Required by sp:IncludeTimestamp policy -->
      <wsu:Timestamp wsu:Id="TS-1">
        <wsu:Created>2026-01-10T22:30:00.000Z</wsu:Created>
        <wsu:Expires>2026-01-10T22:35:00.000Z</wsu:Expires>
      </wsu:Timestamp>

      <!-- BinarySecurityToken: X.509 certificate (base64 encoded) -->
      <!-- Required by sp:X509Token with IncludeToken="AlwaysToRecipient" -->
      <wsse:BinarySecurityToken
          wsu:Id="X509-1"
          ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3"
          EncodingType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary">
        MIIFazCCA1OgAwIBAgIUe5L...<!-- Base64 encoded X.509 certificate -->...
      </wsse:BinarySecurityToken>

      <!-- Signature: Signs the Timestamp (EndorsingSupportingTokens pattern) -->
      <ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#" Id="SIG-1">
        <ds:SignedInfo>
          <ds:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/>
          <ds:SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"/>
          <ds:Reference URI="#TS-1">
            <ds:Transforms>
              <ds:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/>
            </ds:Transforms>
            <ds:DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/>
            <ds:DigestValue>dGhpcyBpcyBhIHNhbXBsZSBkaWdlc3QgdmFsdWU=</ds:DigestValue>
          </ds:Reference>
        </ds:SignedInfo>
        <ds:SignatureValue>c2lnbmF0dXJlIHZhbHVlIGhlcmU=...<!-- Base64 signature -->...</ds:SignatureValue>
        <ds:KeyInfo>
          <wsse:SecurityTokenReference>
            <!-- Direct reference to the BinarySecurityToken -->
            <wsse:Reference URI="#X509-1"
                ValueType="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3"/>
          </wsse:SecurityTokenReference>
        </ds:KeyInfo>
      </ds:Signature>

    </wsse:Security>
  </soap:Header>
  <soap:Body>
    <!-- SOAP request body -->
  </soap:Body>
</soap:Envelope>
```

**Key elements explained:**

| Element | Purpose |
|---------|---------|
| `wsu:Timestamp` | Prevents replay attacks; contains creation and expiration times |
| `wsse:BinarySecurityToken` | Contains the X.509 certificate used for signing |
| `ds:Signature` | Digital signature over the Timestamp element |
| `ds:Reference URI="#TS-1"` | Indicates the Timestamp is signed |
| `wsse:SecurityTokenReference` | Links the signature to the BinarySecurityToken |

> **Note:** This is a theoretical example based on the WSDL security policy. The actual values (certificate, signature, digest) are generated at runtime by Apache CXF/WSS4J.

## Apache CXF

This project uses [Apache CXF](https://cxf.apache.org/) as the SOAP client framework for communicating with Tyler Technologies EFM SOAP web services. CXF provides a comprehensive implementation of JAX-WS and handles the complexities of SOAP messaging, WS-Security, and HTTP transport.

### Role in the Project

Apache CXF serves two primary purposes:

1. **Code Generation**: Generates Java client stubs from WSDL files at build time
2. **Runtime SOAP Client**: Provides the infrastructure for making secured SOAP calls with X509 certificate authentication

### Code Generation (cxf-codegen-plugin)

The `cxf-codegen-plugin` in `pom.xml` automatically generates Java classes from WSDL files during the `generate-sources` Maven phase:

```xml
<plugin>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-codegen-plugin</artifactId>
    <version>${cxf.version}</version>
    ...
</plugin>
```

Generated code is placed in `target/generated-sources/cxf/` and includes:
- Service interfaces (e.g., `IEfmUserService`, `IEfmFirmService`, `CourtRecordMDEService`)
- Request/response types
- JAX-WS service classes

### CXF Configuration Classes

The project has three CXF configuration classes, each responsible for configuring a SOAP client for a specific EFM service:

| Class | Service | Description |
|-------|---------|-------------|
| `CxfClientConfig` | EFM User Service | User authentication and management |
| `CxfCourtRecordClientConfig` | Court Record MDE Service | Court record queries |
| `CxfFirmServiceClientConfig` | EFM Firm Service | Law firm management |

All configuration classes follow the same pattern with four key configurations:

#### 1. Endpoint Configuration

Sets the target SOAP service URL from `application.yaml`:

```java
BindingProvider bp = (BindingProvider) port;
bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
```

#### 2. WS-Security Configuration

Configures X509 certificate-based message signing using Apache WSS4J:

```java
Crypto crypto = createCrypto();  // Loads keystore via Merlin
requestContext.put("security.signature.crypto", crypto);
requestContext.put("security.signature.username", keyAlias);
requestContext.put("security.callback-handler", new KeystorePasswordCallback());
```

- **Merlin**: WSS4J's crypto provider that wraps the PKCS12 keystore
- **KeystorePasswordCallback**: Provides the keystore password to WSS4J when signing messages

#### 3. HTTP Conduit Configuration

Configures HTTP transport settings via CXF's `HTTPConduit`:

```java
HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
httpClientPolicy.setConnectionTimeout(30000);  // 30 seconds
httpClientPolicy.setReceiveTimeout(60000);     // 60 seconds
httpClientPolicy.setAllowChunking(true);
```

#### 4. Logging Interceptors

Adds CXF interceptors for SOAP message logging (useful for debugging):

```java
client.getInInterceptors().add(new LoggingInInterceptor());
client.getOutInterceptors().add(new LoggingOutInterceptor());
```

### KeystorePasswordCallback

`KeystorePasswordCallback.java` implements `CallbackHandler` to provide the keystore password to WSS4J during signature operations:

```java
public class KeystorePasswordCallback implements CallbackHandler {
    @Override
    public void handle(Callback[] callbacks) {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback wsPasswordCallback) {
                wsPasswordCallback.setPassword(keystorePassword);
            }
        }
    }
}
```

### TLS Configuration

`CxfClientConfig` includes a custom TLS configuration with a `CompositeX509TrustManager` that combines:
- JVM default trusted certificates (cacerts)
- Custom Tyler EFM root CA certificate

This allows HTTPS connections to trust both standard CAs and the EFM-specific certificate chain.

### CXF Dependencies

```xml
<!-- Core JAX-WS support with Spring Boot integration -->
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
</dependency>

<!-- WS-Security (signing, encryption) -->
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-ws-security</artifactId>
</dependency>

<!-- WS-Policy support -->
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-ws-policy</artifactId>
</dependency>

<!-- HTTP transport -->
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-transports-http</artifactId>
</dependency>

<!-- SOAP message logging -->
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-features-logging</artifactId>
</dependency>
```

## SOAP UI

You can use [SOAP UI](https://www.soapui.org/) to test EFM SOAP API requests with WS-Security headers. This section explains how to configure SOAP UI to add X509 token signatures and timestamps.

### Step 1: Create a New SOAP Project

1. Open SOAP UI
2. Go to **File → New SOAP Project**
3. Enter a project name (e.g., "EFM Services")
4. For **Initial WSDL**, browse to one of the WSDL files:
   - `src/main/resources/wsdl/EfmUserService.wsdl`
   - `src/main/resources/wsdl/EFMFirmService.wsdl`
   - `src/main/resources/wsdl/CourtRecordMDEService.wsdl`
5. Click **OK** to create the project

### Step 2: Configure the Keystore

1. Double-click on the project name in the Navigator panel to open **Project View**
2. Go to the **WS-Security Configurations** tab
3. Click the **Keystores** sub-tab
4. Click the **+** button to add a new keystore
5. Configure the keystore:
   - **Source**: Browse to `src/main/resources/certs/bitlink.pfx`
   - **Password**: Enter your keystore password
   - **Status**: Should show "OK" if the password is correct

### Step 3: Create an Outgoing WS-Security Configuration

1. Still in the **WS-Security Configurations** tab, click the **Outgoing WS-Security Configurations** sub-tab
2. Click the **+** button and enter a name (e.g., "EFM-X509-Signature")
3. With the new configuration selected, you need to add three entries in the order below:

#### Entry 1: Timestamp

Click the **+** button in the lower panel and select **Timestamp**:

| Setting | Value |
|---------|-------|
| Time To Live | 300 (5 minutes) |
| Millisecond Precision | checked |

#### Entry 2: Binary Security Token (X509 Certificate)

Click **+** and select **Signature**:

| Setting | Value |
|---------|-------|
| Keystore | Select your keystore (bitlink.pfx) |
| Alias | Select the certificate alias from dropdown |
| Password | Enter the private key password (usually same as keystore) |
| Key Identifier Type | **Binary Security Token** |
| Signature Algorithm | http://www.w3.org/2001/04/xmldsig-more#rsa-sha256 |
| Signature Canonicalization | http://www.w3.org/2001/10/xml-exc-c14n# |
| Digest Algorithm | http://www.w3.org/2001/04/xmlenc#sha256 |
| Use Single Certificate | checked |

#### Entry 3: Configure Parts to Sign

In the **Signature** entry you just created, scroll down to **Parts**:

1. Click **+** to add a part to sign
2. Configure to sign the Timestamp:
   - **ID**: (leave empty)
   - **Name**: Timestamp
   - **Namespace**: http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd
   - **Encode**: (leave empty)

### Step 4: Apply WS-Security to Requests

1. Expand your SOAP project in the Navigator
2. Expand the service binding (e.g., `BasicHttpBinding_IEfmUserService`)
3. Double-click on an operation (e.g., `AuthenticateUser`)
4. In the request editor, click on the **Auth** tab at the bottom
5. Set the following:
   - **Authorization**: Basic (or leave as No Authorization)
6. Click on the **WS-A** tab (if addressing is needed) or skip
7. Click on the request properties panel (bottom-left of request window)
8. Find **Outgoing WSS** property and select your configuration: `EFM-X509-Signature`

### Step 5: Configure SSL/TLS (if needed)

If you encounter SSL certificate errors:

1. Go to **File → Preferences → SSL Settings**
2. Configure the following:
   - **KeyStore**: Path to `bitlink.pfx`
   - **KeyStore Password**: Your keystore password
   - **TrustStore**: Path to a truststore containing Tyler's CA certificate (or use `tylerofsefmrootsha2.crt`)

### Step 6: Send the Request

1. Fill in the request body with appropriate values
2. Click the green **Play** button to send the request
3. View the response in the right panel

### Viewing the Raw Request

To verify the WS-Security header is correctly formed:

1. After sending a request, click the **Raw** tab in the request panel
2. You should see something like:

```xml
<soap:Header>
  <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
    <wsu:Timestamp wsu:Id="Timestamp-...">
      <wsu:Created>2026-01-11T12:00:00.000Z</wsu:Created>
      <wsu:Expires>2026-01-11T12:05:00.000Z</wsu:Expires>
    </wsu:Timestamp>
    <wsse:BinarySecurityToken ValueType="...#X509v3" EncodingType="...#Base64Binary" wsu:Id="X509-...">
      MIIF...
    </wsse:BinarySecurityToken>
    <ds:Signature>
      ...
    </ds:Signature>
  </wsse:Security>
</soap:Header>
```

### Testing with `/test` Endpoints (No WS-Security)

To test without WS-Security headers:

1. Change the endpoint URL to use `/test`:
   - Right-click on the request → **Change Endpoint**
   - Enter: `https://california-efm-stage.tylertech.cloud/EFM/EFMUserService.svc/test`
2. Remove the **Outgoing WSS** configuration (set to "None")
3. Send the request - no WS-Security header will be required

### Troubleshooting

| Issue | Solution |
|-------|----------|
| "Keystore not found" | Verify the path to `bitlink.pfx` is correct |
| "Invalid password" | Check keystore password; try the same password for alias |
| "Certificate chain not trusted" | Add Tyler's root CA to SOAP UI's truststore |
| "Signature validation failed" | Ensure Key Identifier Type is "Binary Security Token" |
| "Timestamp expired" | Increase Time To Live or check system clock |
| SSL handshake errors | Import Tyler's CA cert into SOAP UI SSL settings |

## Dependencies

- Spring Boot 4.0.1
- Apache CXF 4.1.1
- Apache WSS4J (for WS-Security)
- Lombok
