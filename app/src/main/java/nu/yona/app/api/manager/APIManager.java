/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager;

import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.impl.ActivityCategoryManagerImpl;
import nu.yona.app.api.manager.impl.AuthenticateManagerImpl;
import nu.yona.app.api.manager.impl.BuddyManagerImpl;
import nu.yona.app.api.manager.impl.ChallengesManagerImpl;
import nu.yona.app.api.manager.impl.DeviceManagerImpl;
import nu.yona.app.api.manager.impl.GoalManagerImpl;
import nu.yona.app.api.manager.impl.PasscodeManagerImpl;

/**
 * Created by kinnarvasa on 05/05/16.
 */
public class APIManager {

    private static APIManager apiManager;
    private ActivityCategoryManager activityCategoryManager;
    private AuthenticateManager authenticateManager;
    private BuddyManager buddyManager;
    private DeviceManager deviceManager;
    private GoalManager goalManager;
    private PasscodeManager passcodeManager;
    private ChallengesManager challengesManager;

    public static APIManager getInstance() {
        if (apiManager == null) {
            apiManager = new APIManager();
        }
        return apiManager;
    }

    public ActivityCategoryManager getActivityCategoryManager() {
        if (activityCategoryManager == null) {
            activityCategoryManager = new ActivityCategoryManagerImpl(YonaApplication.getAppContext());
        }
        return activityCategoryManager;
    }

    public AuthenticateManager getAuthenticateManager() {
        if (authenticateManager == null) {
            authenticateManager = new AuthenticateManagerImpl(YonaApplication.getAppContext());
        }
        return authenticateManager;
    }

    public BuddyManager getBuddyManager() {
        if (buddyManager == null) {
            buddyManager = new BuddyManagerImpl(YonaApplication.getAppContext());
        }
        return buddyManager;
    }

    public DeviceManager getDeviceManager() {
        if (deviceManager == null) {
            deviceManager = new DeviceManagerImpl(YonaApplication.getAppContext());
        }
        return deviceManager;
    }

    public GoalManager getGoalManager() {
        if (goalManager == null) {
            goalManager = new GoalManagerImpl(YonaApplication.getAppContext());
        }
        return goalManager;
    }

    public PasscodeManager getPasscodeManager() {
        if (passcodeManager == null) {
            passcodeManager = new PasscodeManagerImpl();
        }
        return passcodeManager;
    }

    public ChallengesManager getChallengesManager() {
        if (challengesManager == null) {
            challengesManager = new ChallengesManagerImpl(YonaApplication.getAppContext());
        }
        return challengesManager;
    }
}
