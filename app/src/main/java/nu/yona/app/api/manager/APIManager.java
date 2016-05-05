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

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static APIManager getInstance() {
        if (apiManager == null) {
            apiManager = new APIManager();
        }
        return apiManager;
    }

    /**
     * Gets activity category manager.
     *
     * @return the activity category manager
     */
    public ActivityCategoryManager getActivityCategoryManager() {
        if (activityCategoryManager == null) {
            activityCategoryManager = new ActivityCategoryManagerImpl(YonaApplication.getAppContext());
        }
        return activityCategoryManager;
    }

    /**
     * Gets authenticate manager.
     *
     * @return the authenticate manager
     */
    public AuthenticateManager getAuthenticateManager() {
        if (authenticateManager == null) {
            authenticateManager = new AuthenticateManagerImpl(YonaApplication.getAppContext());
        }
        return authenticateManager;
    }

    /**
     * Gets buddy manager.
     *
     * @return the buddy manager
     */
    public BuddyManager getBuddyManager() {
        if (buddyManager == null) {
            buddyManager = new BuddyManagerImpl(YonaApplication.getAppContext());
        }
        return buddyManager;
    }

    /**
     * Gets device manager.
     *
     * @return the device manager
     */
    public DeviceManager getDeviceManager() {
        if (deviceManager == null) {
            deviceManager = new DeviceManagerImpl(YonaApplication.getAppContext());
        }
        return deviceManager;
    }

    /**
     * Gets goal manager.
     *
     * @return the goal manager
     */
    public GoalManager getGoalManager() {
        if (goalManager == null) {
            goalManager = new GoalManagerImpl(YonaApplication.getAppContext());
        }
        return goalManager;
    }

    /**
     * Gets passcode manager.
     *
     * @return the passcode manager
     */
    public PasscodeManager getPasscodeManager() {
        if (passcodeManager == null) {
            passcodeManager = new PasscodeManagerImpl();
        }
        return passcodeManager;
    }

    /**
     * Gets challenges manager.
     *
     * @return the challenges manager
     */
    public ChallengesManager getChallengesManager() {
        if (challengesManager == null) {
            challengesManager = new ChallengesManagerImpl();
        }
        return challengesManager;
    }
}
