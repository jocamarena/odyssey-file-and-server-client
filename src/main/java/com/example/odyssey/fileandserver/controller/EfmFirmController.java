package com.example.odyssey.fileandserver.controller;

import com.example.odyssey.fileandserver.dto.EfmResponse;
import com.example.odyssey.fileandserver.service.EfmFirmServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tyler.efm.services.schema.adduserrolerequest.AddUserRoleRequestType;
import tyler.efm.services.schema.attachservicecontactrequest.AttachServiceContactRequestType;
import tyler.efm.services.schema.createattorneyrequest.CreateAttorneyRequestType;
import tyler.efm.services.schema.createpaymentaccountrequest.CreatePaymentAccountRequestType;
import tyler.efm.services.schema.createservicecontactrequest.CreateServiceContactRequestType;
import tyler.efm.services.schema.detachservicecontactrequest.DetachServiceContactRequestType;
import tyler.efm.services.schema.getattorneyrequest.GetAttorneyRequestType;
import tyler.efm.services.schema.getpaymentaccountlistrequest.GetPaymentAccountListRequestType;
import tyler.efm.services.schema.getpaymentaccountrequest.GetPaymentAccountRequestType;
import tyler.efm.services.schema.getpubliclistrequest.GetPublicListRequestType;
import tyler.efm.services.schema.getservicecontactrequest.GetServiceContactRequestType;
import tyler.efm.services.schema.getuserlistrequest.GetUserListRequest;
import tyler.efm.services.schema.getuserrequest.GetUserRequestType;
import tyler.efm.services.schema.registrationrequest.RegistrationRequestType;
import tyler.efm.services.schema.removeattorneyrequest.RemoveAttorneyRequestType;
import tyler.efm.services.schema.removepaymentaccountrequest.RemovePaymentAccountRequestType;
import tyler.efm.services.schema.removeservicecontactrequest.RemoveServiceContactRequestType;
import tyler.efm.services.schema.removeuserrequest.RemoveUserRequestType;
import tyler.efm.services.schema.removeuserrolerequest.RemoveUserRoleRequestType;
import tyler.efm.services.schema.replaceservicecontactrequest.ReplaceServiceContactRequestType;
import tyler.efm.services.schema.resendactivationemailrequest.ResendActivationEmailRequestType;
import tyler.efm.services.schema.resetuserpasswordrequest.ResetUserPasswordRequestType;
import tyler.efm.services.schema.updateattorneyrequest.UpdateAttorneyRequestType;
import tyler.efm.services.schema.updatefirmrequest.UpdateFirmRequestType;
import tyler.efm.services.schema.updatepaymentaccountrequest.UpdatePaymentAccountRequestType;
import tyler.efm.services.schema.updateservicecontactrequest.UpdateServiceContactRequestType;
import tyler.efm.services.schema.updateuserrequest.UpdateUserRequestType;

@Slf4j
@RestController
@RequestMapping("/api/efm/firm")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "efm.enabled", havingValue = "true", matchIfMissing = true)
public class EfmFirmController {

    private final EfmFirmServiceClient efmFirmServiceClient;

    // ==================== User Endpoints ====================

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequestType request) {
        log.info("REST request to register user");
        return ResponseEntity.ok(efmFirmServiceClient.registerUser(request));
    }

    @PostMapping("/users/list")
    public ResponseEntity<?> getUserList(@RequestBody GetUserListRequest request) {
        log.info("REST request to get user list");
        return ResponseEntity.ok(efmFirmServiceClient.getUserList(request));
    }

    @PostMapping("/users/get")
    public ResponseEntity<?> getUser(@RequestBody GetUserRequestType request) {
        log.info("REST request to get user: {}", request.getUserID());
        return ResponseEntity.ok(efmFirmServiceClient.getUser(request));
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequestType request) {
        log.info("REST request to update user");
        return ResponseEntity.ok(efmFirmServiceClient.updateUser(request));
    }

    @PostMapping("/users/remove")
    public ResponseEntity<?> removeUser(@RequestBody RemoveUserRequestType request) {
        log.info("REST request to remove user: {}", request.getUserID());
        return ResponseEntity.ok(efmFirmServiceClient.removeUser(request));
    }

    @PostMapping("/users/roles/add")
    public ResponseEntity<?> addUserRole(@RequestBody AddUserRoleRequestType request) {
        log.info("REST request to add user role");
        return ResponseEntity.ok(efmFirmServiceClient.addUserRole(request));
    }

    @PostMapping("/users/roles/remove")
    public ResponseEntity<?> removeUserRole(@RequestBody RemoveUserRoleRequestType request) {
        log.info("REST request to remove user role");
        return ResponseEntity.ok(efmFirmServiceClient.removeUserRole(request));
    }

    @PostMapping("/users/reset-password")
    public ResponseEntity<?> resetUserPassword(@RequestBody ResetUserPasswordRequestType request) {
        log.info("REST request to reset user password");
        return ResponseEntity.ok(efmFirmServiceClient.resetUserPassword(request));
    }

    @PostMapping("/users/resend-activation-email")
    public ResponseEntity<?> resendActivationEmail(@RequestBody ResendActivationEmailRequestType request) {
        log.info("REST request to resend activation email");
        return ResponseEntity.ok(efmFirmServiceClient.resendActivationEmail(request));
    }

    // ==================== Firm Endpoints ====================

    @GetMapping
    public ResponseEntity<?> getFirm() {
        log.info("REST request to get firm");
        return ResponseEntity.ok(efmFirmServiceClient.getFirm());
    }

    @PutMapping
    public ResponseEntity<?> updateFirm(@RequestBody UpdateFirmRequestType request) {
        log.info("REST request to update firm");
        return ResponseEntity.ok(efmFirmServiceClient.updateFirm(request));
    }

    // ==================== Attorney Endpoints ====================

    @GetMapping("/attorneys")
    public ResponseEntity<?> getAttorneyList() {
        log.info("REST request to get attorney list");
        return ResponseEntity.ok(efmFirmServiceClient.getAttorneyList());
    }

    @PostMapping("/attorneys/get")
    public ResponseEntity<?> getAttorney(@RequestBody GetAttorneyRequestType request) {
        log.info("REST request to get attorney: {}", request.getAttorneyID());
        return ResponseEntity.ok(efmFirmServiceClient.getAttorney(request));
    }

    @PostMapping("/attorneys")
    public ResponseEntity<?> createAttorney(@RequestBody CreateAttorneyRequestType request) {
        log.info("REST request to create attorney");
        return ResponseEntity.ok(efmFirmServiceClient.createAttorney(request));
    }

    @PutMapping("/attorneys")
    public ResponseEntity<?> updateAttorney(@RequestBody UpdateAttorneyRequestType request) {
        log.info("REST request to update attorney");
        return ResponseEntity.ok(efmFirmServiceClient.updateAttorney(request));
    }

    @PostMapping("/attorneys/remove")
    public ResponseEntity<?> removeAttorney(@RequestBody RemoveAttorneyRequestType request) {
        log.info("REST request to remove attorney: {}", request.getAttorneyID());
        return ResponseEntity.ok(efmFirmServiceClient.removeAttorney(request));
    }

    // ==================== Payment Account Endpoints ====================

    @GetMapping("/payment-accounts/types")
    public ResponseEntity<?> getPaymentAccountTypeList() {
        log.info("REST request to get payment account type list");
        return ResponseEntity.ok(efmFirmServiceClient.getPaymentAccountTypeList());
    }

    @PostMapping("/payment-accounts/list")
    public ResponseEntity<?> getPaymentAccountList(@RequestBody GetPaymentAccountListRequestType request) {
        log.info("REST request to get payment account list");
        return ResponseEntity.ok(efmFirmServiceClient.getPaymentAccountList(request));
    }

    @PostMapping("/payment-accounts/get")
    public ResponseEntity<?> getPaymentAccount(@RequestBody GetPaymentAccountRequestType request) {
        log.info("REST request to get payment account: {}", request.getPaymentAccountID());
        return ResponseEntity.ok(efmFirmServiceClient.getPaymentAccount(request));
    }

    @PostMapping("/payment-accounts")
    public ResponseEntity<?> createPaymentAccount(@RequestBody CreatePaymentAccountRequestType request) {
        log.info("REST request to create payment account");
        return ResponseEntity.ok(efmFirmServiceClient.createPaymentAccount(request));
    }

    @PostMapping("/payment-accounts/inactive")
    public ResponseEntity<?> createInactivePaymentAccount(@RequestBody CreatePaymentAccountRequestType request) {
        log.info("REST request to create inactive payment account");
        return ResponseEntity.ok(efmFirmServiceClient.createInactivePaymentAccount(request));
    }

    @PutMapping("/payment-accounts")
    public ResponseEntity<?> updatePaymentAccount(@RequestBody UpdatePaymentAccountRequestType request) {
        log.info("REST request to update payment account");
        return ResponseEntity.ok(efmFirmServiceClient.updatePaymentAccount(request));
    }

    @PostMapping("/payment-accounts/remove")
    public ResponseEntity<?> removePaymentAccount(@RequestBody RemovePaymentAccountRequestType request) {
        log.info("REST request to remove payment account: {}", request.getPaymentAccountID());
        return ResponseEntity.ok(efmFirmServiceClient.removePaymentAccount(request));
    }

    // ==================== Global Payment Account Endpoints ====================

    @GetMapping("/global-payment-accounts")
    public ResponseEntity<?> getGlobalPaymentAccountList() {
        log.info("REST request to get global payment account list");
        return ResponseEntity.ok(efmFirmServiceClient.getGlobalPaymentAccountList());
    }

    @PostMapping("/global-payment-accounts/get")
    public ResponseEntity<?> getGlobalPaymentAccount(@RequestBody GetPaymentAccountRequestType request) {
        log.info("REST request to get global payment account: {}", request.getPaymentAccountID());
        return ResponseEntity.ok(efmFirmServiceClient.getGlobalPaymentAccount(request));
    }

    @PostMapping("/global-payment-accounts")
    public ResponseEntity<?> createGlobalPaymentAccount(@RequestBody CreatePaymentAccountRequestType request) {
        log.info("REST request to create global payment account");
        return ResponseEntity.ok(efmFirmServiceClient.createGlobalPaymentAccount(request));
    }

    @PutMapping("/global-payment-accounts")
    public ResponseEntity<?> updateGlobalPaymentAccount(@RequestBody UpdatePaymentAccountRequestType request) {
        log.info("REST request to update global payment account");
        return ResponseEntity.ok(efmFirmServiceClient.updateGlobalPaymentAccount(request));
    }

    @PostMapping("/global-payment-accounts/remove")
    public ResponseEntity<?> removeGlobalPaymentAccount(@RequestBody RemovePaymentAccountRequestType request) {
        log.info("REST request to remove global payment account: {}", request.getPaymentAccountID());
        return ResponseEntity.ok(efmFirmServiceClient.removeGlobalPaymentAccount(request));
    }

    // ==================== Service Contact Endpoints ====================

    @GetMapping("/service-contacts")
    public ResponseEntity<?> getServiceContactList() {
        log.info("REST request to get service contact list");
        return ResponseEntity.ok(efmFirmServiceClient.getServiceContactList());
    }

    @PostMapping("/service-contacts/get")
    public ResponseEntity<?> getServiceContact(@RequestBody GetServiceContactRequestType request) {
        log.info("REST request to get service contact: {}", request.getServiceContactID());
        return ResponseEntity.ok(efmFirmServiceClient.getServiceContact(request));
    }

    @PostMapping("/service-contacts")
    public ResponseEntity<?> createServiceContact(@RequestBody CreateServiceContactRequestType request) {
        log.info("REST request to create service contact");
        return ResponseEntity.ok(efmFirmServiceClient.createServiceContact(request));
    }

    @PutMapping("/service-contacts")
    public ResponseEntity<?> updateServiceContact(@RequestBody UpdateServiceContactRequestType request) {
        log.info("REST request to update service contact");
        return ResponseEntity.ok(efmFirmServiceClient.updateServiceContact(request));
    }

    @PostMapping("/service-contacts/remove")
    public ResponseEntity<?> removeServiceContact(@RequestBody RemoveServiceContactRequestType request) {
        log.info("REST request to remove service contact: {}", request.getServiceContactID());
        return ResponseEntity.ok(efmFirmServiceClient.removeServiceContact(request));
    }

    @PostMapping("/service-contacts/attach")
    public ResponseEntity<?> attachServiceContact(@RequestBody AttachServiceContactRequestType request) {
        log.info("REST request to attach service contact");
        return ResponseEntity.ok(efmFirmServiceClient.attachServiceContact(request));
    }

    @PostMapping("/service-contacts/detach")
    public ResponseEntity<?> detachServiceContact(@RequestBody DetachServiceContactRequestType request) {
        log.info("REST request to detach service contact");
        return ResponseEntity.ok(efmFirmServiceClient.detachServiceContact(request));
    }

    @PostMapping("/service-contacts/replace")
    public ResponseEntity<?> replaceServiceContact(@RequestBody ReplaceServiceContactRequestType request) {
        log.info("REST request to replace service contact");
        return ResponseEntity.ok(efmFirmServiceClient.replaceServiceContact(request));
    }

    @PostMapping("/public-list")
    public ResponseEntity<?> getPublicList(@RequestBody GetPublicListRequestType request) {
        log.info("REST request to get public list");
        return ResponseEntity.ok(efmFirmServiceClient.getPublicList(request));
    }

    // ==================== Notification Endpoints ====================

    @GetMapping("/notification-preferences")
    public ResponseEntity<?> getNotificationPreferencesList() {
        log.info("REST request to get notification preferences list");
        return ResponseEntity.ok(efmFirmServiceClient.getNotificationPreferencesList());
    }
}
