package com.coyni.mapp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DebitMaillingAddressTest {

    public static boolean checkNameForValidity(String name) {
        name = name.trim();
        Matcher matcher = VALID_NAME_REGEX.matcher(name);
        return matcher.find();
    }
    private static final Pattern VALID_NAME_REGEX = Pattern.compile("^[A-Z]",Pattern.CASE_INSENSITIVE);
    @Test
    public void isStateValid() {
        String state = "CA";
        assertTrue(state.length() > 0 && state.length() <= 20);
        Assert.assertThat(String.format("Name Validity Test failed for %s ", state), checkNameForValidity(state), is(true));
    }


    public static boolean checkAddrs1ForValidity(String addr) {
        addr = addr.trim();
        Matcher matcher = VALID_ADDRESS_REGEX.matcher(addr);
        return matcher.find();
    }
    private static final Pattern VALID_ADDRESS_REGEX = Pattern.compile("^[0-9]+\\,[A-Z]",Pattern.CASE_INSENSITIVE);
    @Test
    public void isBuilAdrsValid()
    {
       String addr = "3131,Camino Del Rio North";
       assertTrue(addr.length() > 1);
       Assert.assertThat(String.format("Address Validity Test failed for %s ", addr), checkAddrs1ForValidity(addr), is(true));

    }

    public static boolean checkAddrs2ForValidity(String addr2) {
        addr2 = addr2.trim();
        Matcher matcher = VALID_ADDRESS2_REGEX.matcher(addr2);
        return matcher.find();
    }
    private static final Pattern VALID_ADDRESS2_REGEX = Pattern.compile("^[A-Z]",Pattern.CASE_INSENSITIVE);
    @Test
    public void isBuilAdrs2Valid()
    {
        String addrline2 = "san diego";
        assertTrue(addrline2.length() > 1);
        Assert.assertThat(String.format("Address Validity Test failed for %s ", addrline2), checkAddrs2ForValidity(addrline2), is(true));

    }

    @Test
    public void isCityNameValid(){
        String cityName="San Diego";
        assertTrue(cityName.length() >0 && cityName.length() <= 20);
        Assert.assertThat(String.format("Name Validity Test failed for %s ", cityName), checkNameForValidity(cityName), is(true));

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
    public void isCountryValid(){
        String cntry = "United States";
        assertTrue(cntry.length() >0 && cntry.length() <= 20);
        Assert.assertThat(String.format("Country Validity Test failed for %s ", cntry), checkNameForValidity(cntry), is(true));

    }
}

