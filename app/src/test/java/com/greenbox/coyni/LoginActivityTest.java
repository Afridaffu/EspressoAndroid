package com.greenbox.coyni;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class LoginActivityTest {

    @Test
    public void isEmailValid(){
        String isemailid="shivas@ideyalabs.com";
        assertTrue(isemailid.length()>0&&isemailid.length()<=255);
        assertEquals("shivas@ideyalabs.com",isemailid);

    }
    @Test
    public void isPasswordStregnth(){
        String pass="Shiva@123";
        assertTrue(pass.length()>7&&pass.length()<=12);
        assertEquals("Shiva@123",pass);
    }
}
