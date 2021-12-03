package com.greenbox.coyni;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentityVerificationTest {
    public static boolean checkNumValidity(String num) {
        Matcher matcher = VALID_NUM_REGEX.matcher(num);
        return matcher.find();
    }
    private static final Pattern VALID_NUM_REGEX = Pattern.compile("^[0-9]");
    @Test
    public void isSSNLengthValid(){
        String ssnNum="1234";
        assertTrue(ssnNum.length()==4);
        Assert.assertThat(String.format("SSNLength Validity Test failed for %s ", ssnNum), checkNumValidity(ssnNum), is(true));

    }



    public static boolean checkMailAddValidity(String maddr) {
        maddr = maddr.trim();
        Matcher matcher = VALID_M_ADDRESS_REGEX.matcher(maddr);
        return matcher.find();
    }
    private static final Pattern VALID_M_ADDRESS_REGEX = Pattern.compile("^[0-9 -]+\\,[A-Z]+\\,",Pattern.CASE_INSENSITIVE);

    @Test
    public void isMailAddLine1(){
        String mailAdd="5-26,california,usa";
        assertTrue(mailAdd.length()<=30&&mailAdd.length()>0);
        Assert.assertThat(String.format("MailAddress Validity Test failed for %s ", mailAdd), checkMailAddValidity(mailAdd), is(true));

    }
    @Test
    public void isCityNameValid(){
        String cityName="New York";
        assertTrue(cityName.length()>0&&cityName.length()<=30);
        Assert.assertThat(String.format("State Name Validity Test failed for %s ", cityName), checkNameForValidity(cityName), is(true));
    }


    public static boolean checkNameForValidity(String state) {
        state = state.trim();
        Matcher matcher = VALID_NAME_REGEX.matcher(state);
        return matcher.find();
    }
    private static final Pattern VALID_NAME_REGEX = Pattern.compile("^[A-Z]",Pattern.CASE_INSENSITIVE);
    @Test
    public void isStateNameValid(){
        String stateName="Alaska";
        assertTrue(stateName.length() > 0 && stateName.length() <=30);
        Assert.assertThat(String.format("State Name Validity Test failed for %s ", stateName), checkZipValidity(stateName), is(true));
    }



    public static boolean checkZipValidity(String zip) {
        Matcher matcher = VALID_ZIP_REGEX.matcher(zip);
        return matcher.find();
    }
    private static final Pattern VALID_ZIP_REGEX = Pattern.compile("^[0-9]{5}");

    @Test
    public void isZipCodeValid(){
        String zip = "12127";
        assertTrue(zip.length() >0 && zip.length() <= 6);
        Assert.assertThat(String.format("Code Validity Test failed for %s ", zip), checkZipValidity(zip), is(true));
    }


    @Test
    public void isSSNFullValid(){
        String ssnfullnum="123456789101";
        assertTrue(ssnfullnum.length()==12);
        Assert.assertThat(String.format("SSNLength Validity Test failed for %s ", ssnfullnum), checkNumValidity(ssnfullnum), is(true));
    }



}