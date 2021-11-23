package com.greenbox.coyni;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetrieveEmailTest {

    private static final Pattern VALID_PHONE_REGEX =
            Pattern.compile("([0-9])", Pattern.CASE_INSENSITIVE);

    public static boolean checkpwdValidity(String phnum) {
        Matcher matcher = VALID_PHONE_REGEX.matcher(phnum);
        return matcher.find();
    }
    @Test
    public void isPhnumValiid()
    {
        String phnum = "9199898767";
        assertTrue(phnum.length() == 10);
        assertEquals("9199898767",phnum);
    }

    @Test
    public void numIsEmpty()
    {
        String num = "";
        assertTrue(num.isEmpty());
    }
    @Test
    public void isValidFname()
    {
        String fname = "test";
        assertTrue(fname.length() >0 );
        assertEquals("test",fname);
    }
    @Test
    public void isValidLname()
    {
        String lname = "test";
        assertTrue(lname.length() >0 );
        assertEquals("test",lname);

    }
    @Test
    public void otpValidate()
    {
        String otp = "423234";
        assertTrue(otp.length() == 6 );
        assertEquals("423234", otp);
    }
    @Test
    public void otpIsEmpty()
    {
        String otp = "";
        assertTrue(otp.isEmpty());
    }
}

