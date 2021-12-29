package com.greenbox.coyni;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivityTest {

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
        assertTrue(password.length()>7 && password.length()<=12);
        if(strong.matcher(password).matches()){
            System.out.println("Strong Password");
        }else if(medium.matcher(password).matches()){
            System.out.println("Medium Password");
        }else {
            System.out.println("Week Password");
        }
    }


}
