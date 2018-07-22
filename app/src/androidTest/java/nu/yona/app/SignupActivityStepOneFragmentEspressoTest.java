package nu.yona.app;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.ui.signup.SignupActivity;
import nu.yona.app.ui.signup.StepOne;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
public class SignupActivityStepOneFragmentEspressoTest {

    StepOne stepOneFragment;

    @Rule
    public ActivityTestRule<SignupActivity> activityTestRule = new ActivityTestRule<>(SignupActivity.class);

    @Before
    public void setup(){

        stepOneFragment = startFragment();
    }

    @Test
    public void clickButton(){
        onView(withId(R.id.first_name)).check(matches(isDisplayed()));
    }

    public  StepOne startFragment() {
        SignupActivity signupActivity = activityTestRule.getActivity();
        android.support.v4.app.FragmentTransaction fragmentTransaction = signupActivity.getSupportFragmentManager().beginTransaction();
        stepOneFragment = new StepOne();
        fragmentTransaction.add(stepOneFragment, "fragment");
        fragmentTransaction.commit();
        return  stepOneFragment;
    }

}
