package com.coyni.pos.app

import android.widget.DatePicker
import androidx.databinding.adapters.CalendarViewBindingAdapter.setDate
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.coyni.pos.app.databinding.ActivityTransactionHistoryBinding
import com.coyni.pos.app.view.OnboardActivity
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers
import org.junit.Test
import java.util.Calendar

class TestTransactionFilter {
    val activityScenario = ActivityScenario.launch(OnboardActivity::class.java)
    val loginText = "1075070120"
    val loginPass = "Admin@123"
    val pinEnter = "8765"
    val fromAmount="1"
    val toAmount = "10"


    private val digitLayout: (Char) -> Int = { number ->
        when (number) {
            '1' -> R.id.keyOneTV
            '2' -> R.id.keyTwoTV
            '3' -> R.id.keyThreeTV
            '4' -> R.id.keyFourTV
            '5' -> R.id.keyFiveTV
            '6' -> R.id.keySixTV
            '7' -> R.id.keySevenTV
            '8' -> R.id.keyEightTV
            '9' -> R.id.keyNineTV
            '0' -> R.id.keyZeroTV
            else -> throw IllegalArgumentException("Unsupported number: $number")
        }
    }

    @Test
    fun test_Filters() {

        onView(withId(R.id.tvButton)).perform(click())
        onView(withId(R.id.tidET)).perform(typeText(loginText))
        onView(withId(R.id.passwordET)).perform(typeText(loginPass))
        onView(withId(R.id.tvButton)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.todayBatchCV)).check(matches(isDisplayed())).perform(click())
        Thread.sleep(3000)
        for (number in pinEnter) {
            val layoutId = digitLayout(number)
        onView(withId(layoutId)).perform(click())
        }
        Thread.sleep(3000)
        //Transaction Status Filter
        onView(withId(R.id.ivFilterIcon)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.transStatusCompleted)).check(matches(isEnabled())).perform(click())
        onView(withId(R.id.applyFilterBtnCV)).check(matches(isDisplayed())).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.ivFilterIcon)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.transStatusCompleted)).check(matches(isEnabled())).perform(click())
        onView(withId(R.id.transStatusRefunded)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.applyFilterBtnCV)).check(matches(isDisplayed())).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.ivFilterIcon)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.transStatusRefunded)).check(matches(isEnabled())).perform(click())
        onView(withId(R.id.transStatusPartialRefund)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.applyFilterBtnCV)).check(matches(isDisplayed())).perform(click())
        Thread.sleep(3000)
        //Transaction Amount Filter
        onView(withId(R.id.ivFilterIcon)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.transStatusPartialRefund)).check(matches(isEnabled())).perform(click())
        onView(withId(R.id.transAmountStartET)).check(matches(isDisplayed())).perform(typeText(fromAmount))
        onView(withId(R.id.transAmountEndET)).check(matches(isDisplayed())).perform(typeText(toAmount))
        Thread.sleep(3000)
        //Date Filter
        onView(withId(R.id.ivFilterIcon)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.datePickET)).check(matches(isDisplayed())).perform(scrollTo())
        onView(withId(R.id.datePickET)).check(matches(isDisplayed())).perform(click())
    }
}