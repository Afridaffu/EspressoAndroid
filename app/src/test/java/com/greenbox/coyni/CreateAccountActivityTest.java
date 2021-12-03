package com.greenbox.coyni;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;

import com.greenbox.coyni.view.CreateAccountActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivityTest {

    private Pattern strong, medium;

    //    !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
    private static final String STRONG_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!#$%&'()*+,-./:;<=>?@^_`{|}~]).{8,})";

    private static final String MEDIUM_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!#$%&'()*+,-./:;<=>?@^_`{|}~]).{6,})";

    @Before
    public void setup(){
        strong = Pattern.compile(STRONG_PATTERN);
        medium = Pattern.compile(MEDIUM_PATTERN);
    }
    @Test
    public void isPasswordValid() {
        String password = "Admin@123";
        assertTrue(password.length() > 7 && password.length() <= 12);
        if(strong.matcher(password).matches()){
            System.out.println("Strong Password");
        }else if(medium.matcher(password).matches()){
            System.out.println("Medium Password");
        }else {
            System.out.println("Week Password");
        }
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



    public static boolean checkEmailForValidity(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    @Test
    public void isEmailValid() {
        String testEmail = "test@ideyalabs.com";
        Assert.assertThat(String.format("Email Validity Test failed for %s ", testEmail),
                checkEmailForValidity(testEmail), is(true));
    }




    public static boolean checkPhnumForValidity(String amount) {
        Matcher matcher = VALID_PHONE_REGEX.matcher(amount);
        return matcher.find();
    }
    private static final Pattern VALID_PHONE_REGEX = Pattern.compile("^[0-9]");

    @Test
    public void isMobileNumberValid() {
        String phoneNumber = "9123456789";
        assertTrue(phoneNumber.length() == 10);
        Assert.assertThat(String.format("Phone num Validity Test failed for %s ",phoneNumber), checkPhnumForValidity(phoneNumber), is(true));

    }
}
