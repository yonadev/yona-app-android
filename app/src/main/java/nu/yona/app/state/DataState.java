/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.state;

import android.text.TextUtils;

import nu.yona.app.R;
import nu.yona.app.YonaApplication;
import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.ui.BaseFragment;
import nu.yona.app.ui.dashboard.DayActivityDetailFragment;
import nu.yona.app.utils.AppConstant;

/**
 * Created by kinnarvasa on 10/06/16.
 */
public class DataState {

    private User user;
    private EmbeddedYonaActivity embeddedDayActivity;
    private EmbeddedYonaActivity embeddedWeekActivity;
    private EmbeddedYonaActivity embeddedWithBuddyActivity;
    private RegisterUser registerUser;
    private int notificaitonCount;

    /**
     * Gets user.
     *
     * @return the user
     */
    public User getUser() {
        if (user == null) {
            user = APIManager.getInstance().getAuthenticateManager().getUser();
        }
        return user;
    }

    /**
     * Sets user.
     *
     * @param user the user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Update user user.
     *
     * @return the user
     */
    public User updateUser() {
        user = APIManager.getInstance().getAuthenticateManager().getUser();
        return user;
    }

    /**
     * Gets embedded day activity.
     *
     * @return the embedded day activity
     */
    public EmbeddedYonaActivity getEmbeddedDayActivity() {
        return this.embeddedDayActivity;
    }

    /**
     * Sets embedded yona activity.
     *
     * @param embeddedDayActivity the embedded yona activity
     */
    public void setEmbeddedDayActivity(EmbeddedYonaActivity embeddedDayActivity) {
        this.embeddedDayActivity = embeddedDayActivity;
    }


    /**
     * Gets embedded week activity.
     *
     * @return the embedded week activity
     */
    public EmbeddedYonaActivity getEmbeddedWeekActivity() {
        return this.embeddedWeekActivity;
    }

    /**
     * Sets embedded week activity.
     *
     * @param embeddedWeekActivity the embedded week activity
     */
    public void setEmbeddedWeekActivity(EmbeddedYonaActivity embeddedWeekActivity) {
        this.embeddedWeekActivity = embeddedWeekActivity;
    }

    /**
     * Clear activity list.
     *
     * @param fragment the fragment
     */
    public void clearActivityList(BaseFragment fragment) {
        if (!(fragment instanceof DayActivityDetailFragment)) {
            embeddedDayActivity = null;
            embeddedWeekActivity = null;
        }
    }

    /**
     * Gets embedded with buddy activity.
     *
     * @return the embedded with buddy activity
     */
    public EmbeddedYonaActivity getEmbeddedWithBuddyActivity() {
        return this.embeddedWithBuddyActivity;
    }

    /**
     * Sets embedded with buddy activity.
     *
     * @param embeddedWithBuddyActivity the embedded with buddy activity
     */
    public void setEmbeddedWithBuddyActivity(EmbeddedYonaActivity embeddedWithBuddyActivity) {
        this.embeddedWithBuddyActivity = embeddedWithBuddyActivity;
    }

    /**
     * Gets server url.
     *
     * @return the server url
     */
    public String getServerUrl() {
        if (TextUtils.isEmpty(YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getString(AppConstant.SERVER_URL, YonaApplication.getAppContext().getString(R.string.blank)))) {
            YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putString(AppConstant.SERVER_URL, YonaApplication.getAppContext().getString(R.string.server_url)).commit();
        }
        return YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().getString(AppConstant.SERVER_URL, YonaApplication.getAppContext().getString(R.string.server_url));
    }

    /**
     * Sets server url.
     *
     * @param serverUrl the server url
     */
    public void setServerUrl(String serverUrl) {
        YonaApplication.getEventChangeManager().getSharedPreference().getUserPreferences().edit().putString(AppConstant.SERVER_URL, serverUrl).commit();
    }

    public RegisterUser getRegisterUser() {
        if (registerUser == null) {
            registerUser = new RegisterUser();
        }
        return registerUser;
    }

    public void setRegisterUser(RegisterUser registerUser) {
        this.registerUser = registerUser;
    }

    public int getNotificaitonCount() {
        return notificaitonCount;
    }

    public void setNotificaitonCount(int notificaitonCount) {
        this.notificaitonCount = notificaitonCount;
    }

}
