package com.openclassrooms.arista.ui

import android.os.SystemClock
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.openclassrooms.arista.R
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun userScreen_fieldsAreDisplayed() {
        onView(allOf(withId(R.id.et_name), isDisplayed())).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.et_email), isDisplayed())).check(matches(isDisplayed()))
    }

    @Test
    fun navigation_toSleep_showsSleepRecycler() {
        onView(withId(R.id.nav_sleep)).perform(click())
        onView(withId(R.id.sleep_recyclerview)).check(matches(isDisplayed()))
    }

    @Test
    fun exerciseFlow_addExercise_displaysNewItem() {
        val uniqueDuration = "777"

        onView(withId(R.id.nav_exercise)).perform(click())
        onView(withId(R.id.fab)).perform(click())

        onView(withId(R.id.durationEditText)).perform(replaceText(uniqueDuration), closeSoftKeyboard())
        onView(withId(R.id.intensityEditText)).perform(replaceText("9"), closeSoftKeyboard())
        onView(withText(R.string.add)).perform(click())

        // Let Flow collection + RecyclerView update complete.
        SystemClock.sleep(800)

        onView(withText(containsString("Duration: 777 minutes"))).check(matches(isDisplayed()))
    }
}
