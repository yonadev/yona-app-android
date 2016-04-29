/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.challenges;

import android.view.View;

/**
 * Created by bhargavsuthar on 21/04/16.
 */
interface OnItemClickListener {
    void onDelete(View v);

    void onClickStartTime(View v);

    void onClickEndTime(View v);
}
