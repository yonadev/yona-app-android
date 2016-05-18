/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.frinends;

import android.view.View;

/**
 * Created by bhargavsuthar on 17/05/16.
 */
public interface OnFriendsItemClickListener {

    /**
     * on Item click of Friends
     */
    void onFriendsItemClick(View v);

    /**
     * on Item swipe delete click of friends
     */
    void onFriendsItemDeleteClick(View v);
}
