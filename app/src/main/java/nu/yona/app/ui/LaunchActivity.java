/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import nu.yona.app.R;
import nu.yona.app.ui.signup.SignupActivity;

/**
 * Created by kinnarvasa on 25/03/16.
 */
public class LaunchActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_layout);
        findViewById(R.id.join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LaunchActivity.this, SignupActivity.class));
                finish();
            }
        });
    }
}
