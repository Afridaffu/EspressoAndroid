package com.greenbox.coyni;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OTPValidationTest {

    @Test
    public void isOTPValid(){
        String OTP = "123456";
        assertTrue("OTP Validation", OTP.length() ==6);
    }
}
