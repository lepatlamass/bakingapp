package com.movieapp.konwo.sweetbaking.main.activities;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;

import com.movieapp.konwo.sweetbaking.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import androidx.test.espresso.IdlingRegistry;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(JUnit4.class)
@LargeTest
public class StepsListActivityTest {
    private static final String RECIPE_NAME = "Brownies";
    private static final int MIN_RECIPE_COUNT = 4;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule;

    public StepsListActivityTest() {
        mActivityRule = new ActivityTestRule<>(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        IdlingRegistry.getInstance().register(
                mActivityRule.getActivity().getCountingIdlingResource());
    }

    @Test
    public void onLaunch_recipeListIsDisplayed() {
        // Check that the Recycler View is  displayed
        onView(withId(R.id.recipe_fragment)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
        IdlingRegistry.getInstance().unregister(
                mActivityRule.getActivity().getCountingIdlingResource());
    }
}
