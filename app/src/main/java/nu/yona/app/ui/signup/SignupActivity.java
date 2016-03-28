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
import nu.yona.app.api.db.DatabaseHelper;
import nu.yona.app.api.listener.DataLoadListener;
import nu.yona.app.api.manager.SignupManager;
import nu.yona.app.api.manager.impl.SignupManagerImpl;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.LaunchActivity;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class SignupActivity  extends BaseActivity {

    private FrameLayout frameLayout;
    private StepOne stepOne;
    private StepTwo stepTwo;
    private SignupManager signupManager;
    private int SIGNUP_STEP = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        frameLayout = (FrameLayout) findViewById(R.id.fragment_container);

        stepOne = new StepOne();
        stepTwo = new StepTwo();

        signupManager = new SignupManagerImpl(DatabaseHelper.getInstance(this), this);

        loadSteopOne();
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SIGNUP_STEP == 0){
                    loadSteopTwo();
                } else {
                    doRegister();
                }
            }
        });

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
    private void doBack(){
        if(SIGNUP_STEP == 1){
            loadSteopOne();
        }else {
            startActivity(new Intent(SignupActivity.this, LaunchActivity.class));
        }
    }
    private void loadSteopOne(){
        SIGNUP_STEP = 0;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment_container, stepOne);
        fragmentTransaction.commit();
    }

    private void loadSteopTwo(){
        SIGNUP_STEP = 1;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_left);
        fragmentTransaction.replace(R.id.fragment_container, stepTwo);
        fragmentTransaction.commit();
    }

    private void doRegister(){
        signupManager.registerUser("Richard", "Quin", "+3 1612345678", "RQ", new DataLoadListener() {
            @Override
            public void onDataLoad(Object result) {

            }

            @Override
            public void onError(Object errorMessage) {

            }
        });
    }
}
