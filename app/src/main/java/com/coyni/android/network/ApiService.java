package com.coyni.android.network;

import com.coyni.android.model.APIError;
import com.coyni.android.model.bank.BankDeleteResponseData;
import com.coyni.android.model.bank.Banks;
import com.coyni.android.model.bank.SignOn;
import com.coyni.android.model.bank.SyncAccount;
import com.coyni.android.model.buytoken.BuyTokenRequest;
import com.coyni.android.model.buytoken.BuyTokenResponse;
import com.coyni.android.model.cards.CardDeleteResponse;
import com.coyni.android.model.cards.CardEditRequest;
import com.coyni.android.model.cards.CardEditResponse;
import com.coyni.android.model.cards.CardRequest;
import com.coyni.android.model.cards.CardResponse;
import com.coyni.android.model.cards.CardType;
import com.coyni.android.model.cards.CardTypeRequest;
import com.coyni.android.model.cards.Cards;
import com.coyni.android.model.coynipin.CoyniPinRequest;
import com.coyni.android.model.coynipin.CoyniPinResponse;
import com.coyni.android.model.exchangerate.ExchangeRate;
import com.coyni.android.model.export.ExportColumns;
import com.coyni.android.model.export.ExportRequest;
import com.coyni.android.model.export.ExportResponse;
import com.coyni.android.model.giftcard.GiftCard;
import com.coyni.android.model.kycchecks.KYC_ChecksResponse;
import com.coyni.android.model.login.ChangePasswordRequest;
import com.coyni.android.model.login.ChangePasswordResponse;
import com.coyni.android.model.login.Login;
import com.coyni.android.model.login.LoginRequest;
import com.coyni.android.model.notification.Notifications;
import com.coyni.android.model.preauth.PreAuthRequest;
import com.coyni.android.model.preauth.PreAuthResponse;
import com.coyni.android.model.preferences.Preferences;
import com.coyni.android.model.preferences.UserPreference;
import com.coyni.android.model.receiverequests.ReceiveRequests;
import com.coyni.android.model.recentuser.RecentUsers;
import com.coyni.android.model.register.AgreementsByType;
import com.coyni.android.model.register.CustRegisRequest;
import com.coyni.android.model.register.CustRegisterResponse;
import com.coyni.android.model.register.EmailResendResponse;
import com.coyni.android.model.register.EmailResponse;
import com.coyni.android.model.register.ForgotPassword;
import com.coyni.android.model.register.SMSResend;
import com.coyni.android.model.register.SMSResponse;
import com.coyni.android.model.register.SetPassword;
import com.coyni.android.model.register.SetPasswordResponse;
import com.coyni.android.model.register.SmsRequest;
import com.coyni.android.model.reguser.RegUsersResponse;
import com.coyni.android.model.reguser.RegisteredUsersRequest;
import com.coyni.android.model.sendtransfer.TransferSendRequest;
import com.coyni.android.model.sendtransfer.TransferSendResponse;
import com.coyni.android.model.sentrequests.SentRequests;
import com.coyni.android.model.signet.SignetEditResponse;
import com.coyni.android.model.signet.SignetRequest;
import com.coyni.android.model.signet.SignetResponse;
import com.coyni.android.model.templates.TemplateRequest;
import com.coyni.android.model.templates.TemplateResponse;
import com.coyni.android.model.transactions.TokenTransactions;
import com.coyni.android.model.transactions.TransactionLimitRequest;
import com.coyni.android.model.transactions.TransactionLimitResponse;
import com.coyni.android.model.transactions.buycreditcard.BuyCreditCard;
import com.coyni.android.model.transactions.payrequest.PayRequest;
import com.coyni.android.model.transactions.withdraw.WithdrawGiftCard;
import com.coyni.android.model.transactions.withdraw.WithdrawSignet;
import com.coyni.android.model.transactions.withdraw.WithdrawTransDetails;
import com.coyni.android.model.transferfee.TransferFeeRequest;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.model.user.AccountLimits;
import com.coyni.android.model.user.Agreements;
import com.coyni.android.model.user.AgreementsById;
import com.coyni.android.model.user.ImageRequest;
import com.coyni.android.model.user.ImageResponse;
import com.coyni.android.model.user.PublicKeyResponse;
import com.coyni.android.model.user.SavePdfRequest;
import com.coyni.android.model.user.User;
import com.coyni.android.model.user.UserData;
import com.coyni.android.model.user.UserPreferenceModel;
import com.coyni.android.model.userdetails.UserDetails;
import com.coyni.android.model.userrequest.UserReqPatchResponse;
import com.coyni.android.model.userrequest.UserRequest;
import com.coyni.android.model.userrequest.UserRequestPatch;
import com.coyni.android.model.userrequest.UserRequestResponse;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.model.wallet.WalletResponse;
import com.coyni.android.model.withdraw.WithdrawRequest;
import com.coyni.android.model.withdraw.WithdrawResponse;

import java.util.List;
import java.util.Map;
import java.util.Observable;

import okhttp3.MultipartBody;
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
import retrofit2.http.QueryMap;

public interface ApiService {
    @POST("api/v2/register/customer")
    Call<CustRegisterResponse> custRegister(@Body CustRegisRequest custRegisRequest);

    @POST("api/v2/user/login")
    Call<Login> login(@Body LoginRequest loginRequest);

    @POST("api/v2/user/sms-otp/validate")
    Call<Login> smsotp(@Body SmsRequest smsRequest);

    @POST("api/v2/user/email-otp/validate")
    Call<EmailResponse> emailotp(@Body SmsRequest smsRequest);

    @PATCH("api/v2/register/set-password")
    Call<SetPasswordResponse> setpassword(@Body SetPassword setPassword);

    @GET("api/v2/profile/me/wallets")
    Call<WalletResponse> meWallet();

    @GET("api/v2/profile/me")
    Call<User> meProfile();

//    @GET("api/v2/transactions/me")
//    Call<TokenTransactions> meTransactions(@Query("walletCategory") String walletCategory);

    @GET("api/v2/transactions/me")
    Call<TokenTransactions> meTransactions(@QueryMap Map<String, String> options);

    @POST("api/v2/profile/me/tracker")
    Call<UserTracker> meTracker(@Body Object o);

    @POST("api/v2/corda/fee")
    Call<TransferFeeResponse> transferFee(@Body TransferFeeRequest request);

    @POST("api/v2/user/email-otp/resend")
    Call<EmailResendResponse> emailotpresend(@Query("email") String email);

    @POST("api/v2/user/sms-otp/resend")
    Call<SMSResponse> smsotpresend(@Body SMSResend resend);

    @POST("api/v1/customers/me/transactions/get-exchange-rate")
    Call<ExchangeRate> exchangerate(@Query("fromCurrency") String fromCurrency, @Query("toCurrency") String toCurrency, @Query("amount") String amount);

    @POST("api/v2/node/sendTokens")
    Call<TransferSendResponse> sendTokens(@Body TransferSendRequest request);

    @GET("api/v2/cards/me")
    Call<Cards> meCards();

    @POST("api/v2/cards/me")
    Call<CardResponse> saveCards(@Body CardRequest request);

    @PATCH("api/v2/cards/me")
    Call<CardEditResponse> editCards(@Body CardEditRequest request, @Query("cardId") String cardId);

    @DELETE("api/v2/cards/me")
    Call<CardDeleteResponse> deleteCards(@Query("cardId") String cardId);

    @POST("api/v2/node/buyTokens")
    Call<BuyTokenResponse> buyTokens(@Body BuyTokenRequest request);

    @GET("api/v2/banks/me")
    Call<Banks> meBanks();

    @POST("api/v2/node/withdrawTokens")
    Call<WithdrawResponse> withdrawTokens(@Body WithdrawRequest request);

    @DELETE("api/v2/banks/me")
    Call<BankDeleteResponseData> deleteBank(@Query("accountId") String accountId);

    @GET("api/v2/giftcard/cards")
    Call<GiftCard> meGiftCards();

    @POST("api/v2/transactions/me/limit")
    Call<TransactionLimitResponse> transactionlimits(@Body TransactionLimitRequest request);

    @POST("api/v2/fiserv/signon")
    Call<SignOn> meSignOn();

    @GET("api/v2/export/transaction/{eventTypeId}/{eventSubTypeId}")
    Call<ExportColumns> getExportColumns(@QueryMap Map<String, String> options);

    @POST("api/v2/export/me/txnexport")
    Call<ExportResponse> exportData(@Body ExportRequest request);

    @POST("api/v2/neutrino/bin-lookup")
    Call<CardType> cardType(@Body CardTypeRequest request);

    @POST("api/v2/fiserv/sync-account")
    Call<SyncAccount> meSyncAccount();

    @GET("api/v2/transactions/token/info/{gbxTxnId}/{txnType}")
    Call<BuyCreditCard> getBuyCardTransDetails(@Path("gbxTxnId") String gbxTxnId, @Path("txnType") String txnType, @Query("txnSubType") String txnSubType);

    @GET("api/v2/transactions/token/info/{gbxTxnId}/{txnType}")
    Call<PayRequest> getPayReqTransDetails(@Path("gbxTxnId") String gbxTxnId, @Path("txnType") String txnType, @Query("txnSubType") String txnSubType);

    @GET("api/v2/transactions/token/info/{gbxTxnId}/{txnType}")
    Call<WithdrawTransDetails> getWithdrawTransDetails(@Path("gbxTxnId") String gbxTxnId, @Path("txnType") String txnType, @Query("txnSubType") String txnSubType);

    @GET("api/v2/transactions/token/info/{gbxTxnId}/{txnType}")
    Call<WithdrawGiftCard> getGiftTransDetails(@Path("gbxTxnId") String gbxTxnId, @Path("txnType") String txnType, @Query("txnSubType") String txnSubType);

    @GET("api/v2/transactions/token/info/{gbxTxnId}/{txnType}")
    Call<WithdrawSignet> getSignetTransDetails(@Path("gbxTxnId") String gbxTxnId, @Path("txnType") String txnType, @Query("txnSubType") String txnSubType);

    @POST("api/v2/cards/me/preauth-verify")
    Call<Login> preAuthVerify(@Body PreAuthRequest request);

    @POST("api/v2/profile/registeredUsers")
    Call<RegUsersResponse> registeredUsers(@Body List<RegisteredUsersRequest> request);

    @GET("api/v2/profile/me/recentUsers")
    Call<RecentUsers> recentUsers();

    @GET("api/v2/user-requests/user-details/{walletId}")
    Call<UserDetails> getUserDetails(@Path("walletId") String walletId);

    @POST("api/v2/comm-temp/invite/{templateId}/view")
    Call<TemplateResponse> getTemplate(@Path("templateId") int templateId, @Body TemplateRequest request);

    @POST("api/v2/user-requests")
    Call<UserRequestResponse> userRequests(@Body UserRequest request);

    @GET("api/v2/user-requests/me/sent")
    Call<SentRequests> getSentRequests();

    @GET("api/v2/user-requests/me/receive")
    Call<ReceiveRequests> getReceiveRequests();

    @PATCH("api/v2/user-requests")
    Call<UserReqPatchResponse> updateUserRequests(@Body UserRequestPatch request);

    @PATCH("api/v2/user/change-password")
    Call<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest changePasswordRequest, @Query("id") int id);

    @GET("api/v2/profile/me/preferences")
    Call<Preferences> mePreferences();

    @POST("api/v2/profile/me/add-address")
    Call<User> meAddAddress(@Body UserData request);

    @PATCH("api/v2/profile/me/update-address")
    Call<User> meUpdateAddress(@Body UserData request);

    @POST("api/v2/profile/me/preferences")
    Call<UserPreference> meUpdatePreferences(@Body UserPreferenceModel request);


    @GET("api/v2/profile/me/accountlimits/{userType}")
    Call<AccountLimits> meAccountLimits(@Path("userType") int userType);

    @GET("api/v2/profile/me/signedagreements")
    Call<Agreements> meAgreements();

    @GET("api/v2/agreements/{agreementId}")
    Call<AgreementsById> meAgreementsById(@Path("agreementId") int agreementId);

    @POST("api/v2/pdf/save")
    Call<ResponseBody> meAgreementsSavePdf(@Body SavePdfRequest request);

    @POST("api/v2/banks/me")
    Call<SignetResponse> saveBanks(@Body SignetRequest request);

    @PATCH("api/v2/banks/me")
    Call<SignetEditResponse> editBanks(@Body SignetRequest request, @Query("bankId") String bankId);

    @GET("api/v2/notifications/me")
    Call<Notifications> meNotifications();

    @POST("api/v2/notifications/me/clearAll")
    Call<APIError> notificationsClearAll();

    @POST("api/v2/notifications/me/mark-clear")
    Call<APIError> notificationsMarkClear(@Body List<Integer> list);

    @POST("api/v2/notifications/me/mark-read")
    Call<APIError> notificationsMarkRead(@Body List<Integer> list);

    @GET("api/v2/agreements/active/type")
    Call<AgreementsByType> meAgreementsByType(@Query("agreementType") int agreementType);

    @POST("api/v2/user/forgot-password")
    Call<ForgotPassword> forgotPassword(@Query("email") String email);

    @POST("api/v2/profile/me/kyc-checks/{reference-id}/status")
    Call<KYC_ChecksResponse> updateKYC(@Path("reference-id") String reference_id);

    @POST("api/v2/coyni-pin/register")
    Call<CoyniPinResponse> coyniPin(@Body CoyniPinRequest request);

    @POST("api/v2/cards/me/preauth-verify")
    Call<PreAuthResponse> preAuthCards(@Body PreAuthRequest request);

    @GET("/api/v2/encryption/publickey/email")
    Call<PublicKeyResponse> getPublicKey(@Query("email") String email);

    @POST("api/v2/fiserv/users")
    Call<Login> fiservUsers();

    @POST("api/v2/profile/me/uploadImage")
    Call<ImageResponse> uploadImage(@Body ImageRequest request);

    @Multipart
    @POST("api/v2/profile/me/uploadImage")
    Call<ImageResponse> updateProfile(@Part MultipartBody.Part image);

    @DELETE("api/v2/profile/me/removeImage")
    Call<ImageResponse> removeImage(@Query("filename") String filename);
}
