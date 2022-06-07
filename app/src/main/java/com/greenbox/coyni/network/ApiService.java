package com.greenbox.coyni.network;

import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.BatchNow.BatchNowPaymentRequest;
import com.greenbox.coyni.model.BatchNow.BatchNowRequest;
import com.greenbox.coyni.model.BatchNow.BatchNowResponse;
import com.greenbox.coyni.model.BatchPayoutIdDetails.BatchPayoutDetailsRequest;
import com.greenbox.coyni.model.BatchPayoutIdDetails.BatchPayoutIdDetailsResponse;
import com.greenbox.coyni.model.BeneficialOwners.BOIdResp;
import com.greenbox.coyni.model.BeneficialOwners.BOPatchResp;
import com.greenbox.coyni.model.BeneficialOwners.BORequest;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.BeneficialOwners.BOValidateResp;
import com.greenbox.coyni.model.BeneficialOwners.DeleteBOResp;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListResponse;
import com.greenbox.coyni.model.BusinessBatchPayout.RollingListRequest;
import com.greenbox.coyni.model.ChangePassword;
import com.greenbox.coyni.model.ChangePasswordRequest;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoRequest;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoUpdateResp;
import com.greenbox.coyni.model.CompanyInfo.ContactInfoRequest;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoRequest;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoUpdateResp;
import com.greenbox.coyni.model.DashboardReserveList.ReserveListResponse;
import com.greenbox.coyni.model.EmptyRequest;
import com.greenbox.coyni.model.SearchKeyRequest;
import com.greenbox.coyni.model.UpdateSignAgree.UpdateSignAgreementsResponse;
import com.greenbox.coyni.model.actionRqrd.ActionRqrdResponse;
import com.greenbox.coyni.model.actionRqrd.SubmitActionRqrdResponse;
import com.greenbox.coyni.model.activtity_log.ActivityLogResp;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.bank.BankResponse;
import com.greenbox.coyni.model.bank.BanksResponseModel;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SyncAccount;
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.biometric.BiometricTokenResponse;
import com.greenbox.coyni.model.business_activity.BusinessActivityRequest;
import com.greenbox.coyni.model.business_activity.BusinessActivityResp;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.model.business_id_verification.CancelApplicationResponse;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.businesswallet.WalletRequest;
import com.greenbox.coyni.model.buytoken.BuyTokenRequest;
import com.greenbox.coyni.model.buytoken.BuyTokenResponse;
import com.greenbox.coyni.model.buytoken.CancelBuyTokenResponse;
import com.greenbox.coyni.model.cards.CardDeleteResponse;
import com.greenbox.coyni.model.cards.CardEditRequest;
import com.greenbox.coyni.model.cards.CardEditResponse;
import com.greenbox.coyni.model.cards.CardRequest;
import com.greenbox.coyni.model.cards.CardResponse;
import com.greenbox.coyni.model.cards.CardTypeRequest;
import com.greenbox.coyni.model.cards.CardTypeResponse;
import com.greenbox.coyni.model.cards.business.BusinessCardRequest;
import com.greenbox.coyni.model.cards.business.BusinessCardResponse;
import com.greenbox.coyni.model.check_out_transactions.OrderInfoRequest;
import com.greenbox.coyni.model.check_out_transactions.OrderInfoResponse;
import com.greenbox.coyni.model.check_out_transactions.OrderPayRequest;
import com.greenbox.coyni.model.check_out_transactions.OrderPayResponse;
import com.greenbox.coyni.model.coynipin.PINRegisterResponse;
import com.greenbox.coyni.model.coynipin.RegisterRequest;
import com.greenbox.coyni.model.coynipin.StepUpResponse;
import com.greenbox.coyni.model.coynipin.ValidateRequest;
import com.greenbox.coyni.model.coynipin.ValidateResponse;
import com.greenbox.coyni.model.coyniusers.CoyniUsers;
import com.greenbox.coyni.model.fee.Fees;
import com.greenbox.coyni.model.forgotpassword.EmailValidateResponse;
import com.greenbox.coyni.model.forgotpassword.ManagePasswordRequest;
import com.greenbox.coyni.model.forgotpassword.ManagePasswordResponse;
import com.greenbox.coyni.model.forgotpassword.SetPassword;
import com.greenbox.coyni.model.forgotpassword.SetPasswordResponse;
import com.greenbox.coyni.model.giftcard.BrandsResponse;
import com.greenbox.coyni.model.identity_verification.GetIdentityResponse;
import com.greenbox.coyni.model.identity_verification.IdentityAddressRequest;
import com.greenbox.coyni.model.identity_verification.IdentityAddressResponse;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.identity_verification.LatestTransactionsRequest;
import com.greenbox.coyni.model.identity_verification.LatestTxnResponse;
import com.greenbox.coyni.model.identity_verification.RemoveIdentityResponse;
import com.greenbox.coyni.model.login.BiometricLoginRequest;
import com.greenbox.coyni.model.login.LoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.model.login.PasswordRequest;
import com.greenbox.coyni.model.logout.LogoutResponse;
import com.greenbox.coyni.model.merchant_activity.MerchantActivityRequest;
import com.greenbox.coyni.model.merchant_activity.MerchantActivityResp;
import com.greenbox.coyni.model.notification.Notifications;
import com.greenbox.coyni.model.notification.StatusRequest;
import com.greenbox.coyni.model.notification.UnReadDelResponse;
import com.greenbox.coyni.model.paidorder.PaidOrderRequest;
import com.greenbox.coyni.model.paidorder.PaidOrderResp;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.payrequest.PayRequestResponse;
import com.greenbox.coyni.model.payrequest.TransferPayRequest;
import com.greenbox.coyni.model.preauth.PreAuthRequest;
import com.greenbox.coyni.model.preauth.PreAuthResponse;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.model.profile.DownloadDocumentResponse;
import com.greenbox.coyni.model.profile.DownloadImageResponse;
import com.greenbox.coyni.model.profile.DownloadUrlRequest;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.TrackerResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailRequest;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailValidateRequest;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneRequest;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneResponse;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneValidateRequest;
import com.greenbox.coyni.model.publickey.PublicKeyResponse;
import com.greenbox.coyni.model.recentusers.RecentUsers;
import com.greenbox.coyni.model.register.CustRegisRequest;
import com.greenbox.coyni.model.register.CustRegisterResponse;
import com.greenbox.coyni.model.register.EmailExistsResponse;
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.model.register.EmailResponse;
import com.greenbox.coyni.model.register.InitCustomerRequest;
import com.greenbox.coyni.model.register.InitializeCustomerResponse;
import com.greenbox.coyni.model.register.SMSResend;
import com.greenbox.coyni.model.register.SMSResponse;
import com.greenbox.coyni.model.register.SMSValidate;
import com.greenbox.coyni.model.register.SmsRequest;
import com.greenbox.coyni.model.reguser.RegUsersResponse;
import com.greenbox.coyni.model.reguser.RegisteredUsersRequest;
import com.greenbox.coyni.model.reserveIdDetails.DetailsRequest;
import com.greenbox.coyni.model.reserveIdDetails.DetailsResponse;
import com.greenbox.coyni.model.reservemanual.ManualListResponse;
import com.greenbox.coyni.model.reservemanual.RollingSearchRequest;
import com.greenbox.coyni.model.reserverule.RollingRuleResponse;
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailResponse;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersResponse;
import com.greenbox.coyni.model.signedagreements.SignedAgreementResponse;
import com.greenbox.coyni.model.signet.SignetRequest;
import com.greenbox.coyni.model.signet.SignetResponse;
import com.greenbox.coyni.model.submit.ApplicationSubmitResponseModel;
import com.greenbox.coyni.model.summary.ApplicationSummaryModelResponse;
import com.greenbox.coyni.model.team.TeamGetDataModel;
import com.greenbox.coyni.model.team.TeamInfoAddModel;
import com.greenbox.coyni.model.team.TeamListResponse;
import com.greenbox.coyni.model.team.TeamRequest;
import com.greenbox.coyni.model.templates.TemplateRequest;
import com.greenbox.coyni.model.templates.TemplateResponse;
import com.greenbox.coyni.model.transaction.RefundDataResponce;
import com.greenbox.coyni.model.transaction.RefundReferenceRequest;
import com.greenbox.coyni.model.transaction.TransactionDetails;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.transferfee.TransferFeeRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.model.underwriting.ActionRequiredResponse;
import com.greenbox.coyni.model.underwriting.ActionRequiredSubmitResponse;
import com.greenbox.coyni.model.update_resend_otp.UpdateResendOTPResponse;
import com.greenbox.coyni.model.update_resend_otp.UpdateResendRequest;
import com.greenbox.coyni.model.userrequest.UserRequest;
import com.greenbox.coyni.model.userrequest.UserRequestResponse;
import com.greenbox.coyni.model.users.AccountLimits;
import com.greenbox.coyni.model.users.User;
import com.greenbox.coyni.model.users.UserData;
import com.greenbox.coyni.model.users.UserPreferenceModel;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.model.withdraw.WithdrawRequest;
import com.greenbox.coyni.model.withdraw.WithdrawResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
import retrofit2.http.Url;

public interface ApiService {

    @POST("api/v2/user/email-otp/resend")
    Call<EmailResendResponse> emailotpresend(@Query("email") String email);

    @POST("api/v2/user/register/email-otp/validate")
    Call<EmailResponse> emailotp(@Body SmsRequest smsRequest);

    @POST("api/v2/user/sms-otp/resend")
    Call<SMSResponse> smsotpresend(@Body SMSResend resend);

    @POST("api/v2/user/register/phone-otp/validate")
    Call<SMSValidate> smsotp(@Body SmsRequest smsRequest);

    @POST("api/v2/user/sms-otp/validate")
    Call<SMSValidate> smsotpLogin(@Body SmsRequest smsRequest);

    @POST("api/v2/user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/v2/user/logout")
    Call<LogoutResponse> logout();

    @POST("api/v2/register/newcustomer")
    Call<CustRegisterResponse> custRegister(@Body CustRegisRequest custRegisRequest);

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

    @POST("api/v2/user/biometric/login")
    Call<LoginResponse> biometricLogin(@Body BiometricLoginRequest request);

    @GET("api/v2/profile/me")
    Call<Profile> meProfile();

    @POST("api/v2/user/update-email/otp/send")
    Call<UpdateEmailResponse> updateEmailSendOTP(@Body UpdateEmailRequest request);

    @POST("api/v2/user/update-email/otp-validate")
    Call<UpdateEmailResponse> updateEmailValidateOTP(@Body UpdateEmailValidateRequest request);

    @PATCH("api/v2/user/set-password")
    Call<ManagePasswordResponse> setExpiryPassword(@Body ManagePasswordRequest request);

    @GET("api/v2/user/validate-email")
    Call<EmailExistsResponse> validateEmail(@Query("email") String email);

    @GET("api/v2/profile/payment-methods")
    Call<PaymentMethodsResponse> mePaymentMethods();

    @POST("api/v2/user/update-phone/otp/send")
    Call<UpdatePhoneResponse> updatePhoneSendOTP(@Body UpdatePhoneRequest request);

    @POST("api/v2/user/update-phone/otp/validate")
    Call<UpdatePhoneResponse> updatePhoneValidateOTP(@Body UpdatePhoneValidateRequest request);

    @Multipart
    @PATCH("api/v2/profile/me/uploadImage")
    Call<ImageResponse> updateProfile(@Part MultipartBody.Part image);

    @DELETE("api/v2/profile/me/removeImage")
    Call<ImageResponse> removeImage(@Query("filename") String filename);

//    @GET("api/v2/profile/me/wallets")
//    Call<WalletResponse> meWallet();

    @GET("api/v2/profile/me/preferences")
    Call<Preferences> mePreferences();

    @POST("api/v2/profile/me/preferences")
    Call<UserPreference> meUpdatePreferences(@Body UserPreferenceModel request);

    @PATCH("api/v2/profile/me/update-address")
    Call<User> meUpdateAddress(@Body UserData request);

    @GET("api/v2/profile/me/profile-accounts")
    Call<ProfilesResponse> getProfiles();

    @POST("api/v2/fiserv/signon")
    Call<SignOn> meSignOn();

    @POST("api/v2/user/update/otp/resend")
    Call<UpdateResendOTPResponse> updateOtpResend(@Body UpdateResendRequest request);

    @GET("api/v2/user-requests/user-details/{walletId}")
    Call<UserDetails> getUserDetails(@Path("walletId") String walletId);

    @POST("api/v2/transactions/token/info")
    Call<TransactionDetails> getTransactionDt();

    @POST("api/v2/user/change-account")
    Call<AddBusinessUserResponse> getChangeAccount(@Query("userId") int loginUsedId);

    @POST("api/v2/transactions/me/pending-posted-txns")
    Call<TransactionList> meTransactionList(@Body TransactionListRequest request);


    @POST("api/v2/fiserv/sync-account")
    Call<SyncAccount> meSyncAccount();

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

    @POST("api/v2/node/buyTokens/{requestToken}")
    Call<BuyTokenResponse> buyTokens(@Body BuyTokenRequest request, @Path("requestToken") String requestToken);

    @GET("api/v2/giftcard/giftCardBrands")
    Call<BrandsResponse> getGiftCards();

//    @POST("api/v2/node/withdrawTokens")
//    Call<WithdrawResponse> withdrawTokens(@Body WithdrawRequest request);

    @POST("api/v2/node/withdrawTokens/{requestToken}")
    Call<WithdrawResponse> withdrawTokens(@Body WithdrawRequest request, @Path("requestToken") String requestToken);

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

//    @POST("api/v2/node/sendTokens")
//    Call<PayRequestResponse> sendTokens(@Body TransferPayRequest request);

    @POST("api/v2/node/sendTokens/{requestToken}")
    Call<PayRequestResponse> sendTokens(@Body TransferPayRequest request, @Path("requestToken") String requestToken);

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
    Call<SignetResponse> saveBanks(@Body SignetRequest request);

    @POST("api/v2/business/company-info")
    Call<CompanyInfoUpdateResp> postCompanyInforamtion(@Body CompanyInfoRequest companyInfoRequest);

    @GET("/api/v2/business/dba-info")
    Call<DBAInfoResp> getDBAInforamtion();

    @PATCH("api/v2/coyni-pin/stepup")
    Call<StepUpResponse> stepUpPin(@Body ValidateRequest request);

    //Deprecated
//    @GET("api/v2/profile/me/{walletType}")
//    Call<BusinessWalletResponse> meMerchantWallet(@Path("walletType") String walletType);

    @POST("api/v2/profile/wallet")
    Call<BusinessWalletResponse> meMerchantWallet(@Body WalletRequest request);

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
    Call<BOResp> getBeneficailOwners();

    @POST("api/v2/business/beneficial-owners")
    Call<BOIdResp> postBeneficailOwnersID();

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
    Call<BOValidateResp> validateBeneficailOwners();

    @GET("api/v2/banks/me")
    Call<BanksResponseModel> getBankAccountsData();

    @POST("api/v2/business/submit")
    Call<ApplicationSubmitResponseModel> postApplicationSubmissionData();

    @PATCH("/api/v2/business/cancel")
    Call<CancelApplicationResponse> cancelMerchant();

    @POST("api/v2/team/retrieve")
    Call<TeamListResponse> getTeamData(@Body EmptyRequest request);

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

    @POST("api/v2/logs/transaction")
    Call<ActivityLogResp> activityLog(@Query("txnId") String txnId, @Query("userType") String userType);

    @POST("api/v2/checkout/order-info")
    Call<OrderInfoResponse> getOrderInfoDetails(@Body OrderInfoRequest request);

    @POST("api/v2/checkout/pay")
    Call<OrderPayResponse> orderPay(@Body OrderPayRequest request);
}

