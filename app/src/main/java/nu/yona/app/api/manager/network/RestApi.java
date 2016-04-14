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
import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.api.model.NewDevice;
import nu.yona.app.api.model.NewDeviceRequest;
import nu.yona.app.api.model.OTPVerficationCode;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.utils.ApiList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by kinnarvasa on 28/03/16.
 */
public interface RestApi {

    /********
     * USER
     ************/

    @Headers("Cache-Control: public, max-age=640000, s-maxage=640000 , max-stale=2419200")
    @POST(ApiList.USER)
    Call<User> registerUser(@Header(NetworkConstant.YONA_PASSWORD) String yonaPassword,
                            @Body RegisterUser body);

    @POST
    Call<User> verifyMobileNumber(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password,
                                  @Body OTPVerficationCode code);

    @POST
    Call<Void> resendOTP(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);

    /******** USER ************/

    /********
     * DEVICE
     ************/

    @PUT
    Call<Void> addDevice(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password,
                         @Body NewDeviceRequest newDeviceRequest);

    @DELETE
    Call<Void> deleteDevice(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);

    @GET ("newDeviceRequests/{mobileNumber}")
    Call<NewDevice> checkDevice(@Path("mobileNumber") String mobileNumber, @Header(NetworkConstant.YONA_NEW_PASSWORD) String password);

    /******** DEVICE ************/

    /********
     * ActivityCategory
     ************/

    @GET ("activityCategories/")
    Call<ActivityCategories> getActivityCategories();

    /******** ActivityCategory ************/

    /******** GOALS ************/



    /******** GOALS ************/
}
