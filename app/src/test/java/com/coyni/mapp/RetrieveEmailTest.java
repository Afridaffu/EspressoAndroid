package com.coyni.mapp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetrieveEmailTest {

    private static final Pattern VALID_PHONE_REGEX = Pattern.compile("([0-9])");
    public static boolean checkPhnValidity(String phnum) {
        Matcher matcher = VALID_PHONE_REGEX.matcher(phnum);
        return matcher.find();
    }
    @Test
    public void isPhnumValiid()
    {
        String phnum = "9173467467";
        assertTrue(phnum.length() == 10);
        Assert.assertThat(String.format("Phnum Validity Test failed for %s ", phnum), checkPhnValidity(phnum), is(true));
    }


    public static boolean checkNameForValidity(String name) {
        name = name.trim();
        Matcher matcher = VALID_NAME_REGEX.matcher(name);
        return matcher.find();
    }
    private static final Pattern VALID_NAME_REGEX = Pattern.compile("^[A-Z]",Pattern.CASE_INSENSITIVE);

    @Test
    public void isValidFName()
    {
        String fname = "test";
        assertTrue(fname.length() >=0 && fname.length() <=30);
        Assert.assertThat(String.format("Fname Validity Test failed for %s ", fname), checkNameForValidity(fname), is(true));
    }
    @Test
    public void isValidlName()
    {
        String lname = "coyni";
        assertTrue(lname.length() >=0 && lname.length() <=30);
        Assert.assertThat(String.format("Lname Validity Test failed for %s ", lname),checkNameForValidity(lname), is(true));
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

}

