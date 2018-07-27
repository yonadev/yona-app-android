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
import nu.yona.app.api.manager.AuthenticateManager;
import nu.yona.app.customview.YonaFontEditTextView;
import nu.yona.app.utils.AppConstant;


/**
 * Created by kinnarvasa on 31/03/16.
 */
public class StepOneTest extends YonaTestCase {

    private StepOne stepOneFragment;
    private String name;
    private SignupActivity activity;
    private YonaFontEditTextView firstName, lastName;
    private View view;
    private AuthenticateManager authenticateManager;

    DatabaseHelper dbhelper = DatabaseHelper.getInstance(RuntimeEnvironment.application);

    @Before
    public void setup() {
        String uri = YonaApplication.getAppContext().getString(R.string.server_url);
        Intent intent = new Intent(Intent.ACTION_VIEW).putExtra(AppConstant.DEEP_LINK, uri);
        activity = Robolectric.buildActivity(SignupActivity.class, intent)
                .create()
                .start()
                .resume()
                .get();

        StepOne stepOne = new StepOne();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(stepOne, null);
        fragmentTransaction.commit();
        view = stepOne.getView();
        firstName = (YonaFontEditTextView) view.findViewById(R.id.first_name);
        if(authenticateManager == null){
            authenticateManager =  APIManager.getInstance().getAuthenticateManager().validateText(null)
        }
    }

    @Test
    public void validateFirstName(){
        firstName.setText("Kinnar");
        assertTrue(APIManager.getInstance().getAuthenticateManager().validateText(firstName.getText().toString()));
    }

    @Test
    public void validateEmptyFirstName() {
        firstName.setText("");
        assertFalse(APIManager.getInstance().getAuthenticateManager().validateText(firstName.getText().toString()));
    }

    @Test
    public void validateNullFirstName() {
        firstName.setText("");
        assertNotNull(APIManager.getInstance().getAuthenticateManager().validateText(null));
    }

    @After
    public void tearDown() {
        dbhelper.close();
    }
}
