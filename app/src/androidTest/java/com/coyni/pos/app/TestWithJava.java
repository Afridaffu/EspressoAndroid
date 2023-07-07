package com.coyni.pos.app;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import com.coyni.pos.app.view.OnboardActivity;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class TestWithJava {

    private ActivityScenario<OnboardActivity> activityScenario;
    private String loginText = "1075070120";
    private String loginPass = "Admin@123";
    private String pinEnter = "8765";
    private String refundId = "RFDCD239438A123A5ABB9FF37D0B83671";
    private String saleOrderId = "SOTA3B4804CC2973599C134A3C5A822D3";

    private int digitLayout(char number) {
        switch (number) {
            case '1':
                return R.id.keyOneTV;
            case '2':
                return R.id.keyTwoTV;
            case '3':
                return R.id.keyThreeTV;
            case '4':
                return R.id.keyFourTV;
            case '5':
                return R.id.keyFiveTV;
            case '6':
                return R.id.keySixTV;
            case '7':
                return R.id.keySevenTV;
            case '8':
                return R.id.keyEightTV;
            case '9':
                return R.id.keyNineTV;
            case '0':
                return R.id.keyZeroTV;
            default:
                throw new IllegalArgumentException("Unsupported number: " + number);
        }
    }

    @Test
    public void testTransactionList() {
        activityScenario = ActivityScenario.launch(OnboardActivity.class);

        Espresso.onView(ViewMatchers.withId(R.id.tvButton)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.tidET)).perform(ViewActions.typeText(loginText));
        Espresso.onView(ViewMatchers.withId(R.id.passwordET)).perform(ViewActions.typeText(loginPass));
        Espresso.onView(ViewMatchers.withId(R.id.tvButton)).perform(ViewActions.click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(ViewMatchers.withId(R.id.todayBatchCV))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (char number : pinEnter.toCharArray()) {
            int layoutId = digitLayout(number);
            Espresso.onView(ViewMatchers.withId(layoutId)).perform(ViewActions.click());
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(ViewMatchers.withId(R.id.searchET)).perform(ViewActions.typeText(refundId));
        Espresso.onView(ViewMatchers.withId(R.id.searchET)).perform(ViewActions.clearText());
        Espresso.onView(ViewMatchers.withId(R.id.searchET)).perform(ViewActions.typeText(saleOrderId));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(ViewMatchers.withId(R.id.txnListRV)).perform(ViewActions.click());

        activityScenario.close();
    }

    @Test
    public void testTransactionListNew() {
        activityScenario = ActivityScenario.launch(OnboardActivity.class);

        Espresso.onView(ViewMatchers.withId(R.id.tvButton)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.tidET)).perform(ViewActions.typeText(loginText));
        Espresso.onView(ViewMatchers.withId(R.id.passwordET)).perform(ViewActions.typeText(loginPass));
        Espresso.onView(ViewMatchers.withId(R.id.tvButton)).perform(ViewActions.click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(ViewMatchers.withId(R.id.todayBatchCV))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .perform(ViewActions.click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (char number : pinEnter.toCharArray()) {
            int layoutId = digitLayout(number);
            Espresso.onView(ViewMatchers.withId(layoutId)).perform(ViewActions.click());
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(ViewMatchers.withId(R.id.searchET)).perform(ViewActions.typeText(refundId));
        Espresso.onView(ViewMatchers.withId(R.id.searchET)).perform(ViewActions.clearText());
        Espresso.onView(ViewMatchers.withId(R.id.searchET)).perform(ViewActions.typeText(saleOrderId));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(ViewMatchers.withId(R.id.txnListRV)).perform(ViewActions.click());

        activityScenario.close();
    }
}
