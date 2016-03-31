/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.manager.SignupManager;
import nu.yona.app.api.manager.impl.SignupManagerImpl;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.customview.YonaFontButton;
import nu.yona.app.listener.DataLoadListener;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.LaunchActivity;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class SignupActivity extends BaseActivity implements EventChangeListener{

    private FrameLayout frameLayout;
    private StepOne stepOne;
    private StepTwo stepTwo;
    private SignupManager signupManager;
    private int SIGNUP_STEP = 0;
    private RegisterUser registerUser;
    private YonaFontButton nextButton, prevButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        frameLayout = (FrameLayout) findViewById(R.id.fragment_container);

        stepOne = new StepOne();
        stepTwo = new StepTwo();
        registerUser = new RegisterUser();

        signupManager = new SignupManagerImpl(DatabaseHelper.getInstance(this), this);

        nextButton = (YonaFontButton) findViewById(R.id.next);
        prevButton = (YonaFontButton) findViewById(R.id.previous);

        loadSteopOne();


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SIGNUP_STEP == 0) {
                    YonaApplication.getEventChangeManager().notifyChange(EventChangeManager.EVENT_SIGNUP_STEP_ONE_NEXT, null);
                } else {
                    doRegister();
                }
            }
        });
        YonaApplication.getEventChangeManager().registerListener(this);
        findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBack();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        doBack();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YonaApplication.getEventChangeManager().unRegisterListener(this);
    }

    private void doBack() {
        if (SIGNUP_STEP == 1) {
            loadSteopOne();
        } else {
            startActivity(new Intent(SignupActivity.this, LaunchActivity.class));
        }
    }

    private void loadSteopOne() {
        SIGNUP_STEP = 0;
        prevButton.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_container, stepOne);
        fragmentTransaction.commit();
    }

    private void loadSteopTwo() {
        SIGNUP_STEP = 1;
        prevButton.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_left);
        fragmentTransaction.replace(R.id.fragment_container, stepTwo);
        fragmentTransaction.commit();
    }

    private void doRegister() {
        signupManager.registerUser(getRegisterUser(), new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {

            }

            @Override
            public void onError(Object errorMessage) {

            }
        });
    }

    public RegisterUser getRegisterUser() {
        return registerUser;
    }

    public SignupManager getSignupManager() {
        return signupManager;
    }


    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType){
            case EventChangeManager.EVENT_SIGNUP_STEP_ONE_ALLOW_NEXT:
                loadSteopTwo();
                break;
            default:
                break;
        }
    }
}
