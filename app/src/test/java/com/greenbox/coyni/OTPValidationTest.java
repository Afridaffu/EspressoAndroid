package com.greenbox.coyni;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OTPValidationTest {

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

}
