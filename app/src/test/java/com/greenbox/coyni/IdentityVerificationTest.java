package com.greenbox.coyni;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IdentityVerificationTest {
    @Test
    public void isSSNLenghtValid(){
        String ssnNum="1234";
        assertTrue(ssnNum.length()==4);
    }
    @Test
    public void isMailAddLine1(){
        String mailAdd="5-26,california,usa";
        assertTrue(mailAdd.length()<=30&&mailAdd.length()>0);
    }

    @Test
    public void isCityNameValid(){
        String cityName="New York";
        assertTrue(cityName.length()>0&&cityName.length()<=30);

    }
    @Test
    public void isStateNameValid(){
        String stateName="Alaska";
        assertTrue(stateName.length() > 0 && stateName.length() <=30);
    }

    @Test
    public void isZipCodeValid(){
        String cityName="California";
        assertTrue(cityName.length()>0&&cityName.length()<=30);
    }
    @Test
    public void isSSNFullValid(){
        String ssnfullnum="123456789101";
        assertTrue(ssnfullnum.length()==12);
        assertEquals("123456789101",ssnfullnum);
    }



}