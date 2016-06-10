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

import nu.yona.app.api.manager.APIManager;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.User;

/**
 * Created by kinnarvasa on 10/06/16.
 */
public class DataState {

    private User user;
    private EmbeddedYonaActivity embeddedDayActivity;
    private EmbeddedYonaActivity embeddedWeekActivity;

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
     * Gets embedded yona activity.
     *
     * @return the embedded yona activity
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

}
