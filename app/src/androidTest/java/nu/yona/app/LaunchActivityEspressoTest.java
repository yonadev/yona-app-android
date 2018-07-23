package nu.yona.app;

import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import nu.yona.app.ui.LaunchActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LaunchActivityEspressoTest {

    private static final long UI_TEST_TIMEOUT = 5 * 1000;
    @Rule
    public ActivityTestRule<LaunchActivity> activityTestRule = new ActivityTestRule<>(LaunchActivity.class);

    @Test
    public void testWhetherDialogOpened(){
        onView(withId(R.id.environmentSwitch)).perform(longClick());
        // @param UI_TEST_TIMEOUT is dependent on number of tests runned, increase it accordingly.
        IdlingPolicies.setMasterPolicyTimeout(UI_TEST_TIMEOUT, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(UI_TEST_TIMEOUT, TimeUnit.MILLISECONDS);
        IdlingResource idlingResource = new IdlingResource() {
            @Override
            public String getName() {
                return IdlingResource.class.getName();
            }
            @Override
            public boolean isIdleNow() {
                return true;
            }
            @Override
            public void registerIdleTransitionCallback(ResourceCallback callback) {
            }
        };
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withText(R.string.environment_alert_title)).check(matches(isDisplayed()));
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

}
