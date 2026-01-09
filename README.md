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

## Dependencies

- Spring Boot 4.0.1
- Apache CXF 4.1.1
- Apache WSS4J (for WS-Security)
- Lombok
