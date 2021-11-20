package com.greenbox.coyni.network;

import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.ChangePassword;
import com.greenbox.coyni.model.ChangePasswordRequest;
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
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailRequest;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailValidateRequest;
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
import com.greenbox.coyni.model.users.AccountLimits;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
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

}
