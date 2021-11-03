package com.greenbox.coyni.network;

import com.greenbox.coyni.model.coynipin.PINRegisterResponse;
import com.greenbox.coyni.model.coynipin.RegisterRequest;
import com.greenbox.coyni.model.coynipin.ValidateRequest;
import com.greenbox.coyni.model.coynipin.ValidateResponse;
import com.greenbox.coyni.model.forgotpassword.EmailValidateResponse;
import com.greenbox.coyni.model.forgotpassword.SetPassword;
import com.greenbox.coyni.model.forgotpassword.SetPasswordResponse;
import com.greenbox.coyni.model.login.LoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.model.register.CustRegisRequest;
import com.greenbox.coyni.model.register.CustRegisterResponse;
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

    @POST("/api/v2/register/newcustomer")
    Call<CustRegisterResponse> custRegister(@Body CustRegisRequest custRegisRequest);

    @POST("/api/v2/user/email-otp/validate")
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


    @POST("api/v2/coyni-pin/register")
    Call<PINRegisterResponse> coyniPINRegister(@Body RegisterRequest request);

    @PATCH("/api/v2/register/newcustomer/{userId}")
    Call<CustRegisterResponse> custRegisterPatch(@Body CustRegisRequest custRegisRequest, @Path("userId") String  id);

}
