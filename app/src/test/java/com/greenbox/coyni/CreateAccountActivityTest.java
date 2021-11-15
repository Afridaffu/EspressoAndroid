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

    public static boolean checkEmailForValidity(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @Before
    public void setup(){
        strong = Pattern.compile(STRONG_PATTERN);
        medium = Pattern.compile(MEDIUM_PATTERN);
    }

    @Test
    public void isFirstNameValid() {
        String firstName = "Test";
        assertTrue(firstName.length() > 0);
    }

    @Test
    public void isLastNameValid() {
        String lastName = "Test";
        assertTrue(lastName.length() > 0);
    }

    @Test
    public void isEmailValid() {
        String testEmail = "test@ideyalabs.com";
        Assert.assertThat(String.format("Email Validity Test failed for %s ", testEmail),
                checkEmailForValidity(testEmail), is(true));
    }

    @Test
    public void isMobileNumberValid() {
        String phoneNumber = "9123456789";
        assertTrue(phoneNumber.length() >= 10);
    }

    @Test
    public void isPasswordValid() {
        String password = "Admin@123";

        assertTrue(strong.matcher(password).matches());
        if(strong.matcher(password).matches()){
            System.out.println("Strong Password");
        }else if(medium.matcher(password).matches()){
            System.out.println("Medium Password");
        }else {
            System.out.println("Week Password");
        }
    }

}
