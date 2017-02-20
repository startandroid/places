package ru.startandroid.places;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ru.startandroid.places.app.App;
import ru.startandroid.places.events.EventBus;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

public class MainActivityTestAsync {

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    private SearchIdlingResource idlingResource;

    @Before
    public void registerIntentServiceIdlingResource() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        EventBus eventBus = App.getApp(instrumentation.getTargetContext()).getComponentsHolder().getAppComponent().getEventBus();
        idlingResource = new SearchIdlingResource(eventBus);
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void unregisterIntentServiceIdlingResource() {
        idlingResource.destroy();
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void searchProgress() {
        onView(withId(R.id.query)).perform(typeText("Shop"));
        onView(withId(R.id.search)).perform(click());
        onView(withId(R.id.search_progress)).check(matches(not(isDisplayed())));
    }

}
