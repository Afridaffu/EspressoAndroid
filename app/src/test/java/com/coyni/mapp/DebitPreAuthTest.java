package com.coyni.mapp;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DebitPreAuthTest {
    public static boolean checkAmountForValidity(String amount) {
        Matcher matcher = VALID_AMOUNT_REGEX.matcher(amount);
        return matcher.find();
    }
    private static final Pattern VALID_AMOUNT_REGEX = Pattern.compile("^[1-9]");
    @Test
    public void testIsAmountValid() {
        String testamnt = "12.33";
        Assert.assertThat(String.format("Amount Validity Test failed for %s ", testamnt), checkAmountForValidity(testamnt), is(true));
    }
}
