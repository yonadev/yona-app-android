/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.listener;

import android.os.AsyncTask;
import android.os.Build;

public abstract class DataLoader extends AsyncTask<Void, Void, Object> {

    @Override
    protected Object doInBackground(Void... params) {
        return doDBCall();
    }

    public abstract void doDBCall();

    public void executeAsync() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            execute();
        }
    }
}

