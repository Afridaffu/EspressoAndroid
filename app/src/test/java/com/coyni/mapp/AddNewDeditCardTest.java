package com.coyni.mapp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddNewDeditCardTest {

    public static boolean checkNameForValidity(String name) {
        Matcher matcher = VALID_NAME_REGEX.matcher(name);
        return matcher.find();
    }
    private static final Pattern VALID_NAME_REGEX = Pattern.compile("[A-Z]");
    @Test
    public void isValidName() {
        String name = "CA";
        Assert.assertThat(String.format("Name Validity Test failed for %s ", name), checkNameForValidity(name), is(true));
    }


    public static boolean checkCardForValidity(String cardnum) {
        Matcher matcher = VALID_CARD_REGEX.matcher(cardnum);
        return matcher.find();
    }
    private static final Pattern VALID_CARD_REGEX = Pattern.compile("^[0-9]");
    @Test
    public void testIsCardValid() {
        String card = "5876765578907655";
        assertTrue(card.length() == 16);
        Assert.assertThat(String.format("Card Validity Test failed for %s ", card), checkCardForValidity(card), is(true));
    }


    @Test
    public void iscardExp() {
        DateTimeFormatter ccMonthFormatter = DateTimeFormatter.ofPattern("MM/uu");
        String creditCardExpiryDateString = "11/28";
        YearMonth lastValidMonth = YearMonth.parse(creditCardExpiryDateString, ccMonthFormatter);
    }


    public static boolean checkCvvForValidity(String cvv) {
        Matcher matcher = VALID_CVV_REGEX.matcher(cvv);
        return matcher.find();
    }
    private static final Pattern VALID_CVV_REGEX = Pattern.compile("^[0-9]");
    @Test
    public void isCvvValid()
    {
        String cvv = "236";
        assertTrue(cvv.length() == 3);
        Assert.assertThat(String.format("Card CVV Validity Test failed for %s ", cvv), checkCvvForValidity(cvv), is(true));
    }
}
