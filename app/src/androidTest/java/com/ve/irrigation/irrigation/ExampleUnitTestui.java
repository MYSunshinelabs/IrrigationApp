package com.ve.irrigation.irrigation;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ve.irrigation.irrigation.activities.SplashActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleUnitTestui {


    @Rule
    public ActivityTestRule<SplashActivity> splashActivityActivityTestRule=new ActivityTestRule<>(SplashActivity.class);



    @Test
    public void testUI() {

        onView(withId(R.id.edittext_uitest)).perform(typeText("SATEHNDRA"));

        //onView(withId(R.id.textview_currentversion)).perform(click());

        onView(withId(R.id.edittext_uitest)).check(matches(withText("SATEHDRA")));


    }


}