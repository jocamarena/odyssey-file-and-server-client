package com.example.odyssey.fileandserver.service;

import com.example.odyssey.fileandserver.dto.EfmResponse;
import jakarta.xml.ws.WebServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.transport.http.HTTPException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tyler.efm.services.IEfmFirmService;
import tyler.efm.services.schema.adduserrolerequest.AddUserRoleRequestType;
import tyler.efm.services.schema.attachservicecontactrequest.AttachServiceContactRequestType;
import tyler.efm.services.schema.baseresponse.BaseResponseType;
import tyler.efm.services.schema.createattorneyrequest.CreateAttorneyRequestType;
import tyler.efm.services.schema.createattorneyresponse.CreateAttorneyResponseType;
import tyler.efm.services.schema.createpaymentaccountrequest.CreatePaymentAccountRequestType;
import tyler.efm.services.schema.createpaymentaccountresponse.CreatePaymentAccountResponseType;
import tyler.efm.services.schema.createservicecontactrequest.CreateServiceContactRequestType;
import tyler.efm.services.schema.createservicecontactresponse.CreateServiceContactResponseType;
import tyler.efm.services.schema.detachservicecontactrequest.DetachServiceContactRequestType;
import tyler.efm.services.schema.getattorneyrequest.GetAttorneyRequestType;
import tyler.efm.services.schema.getattorneyresponse.GetAttorneyResponseType;
import tyler.efm.services.schema.getfirmresponse.GetFirmResponseType;
import tyler.efm.services.schema.getpaymentaccountlistrequest.GetPaymentAccountListRequestType;
import tyler.efm.services.schema.getpaymentaccountrequest.GetPaymentAccountRequestType;
import tyler.efm.services.schema.getpaymentaccountresponse.GetPaymentAccountResponseType;
import tyler.efm.services.schema.getpubliclistrequest.GetPublicListRequestType;
import tyler.efm.services.schema.getservicecontactrequest.GetServiceContactRequestType;
import tyler.efm.services.schema.getservicecontactresponse.GetServiceContactResponseType;
import tyler.efm.services.schema.getuserlistrequest.GetUserListRequest;
import tyler.efm.services.schema.getuserrequest.GetUserRequestType;
import tyler.efm.services.schema.getuserresponse.GetUserResponseType;
import tyler.efm.services.schema.attorneylistresponse.AttorneyListResponseType;
import tyler.efm.services.schema.notificationpreferenceslistresponse.NotificationPreferencesListResponseType;
import tyler.efm.services.schema.paymentaccountlistresponse.PaymentAccountListResponseType;
import tyler.efm.services.schema.paymentaccounttypelistresponse.PaymentAccountTypeListResponseType;
import tyler.efm.services.schema.registrationrequest.RegistrationRequestType;
import tyler.efm.services.schema.registrationresponse.RegistrationResponseType;
import tyler.efm.services.schema.removeattorneyrequest.RemoveAttorneyRequestType;
import tyler.efm.services.schema.removepaymentaccountrequest.RemovePaymentAccountRequestType;
import tyler.efm.services.schema.removeservicecontactrequest.RemoveServiceContactRequestType;
import tyler.efm.services.schema.removeuserrequest.RemoveUserRequestType;
import tyler.efm.services.schema.removeuserrolerequest.RemoveUserRoleRequestType;
import tyler.efm.services.schema.replaceservicecontactrequest.ReplaceServiceContactRequestType;
import tyler.efm.services.schema.resendactivationemailrequest.ResendActivationEmailRequestType;
import tyler.efm.services.schema.resetpasswordresponse.ResetPasswordResponseType;
import tyler.efm.services.schema.resetuserpasswordrequest.ResetUserPasswordRequestType;
import tyler.efm.services.schema.servicecontactlistresponse.ServiceContactListResponseType;
import tyler.efm.services.schema.updateattorneyrequest.UpdateAttorneyRequestType;
import tyler.efm.services.schema.updateattorneyresponse.UpdateAttorneyResponseType;
import tyler.efm.services.schema.updatefirmrequest.UpdateFirmRequestType;
import tyler.efm.services.schema.updatepaymentaccountrequest.UpdatePaymentAccountRequestType;
import tyler.efm.services.schema.updatepaymentaccountresponse.UpdatePaymentAccountResponseType;
import tyler.efm.services.schema.updateservicecontactrequest.UpdateServiceContactRequestType;
import tyler.efm.services.schema.updateservicecontactresponse.UpdateServiceContactResponseType;
import tyler.efm.services.schema.updateuserrequest.UpdateUserRequestType;
import tyler.efm.services.schema.updateuserresponse.UpdateUserResponseType;
import tyler.efm.services.schema.userlistresponse.UserListResponseType;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "efm.enabled", havingValue = "true", matchIfMissing = true)
public class EfmFirmServiceClient {

    @Lazy
    private final IEfmFirmService efmFirmServiceSoapClient;

    // ==================== User Operations ====================

    public EfmResponse<RegistrationResponseType> registerUser(RegistrationRequestType request) {
        try {
            log.info("Registering user");
            RegistrationResponseType response = efmFirmServiceSoapClient.registerUser(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("registering user", e);
        }
    }

    public EfmResponse<UserListResponseType> getUserList(GetUserListRequest request) {
        try {
            log.info("Getting user list");
            UserListResponseType response = efmFirmServiceSoapClient.getUserList(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting user list", e);
        }
    }

    public EfmResponse<GetUserResponseType> getUser(GetUserRequestType request) {
        try {
            log.info("Getting user");
            GetUserResponseType response = efmFirmServiceSoapClient.getUser(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting user", e);
        }
    }

    public EfmResponse<UpdateUserResponseType> updateUser(UpdateUserRequestType request) {
        try {
            log.info("Updating user");
            UpdateUserResponseType response = efmFirmServiceSoapClient.updateUser(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("updating user", e);
        }
    }

    public EfmResponse<BaseResponseType> removeUser(RemoveUserRequestType request) {
        try {
            log.info("Removing user");
            BaseResponseType response = efmFirmServiceSoapClient.removeUser(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("removing user", e);
        }
    }

    public EfmResponse<BaseResponseType> addUserRole(AddUserRoleRequestType request) {
        try {
            log.info("Adding user role");
            BaseResponseType response = efmFirmServiceSoapClient.addUserRole(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("adding user role", e);
        }
    }

    public EfmResponse<BaseResponseType> removeUserRole(RemoveUserRoleRequestType request) {
        try {
            log.info("Removing user role");
            BaseResponseType response = efmFirmServiceSoapClient.removeUserRole(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("removing user role", e);
        }
    }

    public EfmResponse<ResetPasswordResponseType> resetUserPassword(ResetUserPasswordRequestType request) {
        try {
            log.info("Resetting user password");
            ResetPasswordResponseType response = efmFirmServiceSoapClient.resetUserPassword(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("resetting user password", e);
        }
    }

    public EfmResponse<BaseResponseType> resendActivationEmail(ResendActivationEmailRequestType request) {
        try {
            log.info("Resending activation email");
            BaseResponseType response = efmFirmServiceSoapClient.resendActivationEmail(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("resending activation email", e);
        }
    }

    // ==================== Firm Operations ====================

    public EfmResponse<GetFirmResponseType> getFirm() {
        try {
            log.info("Getting firm");
            GetFirmResponseType response = efmFirmServiceSoapClient.getFirm();
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting firm", e);
        }
    }

    public EfmResponse<BaseResponseType> updateFirm(UpdateFirmRequestType request) {
        try {
            log.info("Updating firm");
            BaseResponseType response = efmFirmServiceSoapClient.updateFirm(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("updating firm", e);
        }
    }

    // ==================== Attorney Operations ====================

    public EfmResponse<AttorneyListResponseType> getAttorneyList() {
        try {
            log.info("Getting attorney list");
            AttorneyListResponseType response = efmFirmServiceSoapClient.getAttorneyList();
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting attorney list", e);
        }
    }

    public EfmResponse<GetAttorneyResponseType> getAttorney(GetAttorneyRequestType request) {
        try {
            log.info("Getting attorney");
            GetAttorneyResponseType response = efmFirmServiceSoapClient.getAttorney(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting attorney", e);
        }
    }

    public EfmResponse<CreateAttorneyResponseType> createAttorney(CreateAttorneyRequestType request) {
        try {
            log.info("Creating attorney");
            CreateAttorneyResponseType response = efmFirmServiceSoapClient.createAttorney(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("creating attorney", e);
        }
    }

    public EfmResponse<UpdateAttorneyResponseType> updateAttorney(UpdateAttorneyRequestType request) {
        try {
            log.info("Updating attorney");
            UpdateAttorneyResponseType response = efmFirmServiceSoapClient.updateAttorney(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("updating attorney", e);
        }
    }

    public EfmResponse<BaseResponseType> removeAttorney(RemoveAttorneyRequestType request) {
        try {
            log.info("Removing attorney");
            BaseResponseType response = efmFirmServiceSoapClient.removeAttorney(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("removing attorney", e);
        }
    }

    // ==================== Payment Account Operations ====================

    public EfmResponse<PaymentAccountTypeListResponseType> getPaymentAccountTypeList() {
        try {
            log.info("Getting payment account type list");
            PaymentAccountTypeListResponseType response = efmFirmServiceSoapClient.getPaymentAccountTypeList();
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting payment account type list", e);
        }
    }

    public EfmResponse<PaymentAccountListResponseType> getPaymentAccountList(GetPaymentAccountListRequestType request) {
        try {
            log.info("Getting payment account list");
            PaymentAccountListResponseType response = efmFirmServiceSoapClient.getPaymentAccountList(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting payment account list", e);
        }
    }

    public EfmResponse<GetPaymentAccountResponseType> getPaymentAccount(GetPaymentAccountRequestType request) {
        try {
            log.info("Getting payment account");
            GetPaymentAccountResponseType response = efmFirmServiceSoapClient.getPaymentAccount(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting payment account", e);
        }
    }

    public EfmResponse<CreatePaymentAccountResponseType> createPaymentAccount(CreatePaymentAccountRequestType request) {
        try {
            log.info("Creating payment account");
            CreatePaymentAccountResponseType response = efmFirmServiceSoapClient.createPaymentAccount(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("creating payment account", e);
        }
    }

    public EfmResponse<CreatePaymentAccountResponseType> createInactivePaymentAccount(CreatePaymentAccountRequestType request) {
        try {
            log.info("Creating inactive payment account");
            CreatePaymentAccountResponseType response = efmFirmServiceSoapClient.createInactivePaymentAccount(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("creating inactive payment account", e);
        }
    }

    public EfmResponse<UpdatePaymentAccountResponseType> updatePaymentAccount(UpdatePaymentAccountRequestType request) {
        try {
            log.info("Updating payment account");
            UpdatePaymentAccountResponseType response = efmFirmServiceSoapClient.updatePaymentAccount(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("updating payment account", e);
        }
    }

    public EfmResponse<BaseResponseType> removePaymentAccount(RemovePaymentAccountRequestType request) {
        try {
            log.info("Removing payment account");
            BaseResponseType response = efmFirmServiceSoapClient.removePaymentAccount(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("removing payment account", e);
        }
    }

    // ==================== Global Payment Account Operations ====================

    public EfmResponse<PaymentAccountListResponseType> getGlobalPaymentAccountList() {
        try {
            log.info("Getting global payment account list");
            PaymentAccountListResponseType response = efmFirmServiceSoapClient.getGlobalPaymentAccountList();
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting global payment account list", e);
        }
    }

    public EfmResponse<GetPaymentAccountResponseType> getGlobalPaymentAccount(GetPaymentAccountRequestType request) {
        try {
            log.info("Getting global payment account");
            GetPaymentAccountResponseType response = efmFirmServiceSoapClient.getGlobalPaymentAccount(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting global payment account", e);
        }
    }

    public EfmResponse<CreatePaymentAccountResponseType> createGlobalPaymentAccount(CreatePaymentAccountRequestType request) {
        try {
            log.info("Creating global payment account");
            CreatePaymentAccountResponseType response = efmFirmServiceSoapClient.createGlobalPaymentAccount(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("creating global payment account", e);
        }
    }

    public EfmResponse<UpdatePaymentAccountResponseType> updateGlobalPaymentAccount(UpdatePaymentAccountRequestType request) {
        try {
            log.info("Updating global payment account");
            UpdatePaymentAccountResponseType response = efmFirmServiceSoapClient.updateGlobalPaymentAccount(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("updating global payment account", e);
        }
    }

    public EfmResponse<BaseResponseType> removeGlobalPaymentAccount(RemovePaymentAccountRequestType request) {
        try {
            log.info("Removing global payment account");
            BaseResponseType response = efmFirmServiceSoapClient.removeGlobalPaymentAccount(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("removing global payment account", e);
        }
    }

    // ==================== Service Contact Operations ====================

    public EfmResponse<ServiceContactListResponseType> getServiceContactList() {
        try {
            log.info("Getting service contact list");
            ServiceContactListResponseType response = efmFirmServiceSoapClient.getServiceContactList();
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting service contact list", e);
        }
    }

    public EfmResponse<GetServiceContactResponseType> getServiceContact(GetServiceContactRequestType request) {
        try {
            log.info("Getting service contact");
            GetServiceContactResponseType response = efmFirmServiceSoapClient.getServiceContact(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting service contact", e);
        }
    }

    public EfmResponse<CreateServiceContactResponseType> createServiceContact(CreateServiceContactRequestType request) {
        try {
            log.info("Creating service contact");
            CreateServiceContactResponseType response = efmFirmServiceSoapClient.createServiceContact(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("creating service contact", e);
        }
    }

    public EfmResponse<UpdateServiceContactResponseType> updateServiceContact(UpdateServiceContactRequestType request) {
        try {
            log.info("Updating service contact");
            UpdateServiceContactResponseType response = efmFirmServiceSoapClient.updateServiceContact(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("updating service contact", e);
        }
    }

    public EfmResponse<BaseResponseType> removeServiceContact(RemoveServiceContactRequestType request) {
        try {
            log.info("Removing service contact");
            BaseResponseType response = efmFirmServiceSoapClient.removeServiceContact(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("removing service contact", e);
        }
    }

    public EfmResponse<BaseResponseType> attachServiceContact(AttachServiceContactRequestType request) {
        try {
            log.info("Attaching service contact");
            BaseResponseType response = efmFirmServiceSoapClient.attachServiceContact(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("attaching service contact", e);
        }
    }

    public EfmResponse<BaseResponseType> detachServiceContact(DetachServiceContactRequestType request) {
        try {
            log.info("Detaching service contact");
            BaseResponseType response = efmFirmServiceSoapClient.detachServiceContact(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("detaching service contact", e);
        }
    }

    public EfmResponse<BaseResponseType> replaceServiceContact(ReplaceServiceContactRequestType request) {
        try {
            log.info("Replacing service contact");
            BaseResponseType response = efmFirmServiceSoapClient.replaceServiceContact(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("replacing service contact", e);
        }
    }

    public EfmResponse<ServiceContactListResponseType> getPublicList(GetPublicListRequestType request) {
        try {
            log.info("Getting public list");
            ServiceContactListResponseType response = efmFirmServiceSoapClient.getPublicList(request);
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting public list", e);
        }
    }

    // ==================== Notification Operations ====================

    public EfmResponse<NotificationPreferencesListResponseType> getNotificationPreferencesList() {
        try {
            log.info("Getting notification preferences list");
            NotificationPreferencesListResponseType response = efmFirmServiceSoapClient.getNotificationPreferencesList();
            return buildResponse(response, response.getError());
        } catch (Exception e) {
            return handleException("getting notification preferences list", e);
        }
    }

    // ==================== Helper Methods ====================

    private <T> EfmResponse<T> buildResponse(T data, tyler.efm.services.schema.common.ErrorType error) {
        if (error != null && !"0".equals(error.getErrorCode())) {
            return EfmResponse.error(error.getErrorCode(), error.getErrorText());
        }
        return EfmResponse.success(data);
    }

    private <T> EfmResponse<T> handleException(String operation, Exception e) {
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
                "Unable to connect to EFM Firm service. Please try again later.");
        }

        log.error("Error {}: {}", operation, e.getMessage(), e);
        return EfmResponse.error("SYSTEM_ERROR", e.getMessage());
    }

    private String getHttpErrorMessage(int statusCode, String operation) {
        return switch (statusCode) {
            case 503 -> "EFM Firm service is temporarily unavailable. Please try again later.";
            case 502 -> "EFM Firm service gateway error. Please try again later.";
            case 504 -> "EFM Firm service request timed out. Please try again later.";
            case 500 -> "EFM Firm service encountered an internal error.";
            case 401 -> "Authentication failed with EFM Firm service.";
            case 403 -> "Access denied to EFM Firm service.";
            case 404 -> "EFM Firm service endpoint not found.";
            default -> "EFM Firm service returned HTTP " + statusCode + " while " + operation;
        };
    }
}
