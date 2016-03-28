/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.manager.network;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import nu.yona.app.R;
import nu.yona.app.api.listener.DataLoadListener;
import nu.yona.app.api.model.User;
import nu.yona.app.api.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kinnarvasa on 28/03/16.
 */
public class SignupNetworkImpl extends BaseImpl implements Callback<User>{

    private Context mContext;
    private String baseUrl;

    public SignupNetworkImpl(String baseUrl, Context mContext) {
        this.mContext = mContext;
        this.baseUrl = baseUrl;
    }

    public void registerUser(JSONObject object, DataLoadListener listener){
        if(!NetworkUtils.isOnline(mContext)){
            listener.onError(mContext.getString(R.string.connection_not_available));
            return;
        }
        Log.e("Register", "Call register user");
        getRestApi().registerUser("R I C H A R D", object).enqueue(this);
    }

    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        Log.e("Response", response.body().getFirstName());
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        Log.e("Response", t.getMessage());
    }
}
