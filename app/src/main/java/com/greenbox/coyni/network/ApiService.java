package com.greenbox.coyni.network;

import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.ChangePassword;
import com.greenbox.coyni.model.ChangePasswordRequest;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.cards.CardRequest;
import com.greenbox.coyni.model.cards.CardResponse;
import com.greenbox.coyni.model.cards.CardTypeRequest;
import com.greenbox.coyni.model.cards.CardTypeResponse;
import com.greenbox.coyni.model.identity_verification.IdentityAddressRequest;
import com.greenbox.coyni.model.identity_verification.IdentityAddressResponse;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.identity_verification.RemoveIdentityResponse;
import com.greenbox.coyni.model.login.PasswordRequest;
import com.greenbox.coyni.model.preauth.PreAuthRequest;
import com.greenbox.coyni.model.preauth.PreAuthResponse;
import com.greenbox.coyni.model.profile.TrackerResponse;
import com.greenbox.coyni.model.publickey.PublicKeyResponse;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SyncAccount;
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.model.coynipin.PINRegisterResponse;
import com.greenbox.coyni.model.coynipin.RegisterRequest;
import com.greenbox.coyni.model.coynipin.ValidateRequest;
import com.greenbox.coyni.model.coynipin.ValidateResponse;
import com.greenbox.coyni.model.forgotpassword.EmailValidateResponse;
import com.greenbox.coyni.model.forgotpassword.ManagePasswordRequest;
import com.greenbox.coyni.model.forgotpassword.ManagePasswordResponse;
import com.greenbox.coyni.model.forgotpassword.SetPassword;
import com.greenbox.coyni.model.forgotpassword.SetPasswordResponse;
import com.greenbox.coyni.model.login.BiometricLoginRequest;
import com.greenbox.coyni.model.login.LoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailRequest;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailValidateRequest;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneRequest;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneResponse;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneValidateRequest;
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
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailResponse;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersResponse;
import com.greenbox.coyni.model.transaction.TransactionDetails;
import com.greenbox.coyni.model.update_resend_otp.UpdateResendOTPResponse;
import com.greenbox.coyni.model.update_resend_otp.UpdateResendRequest;
import com.greenbox.coyni.model.users.AccountLimits;
import com.greenbox.coyni.model.users.User;
import com.greenbox.coyni.model.users.UserData;
import com.greenbox.coyni.model.users.UserPreferenceModel;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.model.wallet.WalletResponse;

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

public interface ApiService {

    @POST("api/v2/user/email-otp/resend")
    Call<EmailResendResponse> emailotpresend(@Query("email") String email);

    @POST("api/v2/user/register/email-otp/validate")
    Call<EmailResponse> emailotp(@Body SmsRequest smsRequest);

    @POST("api/v2/user/sms-otp/resend")
    Call<SMSResponse> smsotpresend(@Body SMSResend resend);

    @POST("api/v2/user/register/phone-otp/validate")
    Call<SMSValidate> smsotp(@Body SmsRequest smsRequest);

    @POST("api/v2/user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

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

    @PATCH("/api/v2/user/change-password")
    Call<ChangePassword> mChangePassword(@Body ChangePasswordRequest request);

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

    @GET("api/v2/profile/me/wallets")
    Call<WalletResponse> meWallet();

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

    @POST("api/v2/fiserv/sync-account")
    Call<SyncAccount> meSyncAccount();

    @GET("api/v2/encryption/publickey")
    Call<PublicKeyResponse> getPublicKey(@Query("userId") int userId);

    @DELETE("api/v2/banks/me")
    Call<BankDeleteResponseData> deleteBank(@Query("accountId") String accountId);

    @POST("api/v2/user/authenticate")
    Call<LoginResponse> authenticatePassword(@Body PasswordRequest request);

    @Multipart
    @POST("api/v2/profile/me/upload-identity")
    Call<IdentityImageResponse> uploadIdentityImage(@Part MultipartBody.Part filee,
                                                    @Part("identityType") RequestBody type,
                                                    @Part("identityNumber") RequestBody number);

    @DELETE("api/v2/profile/me/remove-identity")
    Call<RemoveIdentityResponse> removeIdentityImage(@Query("identityType") String identityType);

    @POST("api/v2/profile/identity")
    Call<IdentityAddressResponse> uploadIdentityAddress(@Body IdentityAddressRequest identityAddressRequest);

    @POST("api/v2/profile/me/tracker")
    Call<TrackerResponse> statusTracker();

    @POST("api/v2/cards/encrypt/me")
    Call<CardResponse> saveCards(@Body CardRequest request);

    @POST("api/v2/cards/me/encrypt/preauth-verify")
    Call<PreAuthResponse> preAuthVerify(@Body PreAuthRequest request);

    @POST("api/v2/neutrino/bin-lookup")
    Call<CardTypeResponse> cardType(@Body CardTypeRequest request);

}
