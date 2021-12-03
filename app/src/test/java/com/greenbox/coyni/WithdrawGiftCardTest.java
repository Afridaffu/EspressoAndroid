package com.greenbox.coyni;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WithdrawGiftCardTest {

    public static boolean checkEmailForValidity(String email) {
        email = email.trim();
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    @Test
    public void testIsEmailValid() {
        String testEmail = "test123@gmail.com";
        Assert.assertThat(String.format("Email Validity Test failed for %s ", testEmail), checkEmailForValidity(testEmail), is(true));
    }


    public static boolean checkNameForValidity(String name) {
        name = name.trim();
        Matcher matcher = VALID_NAME_REGEX.matcher(name);
        return matcher.find();
    }
    private static final Pattern VALID_NAME_REGEX = Pattern.compile("^[A-Z]",Pattern.CASE_INSENSITIVE);

    @Test
    public void isValidName()
    {
        String fname = "test";
        assertTrue(fname.length() >=0 && fname.length() <=20);
        Assert.assertThat(String.format("Fname Validity Test failed for %s ", fname), checkNameForValidity(fname), is(true));
    }
    @Test
    public void isValidlName()
    {
        String lname = "coyni";
        assertTrue(lname.length() >=0 && lname.length() <=20);
        Assert.assertThat(String.format("Lname Validity Test failed for %s ", lname), checkNameForValidity(lname), is(true));
    }




    public static boolean checkAmountForValidity(String amount) {
        Matcher matcher = VALID_AMOUNT_REGEX.matcher(amount);
        return matcher.find();
    }
    private static final Pattern VALID_AMOUNT_REGEX = Pattern.compile("^[1-9]");
    @Test
    public void testIsAmountValid() {
        String testamnt = "200";
        Assert.assertThat(String.format("Amount Validity Test failed for %s ", testamnt), checkAmountForValidity(testamnt), is(true));
    }
}
