package com.greenbox.coyni;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuyTokenTest {

    public static boolean checkAmountForValidity(String amount) {
    Matcher matcher = VALID_AMOUNT_REGEX.matcher(amount);
    return matcher.find();
    }
    private static final Pattern VALID_AMOUNT_REGEX = Pattern.compile("^[1-9]+//.[0-9]");
    @Test
    public void testIsAmountValid() {
        String testamnt = "330.4687";
        Assert.assertThat(String.format("Amount Validity Test failed for %s ", testamnt), checkAmountForValidity(testamnt), is(true));
    }
}
