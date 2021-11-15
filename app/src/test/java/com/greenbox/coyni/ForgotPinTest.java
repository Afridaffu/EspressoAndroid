package com.greenbox.coyni;
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

    @Test
    public void testIsOptValid()
    {
        String otp = "629545";
        assertTrue(otp.length() == 6);
        assertEquals("629545",otp);
    }
    @Test
    public void isValidPin()
    {
        String pin = "223234";
        assertTrue(pin.length() ==6);
        assertEquals("223234",pin);
    }
}

