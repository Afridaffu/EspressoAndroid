package com.coyni.pos.app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.coyni.pos.app.view.LoginActivity
import com.coyni.pos.app.view.OnboardActivity
import org.hamcrest.CoreMatchers.not
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class TestOnboardingAndLogin {
    val activityScenario= ActivityScenario.launch(OnboardActivity::class.java)
    val loginText = "1075070120"
    val loginPass = "Admin@123"
    @Test
    fun test_Onboarding() {
        val activityScenario = ActivityScenario.launch(OnboardActivity::class.java)
        onView(withId(R.id.ICONClick)).check(matches(isDisplayed()))
        onView(withId(R.id.tvButton)).perform(click())
        onView(withId(R.id.tidET)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.tvButton)).check(matches(isDisplayed()))
        activityScenario.close()
    }
    @Test
    fun test_Login() {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        pressBack()
        onView(withId(R.id.tvButton)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.ImgLG)).check(matches(isDisplayed()))
        onView(withId(R.id.tidET)).perform(typeText(loginText))
        onView(withId(R.id.tvButton)).check(matches(not(isEnabled())))
        onView(withId(R.id.passwordET)).perform(typeText(loginPass))
        onView(withId(R.id.endIconIV)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.tvButton)).check(matches(isEnabled())).perform(click())
        activityScenario.close()
    }
}