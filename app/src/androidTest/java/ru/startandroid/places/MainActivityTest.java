package ru.startandroid.places;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void mainActivityOnLaunchHintTextDisplayed() {
        onView(withHint(R.string.query_hint)).check(matches(isDisplayed()));
    }

    @Test
    public void searchWithText() {
        onView(withId(R.id.query)).perform(typeText("Shop"));
        onView(withId(R.id.search)).perform(click());
        onView(withId(R.id.search_progress)).check(matches(isDisplayed()));
    }

    @Test
    public void searchWithoutText() {
        onView(withId(R.id.search)).perform(click());
        onView(withId(R.id.search_progress)).check(matches(not(isDisplayed())));
    }

}