package com.coyni.mapp;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPinTest {
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean checkEmailForValidity(String email) {
        email = email.trim();
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
    @Test
    public void testIsEmailValid(){
        String email = "Test@gmail.com";
        Assert.assertThat(String.format("Email Validity Test failed for %s ", email), checkEmailForValidity(email), is(true));
    }


    public static boolean checkOtpValidity(String otp) {
        Matcher matcher = VALID_OTP_REGEX.matcher(otp);
        return matcher.find();
    }
    private static final Pattern VALID_OTP_REGEX = Pattern.compile("^[0-9]");
    @Test
    public void testIsOptValid() {
        String otp = "641545";
        assertTrue(otp.length() == 6);
        Assert.assertThat(String.format("Otp Validity Test failed for %s ", otp), checkOtpValidity(otp), is(true));
    }


    public static boolean checkPinValidity(String pin) {
        Matcher matcher = VALID_PIN_REGEX.matcher(pin);
        return matcher.find();
    }
    private static final Pattern VALID_PIN_REGEX = Pattern.compile("[0-9]");
    @Test
    public void testIsPinValid(){
        String pin = "177687";
        assertTrue(pin.length() == 6);
        Assert.assertThat(String.format("Pin Validity Test failed for %s ", pin), checkPinValidity(pin), is(true));
    }
}

