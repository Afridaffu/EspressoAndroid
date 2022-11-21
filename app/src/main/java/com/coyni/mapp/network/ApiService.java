package com.coyni.mapp.network;

import com.coyni.mapp.model.Agreements;
import com.coyni.mapp.model.AgreementsPdf;
import com.coyni.mapp.model.BatchNow.BatchNowPaymentRequest;
import com.coyni.mapp.model.BatchNow.BatchNowRequest;
import com.coyni.mapp.model.BatchNow.BatchNowResponse;
import com.coyni.mapp.model.BatchPayoutIdDetails.BatchPayoutDetailsRequest;
import com.coyni.mapp.model.BatchPayoutIdDetails.BatchPayoutIdDetailsResponse;
import com.coyni.mapp.model.BeneficialOwners.BOIdResp;
import com.coyni.mapp.model.BeneficialOwners.BOPatchResp;
import com.coyni.mapp.model.BeneficialOwners.BORequest;
import com.coyni.mapp.model.BeneficialOwners.BOResp;
import com.coyni.mapp.model.BeneficialOwners.BOValidateResp;
import com.coyni.mapp.model.BeneficialOwners.DeleteBOResp;
import com.coyni.mapp.model.BusinessBatchPayout.BatchPayoutListResponse;
import com.coyni.mapp.model.BusinessBatchPayout.RollingListRequest;
import com.coyni.mapp.model.ChangePassword;
import com.coyni.mapp.model.ChangePasswordRequest;
import com.coyni.mapp.model.CompanyInfo.CompanyInfoRequest;
import com.coyni.mapp.model.CompanyInfo.CompanyInfoResp;
import com.coyni.mapp.model.CompanyInfo.CompanyInfoUpdateResp;
import com.coyni.mapp.model.CompanyInfo.ContactInfoRequest;
import com.coyni.mapp.model.DBAInfo.BusinessTypeResp;
import com.coyni.mapp.model.DBAInfo.DBAInfoRequest;
import com.coyni.mapp.model.DBAInfo.DBAInfoResp;
import com.coyni.mapp.model.DBAInfo.DBAInfoUpdateResp;
import com.coyni.mapp.model.DashboardReserveList.ReserveListResponse;
import com.coyni.mapp.model.EmailRequest;
import com.coyni.mapp.model.EmptyRequest;
import com.coyni.mapp.model.SearchKeyRequest;
import com.coyni.mapp.model.SignAgreementsResp;
import com.coyni.mapp.model.UpdateSignAgree.UpdateSignAgreementsResponse;
import com.coyni.mapp.model.UpdateSignAgreementsResp;
import com.coyni.mapp.model.UpdateSignRequest;
import com.coyni.mapp.model.actionRqrd.ActionRqrdResponse;
import com.coyni.mapp.model.actionRqrd.InformationRequest;
import com.coyni.mapp.model.actionRqrd.SubmitActionRqrdResponse;
import com.coyni.mapp.model.activtity_log.ActivityLogResp;
import com.coyni.mapp.model.appupdate.AppUpdateResp;
import com.coyni.mapp.model.bank.BankDeleteResponseData;
import com.coyni.mapp.model.bank.BankResponse;
import com.coyni.mapp.model.bank.BanksResponseModel;
import com.coyni.mapp.model.bank.ManualBankRequest;
import com.coyni.mapp.model.bank.ManualBankResponse;
import com.coyni.mapp.model.biometric.BiometricRequest;
import com.coyni.mapp.model.biometric.BiometricResponse;
import com.coyni.mapp.model.biometric.BiometricTokenRequest;
import com.coyni.mapp.model.biometric.BiometricTokenResponse;
import com.coyni.mapp.model.business_activity.BusinessActivityRequest;
import com.coyni.mapp.model.business_activity.BusinessActivityResp;
import com.coyni.mapp.model.business_id_verification.BusinessTrackerResponse;
import com.coyni.mapp.model.business_id_verification.CancelApplicationResponse;
import com.coyni.mapp.model.businesswallet.BusinessWalletResponse;
import com.coyni.mapp.model.buytoken.BuyTokenRequest;
import com.coyni.mapp.model.buytoken.BuyTokenResponse;
import com.coyni.mapp.model.buytoken.CancelBuyTokenResponse;
import com.coyni.mapp.model.cards.CardDeleteResponse;
import com.coyni.mapp.model.cards.CardEditRequest;
import com.coyni.mapp.model.cards.CardEditResponse;
import com.coyni.mapp.model.cards.CardRequest;
import com.coyni.mapp.model.cards.CardResponse;
import com.coyni.mapp.model.cards.CardTypeRequest;
import com.coyni.mapp.model.cards.CardTypeResponse;
import com.coyni.mapp.model.cards.business.BusinessCardRequest;
import com.coyni.mapp.model.cards.business.BusinessCardResponse;
import com.coyni.mapp.model.check_out_transactions.CancelOrderRequest;
import com.coyni.mapp.model.check_out_transactions.CancelOrderResponse;
import com.coyni.mapp.model.check_out_transactions.OrderInfoRequest;
import com.coyni.mapp.model.check_out_transactions.OrderInfoResponse;
import com.coyni.mapp.model.check_out_transactions.OrderPayRequest;
import com.coyni.mapp.model.check_out_transactions.OrderPayResponse;
import com.coyni.mapp.model.check_out_transactions.ScanQRRequest;
import com.coyni.mapp.model.check_out_transactions.ScanQrCodeResp;
import com.coyni.mapp.model.cogent.CogentRequest;
import com.coyni.mapp.model.cogent.CogentResponse;
import com.coyni.mapp.model.coynipin.PINRegisterResponse;
import com.coyni.mapp.model.coynipin.RegisterRequest;
import com.coyni.mapp.model.coynipin.StepUpOTPResponse;
import com.coyni.mapp.model.coynipin.StepUpResponse;
import com.coyni.mapp.model.coynipin.ValidateRequest;
import com.coyni.mapp.model.coynipin.ValidateResponse;
import com.coyni.mapp.model.coyniusers.CoyniUsers;
import com.coyni.mapp.model.deviceintialize.DeviceInitializeResponse;
import com.coyni.mapp.model.featurecontrols.FeatureControlGlobalResp;
import com.coyni.mapp.model.featurecontrols.FeatureControlRespByUser;
import com.coyni.mapp.model.fee.Fees;
import com.coyni.mapp.model.forgotpassword.EmailValidateResponse;
import com.coyni.mapp.model.forgotpassword.ManagePasswordRequest;
import com.coyni.mapp.model.forgotpassword.ManagePasswordResponse;
import com.coyni.mapp.model.forgotpassword.SetPassword;
import com.coyni.mapp.model.forgotpassword.SetPasswordResponse;
import com.coyni.mapp.model.giftcard.BrandsResponse;
import com.coyni.mapp.model.identity_verification.GetIdentityResponse;
import com.coyni.mapp.model.identity_verification.IdentityAddressRequest;
import com.coyni.mapp.model.identity_verification.IdentityAddressResponse;
import com.coyni.mapp.model.identity_verification.IdentityImageResponse;
import com.coyni.mapp.model.identity_verification.LatestTransactionsRequest;
import com.coyni.mapp.model.identity_verification.LatestTxnResponse;
import com.coyni.mapp.model.identity_verification.RemoveIdentityResponse;
import com.coyni.mapp.model.login.BiometricLoginRequest;
import com.coyni.mapp.model.login.LoginRequest;
import com.coyni.mapp.model.login.LoginResponse;
import com.coyni.mapp.model.login.PasswordRequest;
import com.coyni.mapp.model.logout.LogoutResponse;
import com.coyni.mapp.model.merchant_activity.MerchantActivityRequest;
import com.coyni.mapp.model.merchant_activity.MerchantActivityResp;
import com.coyni.mapp.model.notification.Notifications;
import com.coyni.mapp.model.notification.StatusRequest;
import com.coyni.mapp.model.notification.UnReadDelResponse;
import com.coyni.mapp.model.paidorder.PaidOrderRequest;
import com.coyni.mapp.model.paidorder.PaidOrderResp;
import com.coyni.mapp.model.paymentmethods.PaymentMethodsResponse;
import com.coyni.mapp.model.payrequest.PayRequestResponse;
import com.coyni.mapp.model.payrequest.TransferPayRequest;
import com.coyni.mapp.model.preauth.PreAuthRequest;
import com.coyni.mapp.model.preauth.PreAuthResponse;
import com.coyni.mapp.model.preferences.Preferences;
import com.coyni.mapp.model.preferences.ProfilesResponse;
import com.coyni.mapp.model.preferences.UserPreference;
import com.coyni.mapp.model.profile.AddBusinessUserResponse;
import com.coyni.mapp.model.profile.DownloadDocumentResponse;
import com.coyni.mapp.model.profile.DownloadImageResponse;
import com.coyni.mapp.model.profile.DownloadUrlRequest;
import com.coyni.mapp.model.profile.ImageResponse;
import com.coyni.mapp.model.profile.Profile;
import com.coyni.mapp.model.profile.TrackerResponse;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailRequest;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailResponse;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailValidateRequest;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneRequest;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneResponse;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneValidateRequest;
import com.coyni.mapp.model.publickey.PublicKeyResponse;
import com.coyni.mapp.model.recentusers.RecentUsers;
import com.coyni.mapp.model.register.CustRegisRequest;
import com.coyni.mapp.model.register.CustRegisterResponse;
import com.coyni.mapp.model.register.EmailExistsResponse;
import com.coyni.mapp.model.register.EmailResendResponse;
import com.coyni.mapp.model.register.EmailResponse;
import com.coyni.mapp.model.register.InitCustomerRequest;
import com.coyni.mapp.model.register.InitializeCustomerResponse;
import com.coyni.mapp.model.register.OTPResendRequest;
import com.coyni.mapp.model.register.OTPValidateRequest;
import com.coyni.mapp.model.register.OTPValidateResponse;
import com.coyni.mapp.model.register.SMSResend;
import com.coyni.mapp.model.register.SMSResponse;
import com.coyni.mapp.model.register.SMSValidate;
import com.coyni.mapp.model.register.SignAgreementRequest;
import com.coyni.mapp.model.register.SignAgreementResponse;
import com.coyni.mapp.model.register.SmsRequest;
import com.coyni.mapp.model.reguser.RegUsersResponse;
import com.coyni.mapp.model.reguser.RegisteredUsersRequest;
import com.coyni.mapp.model.reserveIdDetails.DetailsRequest;
import com.coyni.mapp.model.reserveIdDetails.DetailsResponse;
import com.coyni.mapp.model.reservemanual.ManualListResponse;
import com.coyni.mapp.model.reservemanual.RollingSearchRequest;
import com.coyni.mapp.model.reserverule.RollingRuleResponse;
import com.coyni.mapp.model.retrieveemail.RetrieveEmailRequest;
import com.coyni.mapp.model.retrieveemail.RetrieveEmailResponse;
import com.coyni.mapp.model.retrieveemail.RetrieveUsersRequest;
import com.coyni.mapp.model.retrieveemail.RetrieveUsersResponse;
import com.coyni.mapp.model.signedagreements.SignedAgreementResponse;
import com.coyni.mapp.model.signin.BiometricSignIn;
import com.coyni.mapp.model.signin.InitializeResponse;
import com.coyni.mapp.model.submit.ApplicationSubmitResponseModel;
import com.coyni.mapp.model.summary.ApplicationSummaryModelResponse;
import com.coyni.mapp.model.team.TeamGetDataModel;
import com.coyni.mapp.model.team.TeamInfoAddModel;
import com.coyni.mapp.model.team.TeamListResponse;
import com.coyni.mapp.model.team.TeamRequest;
import com.coyni.mapp.model.templates.TemplateRequest;
import com.coyni.mapp.model.templates.TemplateResponse;
import com.coyni.mapp.model.transaction.RefundDataResponce;
import com.coyni.mapp.model.transaction.RefundReferenceRequest;
import com.coyni.mapp.model.transaction.TransactionDetails;
import com.coyni.mapp.model.transaction.TransactionList;
import com.coyni.mapp.model.transaction.TransactionListRequest;
import com.coyni.mapp.model.transactionlimit.TransactionLimitRequest;
import com.coyni.mapp.model.transactionlimit.TransactionLimitResponse;
import com.coyni.mapp.model.transferfee.TransferFeeRequest;
import com.coyni.mapp.model.transferfee.TransferFeeResponse;
import com.coyni.mapp.model.underwriting.ActionRequiredResponse;
import com.coyni.mapp.model.underwriting.ActionRequiredSubmitResponse;
import com.coyni.mapp.model.update_resend_otp.UpdateResendOTPResponse;
import com.coyni.mapp.model.update_resend_otp.UpdateResendRequest;
import com.coyni.mapp.model.userrequest.UserRequest;
import com.coyni.mapp.model.userrequest.UserRequestResponse;
import com.coyni.mapp.model.users.AccountLimits;
import com.coyni.mapp.model.users.User;
import com.coyni.mapp.model.users.UserData;
import com.coyni.mapp.model.users.UserPreferenceModel;
import com.coyni.mapp.model.wallet.UserDetails;
import com.coyni.mapp.model.websocket.WebSocketUrlResponse;
import com.coyni.mapp.model.withdraw.WithdrawRequest;
import com.coyni.mapp.model.withdraw.WithdrawResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/v2/user/email-otp/resend")
    Call<EmailResendResponse> emailotpresend(@Body EmailRequest emailRequest);

    @POST("api/v2/user/register/email-otp/validate")
    Call<EmailResponse> emailotp(@Body SmsRequest smsRequest);

    @POST("api/v2/user/sms-otp/resend")
    Call<SMSResponse> smsotpresend(@Body SMSResend resend);

    @POST("api/v2/user/register/phone-otp/validate")
    Call<SMSValidate> smsotp(@Body SmsRequest smsRequest);

    @POST("api/v2/user/verify/phone-otp/registration")
    Call<OTPValidateResponse> validateRegisterMobileOTP(@Body OTPValidateRequest OTPValidateRequest);


    @POST("api/v2/user/sms-otp/validate")
    Call<SMSValidate> smsotpLogin(@Body SmsRequest smsRequest);

    @POST("api/v2/user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/v2/user/signin")
    Call<BiometricSignIn> loginNew(@Body LoginRequest loginRequest);

    @POST("api/v2/user/initialize")
    Call<InitializeResponse> initialize();

    @POST("api/v2/user/logout")
    Call<LogoutResponse> logout();

    @POST("api/v2/register/newcustomer")
    Call<CustRegisterResponse> custRegister(@Body CustRegisRequest custRegisRequest);

    //2.1 Register flow
    @POST("api/v2/register/new-user")
    Call<CustRegisterResponse> registerNew(@Body CustRegisRequest custRegisRequest);


    @POST("api/v2/user/verify/email-otp/registration")
    Call<OTPValidateResponse> validateRegisterEmailOTP(@Body OTPValidateRequest OTPValidateRequest);

    @POST("api/v2/user/email-otp/validate")
    Call<EmailValidateResponse> emailotpValidate(@Body SmsRequest smsRequest);

    @PATCH("api/v2/register/set-password")
    Call<SetPasswordResponse> setpassword(@Body SetPassword setPassword);

    @POST("api/v2/register/initialize/customer")
    Call<InitializeCustomerResponse> initializeCustomer(@Body InitCustomerRequest initCustomerRequest);

    @POST("api/v2/user/forgot-email/otp/send")
    Call<RetrieveEmailResponse> retrieveEmail(@Body RetrieveEmailRequest request);

    @PATCH("api/v2/coyni-pin/validate")
    Call<ValidateResponse> validateCoyniPin(@Body ValidateRequest request);

    @GET("api/v2/profile/me/accountlimits/{userType}")
    Call<AccountLimits> meAccountLimits(@Path("userType") int userType);

    @GET("api/v2/profile/me/signedagreements")
    Call<Agreements> meAgreementsByType();

//    @PATCH("api/v2/user/change-password")
//    Call<ChangePassword> mChangePassword(@Body ChangePasswordRequest request);

    @PATCH("api/v2/user/change-password/{requestToken}")
    Call<ChangePassword> mChangePassword(@Body ChangePasswordRequest request, @Path("requestToken") String requestToken);

    @POST("api/v2/coyni-pin/register")
    Call<PINRegisterResponse> coyniPINRegister(@Body RegisterRequest request);

    @GET("api/v2/agreements/active/type")
    Call<AgreementsPdf> agreementsByType(@Query("agreementType") String agreetype);

    @PATCH("api/v2/register/newcustomer")
    Call<CustRegisterResponse> custRegisterPatch(@Body CustRegisRequest custRegisRequest, @Query("id") int id);

    @POST("api/v2/user/forgot-email/retrieve-users")
    Call<RetrieveUsersResponse> retrieveUsers(@Body RetrieveUsersRequest request, @Query("otp") String otp);

    @POST("api/v2/user/biometric")
    Call<BiometricResponse> saveBiometric(@Body BiometricRequest request);

//    @POST("api/v2/user/biometric/login")
//    Call<BiometricSignIn> biometricLogin(@Body BiometricLoginRequest request);

    @POST("api/v2/user/biometric/signin")
    Call<BiometricSignIn> biometricLogin(@Body BiometricLoginRequest request);

    @GET("api/v2/profile/me")
    Call<Profile> meProfile();

    @POST("api/v2/user/update-email/otp/send")
    Call<UpdateEmailResponse> updateEmailSendOTP(@Body UpdateEmailRequest request);

    @POST("api/v2/user/update-email/otp-validate")
    Call<UpdateEmailResponse> updateEmailValidateOTP(@Body UpdateEmailValidateRequest request);

    @PATCH("api/v2/user/set-password")
    Call<ManagePasswordResponse> setExpiryPassword(@Body ManagePasswordRequest request);

    @POST("api/v2/user/validate-email")
    Call<EmailExistsResponse> validateEmail(@Body EmailRequest emailRequest);

    @GET("api/v2/profile/payment-methods")
    Call<PaymentMethodsResponse> mePaymentMethods();

    @POST("api/v2/user/update-phone/otp/send")
    Call<UpdatePhoneResponse> updatePhoneSendOTP(@Body UpdatePhoneRequest request);

    @POST("api/v2/user/update-phone/otp/validate")
    Call<UpdatePhoneResponse> updatePhoneValidateOTP(@Body UpdatePhoneValidateRequest request);

    @Multipart
    @PATCH("api/v2/profile/me/uploadImage")
    Call<ImageResponse> updateProfile(@Part MultipartBody.Part image);

    @Multipart
    @PATCH("api/v2/business/uploadImage")
    Call<ImageResponse> updateProfileShared(@Part MultipartBody.Part image);

    @DELETE("api/v2/profile/me/removeImage")
    Call<ImageResponse> removeImage(@Query("filename") String filename);

//    @GET("api/v2/profile/me/wallets")
//    Call<WalletResponse> meWallet();

    @GET("api/v2/profile/me/preferences")
    Call<Preferences> mePreferences();

    @GET("api/v2/admin/{id}/preferences")
    Call<Preferences> mePreferencesShared(@Path("id") String id);

    @POST("api/v2/profile/me/preferences")
    Call<UserPreference> meUpdatePreferences(@Body UserPreferenceModel request);

    @POST("api/v2/business/preferences")
    Call<UserPreference> meUpdatePreferences_Shared(@Body UserPreferenceModel request);

    @POST("api/v2/admin/{id}/preferences")
    Call<UserPreference> updateTimeZoneShared(@Path("id") String id, @Body UserPreferenceModel request);

    @PATCH("api/v2/profile/me/update-address")
    Call<User> meUpdateAddress(@Body UserData request);

    @GET("api/v2/profile/me/profile-accounts")
    Call<ProfilesResponse> getProfiles();

//    @POST("api/v2/fiserv/signon")
//    Call<SignOn> meSignOn();

    @POST("api/v2/user/update/otp/resend")
    Call<UpdateResendOTPResponse> updateOtpResend(@Body UpdateResendRequest request);

    @GET("api/v2/user-requests/user-details/{walletId}")
    Call<UserDetails> getUserDetails(@Path("walletId") String walletId);

    @POST("api/v2/transactions/token/info")
    Call<TransactionDetails> getTransactionDt();

    @POST("api/v2/user/switch-account")
    Call<BiometricSignIn> getChangeAccount(@Query("userId") int loginUsedId);

    @POST("api/v2/transactions/me/pending-posted-txns")
    Call<TransactionList> meTransactionList(@Body TransactionListRequest request);


//    @POST("api/v2/fiserv/sync-account")
//    Call<SyncAccount> meSyncAccount();

    @GET("api/v2/encryption/publickey")
    Call<PublicKeyResponse> getPublicKey(@Query("userId") int userId);

    @DELETE("api/v2/banks/me")
    Call<BankDeleteResponseData> deleteBank(@Query("accountId") Integer accountId);

//    @POST("api/v2/user/authenticate")
//    Call<LoginResponse> authenticatePassword(@Body PasswordRequest request);

    @POST("api/v2/user/verify")
    Call<LoginResponse> authenticatePassword(@Body PasswordRequest request);

    @Multipart
    @POST("api/v2/profile/me/upload-identity")
    Call<IdentityImageResponse> uploadIdentityImage(@Part MultipartBody.Part filee,
                                                    @Part("identityType") RequestBody type,
                                                    @Part("identityNumber") RequestBody number);

    @Multipart
    @POST("api/v2/business/sign-agreement")
    Call<SignedAgreementResponse> signedAgreement(@Part MultipartBody.Part file,
                                                  @Part("agreementType") int agreementType);

    @POST("api/v2/business/agreements")
    Call<UpdateSignAgreementsResponse> updateSignAgreemets();

    @DELETE("api/v2/profile/me/remove-identity")
    Call<RemoveIdentityResponse> removeIdentityImage(@Query("identityType") String identityType);

    @DELETE("api/v2/profile/remove-identity/{identityId}")
    Call<RemoveIdentityResponse> removeImageMultiDocs(@Path("identityId") String identityType);

    @POST("api/v2/profile/identity")
    Call<IdentityAddressResponse> uploadIdentityAddress(@Body IdentityAddressRequest identityAddressRequest);

    @POST("api/v2/profile/me/tracker")
    Call<TrackerResponse> statusTracker();

    @POST("api/v2/register/add-customer")
    Call<AddBusinessUserResponse> registerAddCustomer();

    @POST("api/v2/register/add-business-user")
    Call<AddBusinessUserResponse> addBusinessUserInIndividual();

    @POST("api/v2/register/add-dba/{companyID}")
    Call<AddBusinessUserResponse> addDBAInBusinessAccount(@Path("companyID") int companyID);

//    @POST("api/v2/cards/encrypt/me")
//    Call<CardResponse> saveCards(@Body CardRequest request);

    @POST("api/v2/cards/me")
    Call<CardResponse> saveCards(@Body CardRequest request);

//    @POST("api/v2/cards/me/encrypt/preauth-verify")
//    Call<PreAuthResponse> preAuthVerify(@Body PreAuthRequest request);

    @POST("api/v2/cards/me/preauth-verify")
    Call<PreAuthResponse> preAuthVerify(@Body PreAuthRequest request);

    @POST("api/v2/neutrino/bin-lookup")
    Call<CardTypeResponse> cardType(@Body CardTypeRequest request);

    @GET("api/v2/profile/identity")
    Call<GetIdentityResponse> getIdentity();

    @POST("api/v2/transactions/latest-transactions")
    Call<LatestTxnResponse> getLatestTransactions(@Body LatestTransactionsRequest request);

    @PATCH("api/v2/cards/me")
    Call<CardEditResponse> editCards(@Body CardEditRequest request, @Query("cardId") Integer cardId);

    @DELETE("api/v2/cards/me")
    Call<CardDeleteResponse> deleteCards(@Query("cardId") Integer cardId);

    @PATCH("api/v2/profile/identity")
    Call<IdentityAddressResponse> uploadIdentityAddressPatch(@Body IdentityAddressRequest identityAddressRequest);

    @GET("api/v2/transactions/token/info/{gbxTxnId}/{txnType}")
    Call<TransactionDetails> getTransactionDetails(@Path("gbxTxnId") String gbxTxnId,
                                                   @Path("txnType") int txnType,
                                                   @Query("txnSubType") Integer txnSubType);

    @POST("api/v2/transactions/me/limit/{userType}")
    Call<TransactionLimitResponse> transactionLimits(@Body TransactionLimitRequest request, @Path("userType") int userType);

    @POST("api/v2/corda/fee")
    Call<TransferFeeResponse> transferFee(@Body TransferFeeRequest request);

//    @POST("api/v2/node/buyTokens")
//    Call<BuyTokenResponse> buyTokens(@Body BuyTokenRequest request);

    @POST("api/v2/node/buy-tokens")
    Call<BuyTokenResponse> buyTokens(@Body BuyTokenRequest request);

//    @POST("api/v2/node/buyTokens/{requestToken}")
//    Call<BuyTokenResponse> buyTokens(@Body BuyTokenRequest request, @Path("requestToken") String requestToken);

    @GET("api/v2/giftcard/giftCardBrands")
    Call<BrandsResponse> getGiftCards();

    @POST("api/v2/node/withdrawTokens")
    Call<WithdrawResponse> withdrawTokens(@Body WithdrawRequest request);

//    @POST("api/v2/node/withdrawTokens/{requestToken}")
//    Call<WithdrawResponse> withdrawTokens(@Body WithdrawRequest request, @Path("requestToken") String requestToken);

    @GET("api/v2/giftcard/giftCardBrandItems")
    Call<BrandsResponse> getGiftCardItems(@Query("brandKey") String brandKey);

    @GET("api/v2/user-requests/me/receive")
    Call<Notifications> getReceivedNotifications();

    @GET("api/v2/user-requests/me/sent")
    Call<Notifications> getSentNotifications();

    @GET("api/v2/notifications/me")
    Call<Notifications> getNotifications();

    @GET("api/v2/profile/me/recentUsers")
    Call<RecentUsers> recentUsers();

    @GET("api/v2/user-requests/user-details/search")
    Call<CoyniUsers> getCoyniUsers(@Query("searchKey") String searchKey);

    @POST("api/v2/profile/registeredUsers")
    Call<RegUsersResponse> registeredUsers(@Body List<RegisteredUsersRequest> request);

    @POST("api/v2/comm-temp/invite/{templateId}/view")
    Call<TemplateResponse> getTemplate(@Path("templateId") int templateId, @Body TemplateRequest request);

    @POST("api/v2/node/sendTokens")
    Call<PayRequestResponse> sendTokens(@Body TransferPayRequest request);

//    @POST("api/v2/node/sendTokens/{requestToken}")
//    Call<PayRequestResponse> sendTokens(@Body TransferPayRequest request, @Path("requestToken") String requestToken);

    @POST("api/v2/notifications/me/mark-read")
    Call<UnReadDelResponse> notificationsMarkRead(@Body List<Integer> list);

    @POST("api/v2/notifications/me/mark-unread")
    Call<UnReadDelResponse> notificationsMarkUnRead(@Body List<Integer> list);

    @POST("api/v2/notifications/me/mark-clear")
    Call<UnReadDelResponse> notificationDelete(@Body List<Integer> list);

    @POST("api/v2/user-requests")
    Call<UserRequestResponse> userRequests(@Body UserRequest request);

    @PATCH("api/v2/user-requests")
    Call<UserRequestResponse> updateUserRequests(@Body StatusRequest request);

    @GET("api/v2/business/tracker")
    Call<BusinessTrackerResponse> getBusinessTracker();

    @GET("api/v2/business/company-info")
    Call<CompanyInfoResp> getCompanyInforamtion();

    @PATCH("api/v2/business/company-info")
    Call<CompanyInfoUpdateResp> updateCompanyInforamtion(@Body CompanyInfoRequest companyInfoRequest);

    @PATCH("/api/v2/business/contact-info")
    Call<CompanyInfoUpdateResp> updateContactInforamtion(@Body ContactInfoRequest contactInfoRequest);

    @PATCH("api/v2/business/dba-info")
    Call<DBAInfoUpdateResp> updateDBAInforamtion(@Body DBAInfoRequest dbaInfoRequest);

    @POST("api/v2/business/dba-info")
    Call<DBAInfoUpdateResp> postDBAInforamtion(@Body DBAInfoRequest dbaInfoRequest);


    @GET("api/v2/business/payment-methods")
    Call<PaymentMethodsResponse> meBusinessPaymentMethods();

    @POST("api/v2/banks/me")
    Call<CogentResponse> saveBanks(@Body CogentRequest request);

    @POST("api/v2/business/company-info")
    Call<CompanyInfoUpdateResp> postCompanyInforamtion(@Body CompanyInfoRequest companyInfoRequest);

    @GET("/api/v2/business/dba-info")
    Call<DBAInfoResp> getDBAInforamtion();

    @PATCH("api/v2/coyni-pin/stepup")
    Call<StepUpResponse> stepUpPin(@Body ValidateRequest request);

    //Deprecated
//    @GET("api/v2/profile/me/{walletType}")
//    Call<BusinessWalletResponse> meMerchantWallet(@Path("walletType") String walletType);

    @GET("api/v2/profile/wallets")
    Call<BusinessWalletResponse> meWallets();

    @GET("api/v2/lov/BT")
    Call<BusinessTypeResp> getBusinessType();

    @POST("api/v2/node/cancel-buytoken/{gbxTxnId}")
    Call<CancelBuyTokenResponse> cancelBuyToken(@Path("gbxTxnId") String gbxTxnId);

    @GET("/api/v2/profile/fee-type/{feeStructureId}")
    Call<Fees> meFees(@Path("feeStructureId") int feeStructureId);

    @POST("api/v2/cards/merchant/addcard")
    Call<BusinessCardResponse> saveBusinessCards(@Body BusinessCardRequest request);

    @POST("api/v2/user/biometric/token")
    Call<BiometricTokenResponse> biometricToken(@Body BiometricTokenRequest request);

    @GET("api/v2/banks/me")
    Call<BankResponse> meBanks();

    @GET("api/v2/business/beneficial-owners")
    Call<BOResp> getBeneficialOwners();

    @POST("api/v2/business/beneficial-owners")
    Call<BOIdResp> postBeneficialOwnersID();

    @DELETE("api/v2/business/beneficial-owners")
    Call<DeleteBOResp> deleteBeneficialOwner(@Query("beneficialOwnerId") Integer beneficialOwnerId);

    @PATCH("api/v2/business/beneficial-owners/update")
    Call<BOPatchResp> patchBeneficialOwner(@Query("id") Integer beneficialOwnerId, @Body BORequest boRequest);

    @Multipart
    @POST("api/v2/business/beneficial-owners-doc/{beneficialOwnerId}")
    Call<IdentityImageResponse> uploadBODoc(@Path("beneficialOwnerId") Integer beneficialOwnerId, @Part MultipartBody.Part filee,
                                            @Part("identityType") RequestBody type);

    @DELETE("api/v2/business/beneficial-owners-doc/remove")
    Call<RemoveIdentityResponse> removeBODoc(@Query("identityType") String identityType, @Query("beneficialOwnerId") String beneficialOwnerId);

    @GET("api/v2/business/beneficial-owners-validate")
    Call<BOValidateResp> validateBeneficialOwners();

    @GET("api/v2/banks/me")
    Call<BanksResponseModel> getBankAccountsData();

    @POST("api/v2/business/submit")
    Call<ApplicationSubmitResponseModel> postApplicationSubmissionData();

    @PATCH("/api/v2/business/cancel")
    Call<CancelApplicationResponse> cancelMerchant();

    @POST("api/v2/team/retrieve")
    Call<TeamListResponse> getTeamData(@Body EmptyRequest request);

    @POST("api/v2/team/retrieve")
    Call<TeamListResponse> getSearchData(@Body SearchKeyRequest searchKey);

    @PATCH("api/v2/team/update/{teamMemberId}")
    Call<TeamInfoAddModel> updateTeamMember(@Body TeamRequest request, @Path("teamMemberId") Integer teamMemberId);

    @DELETE("api/v2/team/{teamMemberId}")
    Call<TeamInfoAddModel> deleteTeamMember(@Path("teamMemberId") Integer teamMemberId);

    @GET("api/v2/team/{teamMemberId}")
    Call<TeamGetDataModel> getTeamMember(@Path("teamMemberId") Integer teamMemberId);

    @POST("api/v2/team/send-invitation")
    Call<TeamInfoAddModel> addTeamMember(@Body TeamRequest request);

    @PATCH("/api/v2/team/cancel")
    Call<TeamInfoAddModel> cancelTeamMember(@Query("userId") Integer teamMemberId);

    @GET("api/v2/business/summary")
    Call<ApplicationSummaryModelResponse> getApplicationSummaryData();

    @POST("api/v2/business/fees")
    Call<CompanyInfoUpdateResp> fees();

    @GET("api/v2/underwriting/user/action-required")
    Call<ActionRequiredResponse> postAdditionActionRequired();

    @GET("api/v2/underwriting/user/action-required")
    Call<ActionRqrdResponse> getActionRqrdCust();

    @Multipart
    @POST("api/v2/underwriting/user/business/action-required")
    Call<ActionRequiredSubmitResponse> submitActionRequired(@Part MultipartBody.Part[] body,
                                                            @Part("information") RequestBody type);

    @POST("api/v2/underwriting/user/business/action-required")
    Call<ActionRequiredSubmitResponse> submitMerchantActionRequired(@Body InformationRequest type);

    @POST("api/v2/underwriting/user/business/action-required")
    Call<ActionRequiredSubmitResponse> submitMerchantActionRequired(@Body RequestBody data);

    @GET("api/v2/transactions/admin/totalPayout")
    Call<BatchPayoutListResponse> getPayoutListData();

    @POST("api/v2/transactions/admin/totalPayout")
    Call<BatchPayoutListResponse> getPayoutListData(@Body BatchNowRequest request);

    @POST("api/v2/transactions/admin/totalPayout")
    Call<BatchPayoutListResponse> getRollingListData(@Body RollingListRequest request);

    @POST("api/v2/transactions/admin/totalPayout")
    Call<BatchPayoutListResponse> getRollingListData(@Body RollingSearchRequest searchKey);

    @GET("api/v2/reserve/user/rule")
    Call<RollingRuleResponse> getRollingRuleDetails();

    @POST("api/v2/transactions/merchatPayout/summary")
    Call<DetailsResponse> getReserveIdDetails(@Body DetailsRequest reserveDetailsRequest);

    @POST("api/v2/reserve/manual-release/all")
    Call<ManualListResponse> getManualListData(@Body EmptyRequest request);

    @POST("api/v2/reserve/manual-release/all")
    Call<ManualListResponse> getManualListData(@Body SearchKeyRequest searchKey);

    @POST("api/v2/transactions/merchatPayout/summary")
    Call<BatchPayoutIdDetailsResponse> batchPayoutIdDetails(@Body BatchPayoutDetailsRequest batchPayoutDetailsRequest);

    @POST("api/v2/transactions/admin/totalPayout")
    Call<BatchPayoutListResponse> getPayoutlistData(@Query("searchKey") String searchKey);

    @POST("api/v2/transactions/admin/totalPayout")
    Call<BatchPayoutListResponse> getPayoutlistdata(@Query("fromDate") String fromDate, @Query("toDate") String toDate);

    @POST("api/v2/node/refund/verify")
    Call<RefundDataResponce> getRefundDetails(@Body RefundReferenceRequest refundrefrequest);

    @POST("api/v2/node/refund/process")
    Call<RefundDataResponce> getRefundProcess(@Body RefundReferenceRequest request);

    @POST("api/v2/transactions/merchant-payout")
    Call<BatchNowResponse> getSlideBatchNow(@Body BatchNowPaymentRequest request);

    @POST("api/v2/transactions/admin/reserve/summary")
    Call<ReserveListResponse> getReserveListItems();

    @POST("api/v2/profile/download-url")
    Call<DownloadImageResponse> getDownloadUrl(@Body List<DownloadUrlRequest> downloadUrlRequest);

    @GET("api/v2/agreements/url")
    Call<DownloadDocumentResponse> getAgreementUrl(@Query("agreementType") String agreementType);

    @GET("api/v2/agreements/url/{documentNumber}")
    Call<DownloadDocumentResponse> getAgreementUrlByDocumentNumber(@Path("documentNumber") String documentNumber);

    @POST("api/v2/node/paidOrder")
    Call<PaidOrderResp> paidOrder(@Body PaidOrderRequest request);

    @POST("api/v2/transactions/business-activity")
    Call<BusinessActivityResp> businessActivity(@Body BusinessActivityRequest businessActivityRequest);

    @POST("api/v2/transactions/admin/merchant-activity-chart")
    Call<MerchantActivityResp> merchantActivity(@Body MerchantActivityRequest request);

    @POST("api/v2/node/cancel-withdraw/{gbxTxnId}")
    Call<CancelBuyTokenResponse> cancelWithdrawToken(@Path("gbxTxnId") String gbxTxnId);

    @Multipart
    @POST("api/v2/underwriting/user/customer/action-required")
    Call<SubmitActionRqrdResponse> submitActRqrd(@Part MultipartBody.Part[] body,
                                                 @Part("underwritingActionRequired") RequestBody type);

    @POST("api/v2/underwriting/user/customer/action-required")
    Call<SubmitActionRqrdResponse> submitCustomerActRqrd();

    @POST("api/v2/logs/transaction")
    Call<ActivityLogResp> activityLog(@Query("txnId") String txnId, @Query("userType") String userType);

    @POST("api/v2/checkout/order-info")
    Call<OrderInfoResponse> getOrderInfoDetails(@Body OrderInfoRequest request);

    @POST("api/v2/checkout/pay")
    Call<OrderPayResponse> orderPay(@Body OrderPayRequest request);

    @POST("/api/v2/checkout/scanQRCode")
    Call<ScanQrCodeResp> scanQrCode(@Body ScanQRRequest string);

    @POST("/api/v2/checkout/cancel-order")
    Call<CancelOrderResponse> orderCancel(@Body CancelOrderRequest request);

    @GET("api/v2/feature/controls/customer/{customerId}")
    Call<FeatureControlRespByUser> featureControlByUser(@Path("customerId") int customerId);

    @GET("api/v2/feature/controls/{portalType}")
    Call<FeatureControlGlobalResp> featureControlGlobal(@Path("portalType") String portalType);

    @POST("api/v2/user/intialize/device")
    Call<DeviceInitializeResponse> initializedevice(@Query("fcmToken") String fcmToken);

    @POST("api/v2/user/stepup/email")
    Call<StepUpOTPResponse> stepUpEmailOTP(@Body SmsRequest request);

    @POST("api/v2/user/stepup/phone")
    Call<StepUpOTPResponse> stepUpPhoneOTP(@Body SmsRequest request);

    @POST("api/v2/user/start")
    Call<WebSocketUrlResponse> webSocketUrl();

    @POST("api/v2/banks/manual")
    Call<ManualBankResponse> addManualBank(@Body ManualBankRequest request);

    @GET("api/v2/app-version/retrieve")
    Call<AppUpdateResp> getAppUpdate(@Query("osType") String osType);

    @POST("api/v2/user/sign/agreement")
    Call<OTPValidateResponse> signAgreementTOS(@Body SignAgreementRequest signAgreementRequest);

    @POST("api/v2/user/sign/agreement")
    Call<SignAgreementResponse> signAgreementPP(@Body SignAgreementRequest signAgreementRequest);

    @POST("api/v2/user/resend/email-otp/registration")
    Call<OTPValidateResponse> regEmailOTPResend(@Body OTPResendRequest resendRequest);

    @POST("api/v2/user/resend/phone-otp/registration")
    Call<OTPValidateResponse> regPhoneOTPResend(@Body OTPResendRequest resend);

    @GET("api/v2/agreements/has-to-sign-agreements")
    Call<SignAgreementsResp> hasToSignAgreements();

    @POST("api/v2/profile/update-sign-agreement")
    Call<UpdateSignAgreementsResp> signUpdatedAgreement(@Body UpdateSignRequest updateSignRequest);

    @Multipart
    @POST("api/v2/profile/update-agreement/{agreementId}")
    Call<UpdateSignAgreementsResp> signUpdatedAgreementDoc(@Path("agreementId") Integer agreementId, @Part MultipartBody.Part filee);

    @Multipart
    @POST("api/v2/underwriting/upload-docs")
    Call<IdentityImageResponse> uploadActionRequiredDoc(@Part MultipartBody.Part filee,
                                                        @Part("identityType") RequestBody type,
                                                        @Part("uwDocId") RequestBody docID);
}

