package com.example.odyssey.fileandserver.service;

import com.example.odyssey.fileandserver.dto.*;
import jakarta.xml.ws.WebServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.transport.http.HTTPException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tyler.efm.services.IEfmUserService;
import tyler.efm.services.schema.authenticaterequest.AuthenticateRequestType;
import tyler.efm.services.schema.authenticateresponse.AuthenticateResponseType;
import tyler.efm.services.schema.baseresponse.BaseResponseType;
import tyler.efm.services.schema.changepasswordrequest.ChangePasswordRequestType;
import tyler.efm.services.schema.changepasswordresponse.ChangePasswordResponseType;
import tyler.efm.services.schema.common.UserType;
import tyler.efm.services.schema.getpasswordquestionrequest.GetPasswordQuestionRequestType;
import tyler.efm.services.schema.getuserrequest.GetUserRequestType;
import tyler.efm.services.schema.getuserresponse.GetUserResponseType;
import tyler.efm.services.schema.notificationpreferencesresponse.NotificationPreferencesResponseType;
import tyler.efm.services.schema.passwordquestionresponse.PasswordQuestionResponseType;
import tyler.efm.services.schema.resetpasswordrequest.ResetPasswordRequestType;
import tyler.efm.services.schema.resetpasswordresponse.ResetPasswordResponseType;
import tyler.efm.services.schema.selfresendactivationemailrequest.SelfResendActivationEmailRequestType;
import tyler.efm.services.schema.updatenotificationpreferencesrequest.UpdateNotificationPreferencesRequestType;
import tyler.efm.services.schema.updateuserrequest.UpdateUserRequestType;
import tyler.efm.services.schema.updateuserresponse.UpdateUserResponseType;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "efm.enabled", havingValue = "true", matchIfMissing = true)
public class EfmUserServiceClient {

    @Lazy
    private final IEfmUserService efmUserServiceClient;

    public EfmResponse<Map<String, Object>> authenticateUser(AuthenticateUserRequest request) {
        try {
            log.info("Authenticating user: {}", request.getEmail());

            AuthenticateRequestType soapRequest = new AuthenticateRequestType();
            soapRequest.setEmail(request.getEmail());
            soapRequest.setPassword(request.getPassword());

            AuthenticateResponseType response = efmUserServiceClient.authenticateUser(soapRequest);

            if (isError(response.getError())) {
                return EfmResponse.error(
                        response.getError().getErrorCode(),
                        response.getError().getErrorText()
                );
            }

            Map<String, Object> data = new HashMap<>();
            data.put("userID", response.getUserID() != null ? response.getUserID() : "");
            data.put("email", response.getEmail() != null ? response.getEmail() : "");
            data.put("firstName", response.getFirstName() != null ? response.getFirstName() : "");
            data.put("lastName", response.getLastName() != null ? response.getLastName() : "");
            data.put("passwordHash", response.getPasswordHash() != null ? response.getPasswordHash() : "");
            data.put("expirationDateTime", response.getExpirationDateTime() != null
                    ? response.getExpirationDateTime().toString() : "");

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("authenticating user", e);
        }
    }

    public EfmResponse<Map<String, Object>> getUser(GetUserRequest request) {
        try {
            log.info("Getting user: {}", request.getUserID());

            GetUserRequestType soapRequest = new GetUserRequestType();
            soapRequest.setUserID(request.getUserID());

            GetUserResponseType response = efmUserServiceClient.getUser(soapRequest);

            if (isError(response.getError())) {
                return EfmResponse.error(
                        response.getError().getErrorCode(),
                        response.getError().getErrorText()
                );
            }

            Map<String, Object> data = new HashMap<>();
            if (response.getUser() != null) {
                UserType user = response.getUser();
                data.put("userID", user.getUserID() != null ? user.getUserID() : "");
                data.put("firmID", user.getFirmID() != null ? user.getFirmID() : "");
                data.put("email", user.getEmail() != null ? user.getEmail() : "");
                data.put("firstName", user.getFirstName() != null ? user.getFirstName() : "");
                data.put("middleName", user.getMiddleName() != null ? user.getMiddleName() : "");
                data.put("lastName", user.getLastName() != null ? user.getLastName() : "");
                data.put("isActive", user.isIsActive());
                data.put("isApproved", user.isIsApproved());
            }

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("getting user", e);
        }
    }

    public EfmResponse<Map<String, Object>> changePassword(ChangePasswordRequest request) {
        try {
            log.info("Changing password for user: {}", request.getEmail());

            ChangePasswordRequestType soapRequest = new ChangePasswordRequestType();
            soapRequest.setOldPassword(request.getOldPassword());
            soapRequest.setNewPassword(request.getNewPassword());

            ChangePasswordResponseType response = efmUserServiceClient.changePassword(soapRequest);

            if (isError(response.getError())) {
                return EfmResponse.error(
                        response.getError().getErrorCode(),
                        response.getError().getErrorText()
                );
            }

            Map<String, Object> data = new HashMap<>();
            data.put("message", "Password changed successfully");
            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("changing password", e);
        }
    }

    public EfmResponse<Map<String, Object>> resetPassword(ResetPasswordRequest request) {
        try {
            log.info("Resetting password for user: {}", request.getEmail());

            ResetPasswordRequestType soapRequest = new ResetPasswordRequestType();
            soapRequest.setEmail(request.getEmail());
            soapRequest.setPasswordAnswer(request.getPasswordAnswer());

            ResetPasswordResponseType response = efmUserServiceClient.resetPassword(soapRequest);

            if (isError(response.getError())) {
                return EfmResponse.error(
                        response.getError().getErrorCode(),
                        response.getError().getErrorText()
                );
            }

            Map<String, Object> data = new HashMap<>();
            data.put("message", "Password reset successfully");
            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("resetting password", e);
        }
    }

    public EfmResponse<Map<String, Object>> getPasswordQuestion(GetPasswordQuestionRequest request) {
        try {
            log.info("Getting password question for user: {}", request.getEmail());

            GetPasswordQuestionRequestType soapRequest = new GetPasswordQuestionRequestType();
            soapRequest.setEmail(request.getEmail());

            PasswordQuestionResponseType response = efmUserServiceClient.getPasswordQuestion(soapRequest);

            if (isError(response.getError())) {
                return EfmResponse.error(
                        response.getError().getErrorCode(),
                        response.getError().getErrorText()
                );
            }

            Map<String, Object> data = new HashMap<>();
            data.put("passwordQuestion", response.getPasswordQuestion() != null
                    ? response.getPasswordQuestion() : "");

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("getting password question", e);
        }
    }

    public EfmResponse<Map<String, Object>> updateUser(UpdateUserRequest request) {
        try {
            log.info("Updating user: {}", request.getUserID());

            UserType user = new UserType();
            user.setUserID(request.getUserID());
            user.setFirstName(request.getFirstName());
            user.setMiddleName(request.getMiddleName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());

            UpdateUserRequestType soapRequest = new UpdateUserRequestType();
            soapRequest.setUser(user);

            UpdateUserResponseType response = efmUserServiceClient.updateUser(soapRequest);

            if (isError(response.getError())) {
                return EfmResponse.error(
                        response.getError().getErrorCode(),
                        response.getError().getErrorText()
                );
            }

            Map<String, Object> data = new HashMap<>();
            data.put("message", "User updated successfully");
            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("updating user", e);
        }
    }

    public EfmResponse<Map<String, Object>> getNotificationPreferences(GetNotificationPreferencesRequest request) {
        try {
            log.info("Getting notification preferences for user: {}", request.getUserID());

            NotificationPreferencesResponseType response = efmUserServiceClient.getNotificationPreferences();

            if (isError(response.getError())) {
                return EfmResponse.error(
                        response.getError().getErrorCode(),
                        response.getError().getErrorText()
                );
            }

            Map<String, Object> data = new HashMap<>();
            data.put("notifications", response.getNotification());

            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("getting notification preferences", e);
        }
    }

    public EfmResponse<Map<String, Object>> updateNotificationPreferences(UpdateNotificationPreferencesRequest request) {
        try {
            log.info("Updating notification preferences for user: {}", request.getUserID());

            UpdateNotificationPreferencesRequestType soapRequest = new UpdateNotificationPreferencesRequestType();

            BaseResponseType response = efmUserServiceClient.updateNotificationPreferences(soapRequest);

            if (isError(response.getError())) {
                return EfmResponse.error(
                        response.getError().getErrorCode(),
                        response.getError().getErrorText()
                );
            }

            Map<String, Object> data = new HashMap<>();
            data.put("message", "Notification preferences updated successfully");
            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("updating notification preferences", e);
        }
    }

    public EfmResponse<Map<String, Object>> selfResendActivationEmail(SelfResendActivationEmailRequest request) {
        try {
            log.info("Resending activation email for: {}", request.getEmail());

            SelfResendActivationEmailRequestType soapRequest = new SelfResendActivationEmailRequestType();
            soapRequest.setEmail(request.getEmail());

            BaseResponseType response = efmUserServiceClient.selfResendActivationEmail(soapRequest);

            if (isError(response.getError())) {
                return EfmResponse.error(
                        response.getError().getErrorCode(),
                        response.getError().getErrorText()
                );
            }

            Map<String, Object> data = new HashMap<>();
            data.put("message", "Activation email sent successfully");
            return EfmResponse.success(data);
        } catch (Exception e) {
            return handleException("resending activation email", e);
        }
    }

    /**
     * Checks if the error response indicates an actual error.
     * Error code "0" means success (No Error), so we only treat non-zero codes as errors.
     */
    private boolean isError(tyler.efm.services.schema.common.ErrorType error) {
        if (error == null || error.getErrorCode() == null) {
            return false;
        }
        String errorCode = error.getErrorCode().trim();
        return !errorCode.isEmpty() && !"0".equals(errorCode);
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
                "Unable to connect to EFM service. Please try again later.");
        }

        log.error("Error {}: {}", operation, e.getMessage(), e);
        return EfmResponse.error("SYSTEM_ERROR", e.getMessage());
    }

    private String getHttpErrorMessage(int statusCode, String operation) {
        return switch (statusCode) {
            case 503 -> "EFM service is temporarily unavailable. Please try again later.";
            case 502 -> "EFM service gateway error. Please try again later.";
            case 504 -> "EFM service request timed out. Please try again later.";
            case 500 -> "EFM service encountered an internal error.";
            case 401 -> "Authentication failed with EFM service.";
            case 403 -> "Access denied to EFM service.";
            case 404 -> "EFM service endpoint not found.";
            default -> "EFM service returned HTTP " + statusCode + " while " + operation;
        };
    }
}
