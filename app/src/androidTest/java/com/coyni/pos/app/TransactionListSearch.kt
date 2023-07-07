package com.coyni.pos.app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.coyni.pos.app.view.OnboardActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class TransactionListSearch{
    val activityScenario = ActivityScenario.launch(OnboardActivity::class.java)
    val loginText = "1075070120"
    val loginPass = "Admin@123"
    val pinEnter = "8765"
    val refundId = "RFDCD239438A123A5ABB9FF37D0B83671"
    val saleOrderId = "SOTA3B4804CC2973599C134A3C5A822D3"
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
    fun test_Transaction_List() {
        onView(withId(R.id.tvButton)).perform(click())
        onView(withId(R.id.tidET)).perform(typeText(loginText))
        onView(withId(R.id.passwordET))
            .perform(typeText(loginPass))
        onView(withId(R.id.tvButton)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.todayBatchCV))
            .check(matches(isDisplayed())).perform(click())
        Thread.sleep(3000)
        for (number in pinEnter) {
            val layoutId = digitLayout(number)
            onView(withId(layoutId)).perform(click())
        }
        Thread.sleep(3000)
//        onView(withId(R.id.txnListRV)).perform(scrollTo())
        onView(withId(R.id.searchET)).perform(typeText(refundId))
        onView(withId(R.id.searchET)).perform(clearText())
        onView(withId(R.id.searchET)).perform(typeText(saleOrderId))
        Thread.sleep(3000)
        onView(withId(R.id.txnListRV)).perform(click())


        activityScenario.close()
    }

}