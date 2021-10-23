package com.greenbox.coyni.network;

import com.greenbox.coyni.model.login.LoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.model.register.EmailResponse;
import com.greenbox.coyni.model.register.SMSResend;
import com.greenbox.coyni.model.register.SMSResponse;
import com.greenbox.coyni.model.register.SMSValidate;
import com.greenbox.coyni.model.register.SmsRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/v2/user/email-otp/resend")
    Call<EmailResendResponse> emailotpresend(@Query("email") String email);

    @POST("api/v2/register/email-otp/validate")
    Call<EmailResponse> emailotp(@Body SmsRequest smsRequest);

    @POST("api/v2/user/sms-otp/resend")
    Call<SMSResponse> smsotpresend(@Body SMSResend resend);

    @POST("api/v2/user/register/phone-otp/validate")
    Call<SMSValidate> smsotp(@Body SmsRequest smsRequest);

    @POST("api/v2/user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
