/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.tour;

import android.content.Intent;
import android.os.Bundle;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.state.EventChangeListener;
import nu.yona.app.state.EventChangeManager;
import nu.yona.app.ui.BaseActivity;
import nu.yona.app.ui.LaunchActivity;
import nu.yona.app.utils.PreferenceConstant;

public class TourActivity extends BaseActivity implements EventChangeListener {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.tour_activity);
        YonaApplication.getEventChangeManager().registerListener(this);
    }

    @Override
    public void onBackPressed() {
        //do nothing on back pressed, as we are not allowing to go back to user here.
    }

    @Override
    public void onDestroy() {
        YonaApplication.getEventChangeManager().unRegisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onStateChange(int eventType, Object object) {
        switch (eventType) {
            case EventChangeManager.EVENT_TOUR_COMPLETE:
                moveToLaunchActivity();
                break;
            default:
                break;
        }
    }

    private void moveToLaunchActivity() {
        YonaApplication.getUserPreferences().edit().putBoolean(PreferenceConstant.STEP_TOUR, true).commit();
        startActivity(new Intent(this, LaunchActivity.class));
        finish();
    }
}
