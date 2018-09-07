/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui.signup;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;


import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.YonaTestCase;
import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.customview.YonaFontNumberTextView;
import nu.yona.app.utils.AppConstant;
import nu.yona.app.utils.MobileNumberFormatter;


/**
 * Created by kinnarvasa on 31/03/16.
 */
public class StepTwoFragmentTest extends YonaTestCase {

    private SignupActivity activity;
    private YonaFontNumberTextView mobileNumber;
    private YonaFontEditTextView nickName;
    private StepTwoFragment stepTwoFragment;
    private View view;

    DatabaseHelper dbhelper = DatabaseHelper.getInstance(RuntimeEnvironment.application);
    @Before
    public void setup() {
        String uri = YonaApplication.getAppContext().getString(R.string.server_url);
        Intent intent = new Intent(Intent.ACTION_VIEW).putExtra(AppConstant.DEEP_LINK, uri);
        activity = Robolectric.buildActivity(SignupActivity.class, intent)
                .create()
                .start()
                .visible()
                .get();
        stepTwoFragment = new StepTwoFragment();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(stepTwoFragment, null);
        fragmentTransaction.commit();
        view = stepTwoFragment.getView();
        mobileNumber = view.findViewById(R.id.mobileNumber);
        nickName = view.findViewById(R.id.nick_name);
    }

    @Test
    public void validateMobileNumber() {

        mobileNumber.setText(MobileNumberFormatter.format("+31 (0) 123456789"));
        assertTrue(APIManager.getInstance().getAuthenticateManager().isMobileNumberValid(mobileNumber.getText().toString()));

        mobileNumber.setText(MobileNumberFormatter.format("+31123456789"));
        assertTrue(APIManager.getInstance().getAuthenticateManager().isMobileNumberValid(mobileNumber.getText().toString()));

        mobileNumber.setText("+311234567ddddd89");
        assertFalse(APIManager.getInstance().getAuthenticateManager().isMobileNumberValid(mobileNumber.getText().toString()));

        mobileNumber.setText(MobileNumberFormatter.format("+919686270640"));
        assertTrue(APIManager.getInstance().getAuthenticateManager().isMobileNumberValid(mobileNumber.getText().toString()));

        mobileNumber.setText(null);
        assertNotNull("Null mobile number",APIManager.getInstance().getAuthenticateManager().isMobileNumberValid(mobileNumber.getText().toString()));
    }

    @After
    public void tearDown() {
        dbhelper.close();
    }

}
