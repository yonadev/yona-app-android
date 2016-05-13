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
import nu.yona.app.api.model.AddBuddy;
import nu.yona.app.api.model.Buddy;
import nu.yona.app.api.model.Goals;
import nu.yona.app.api.model.MessageBody;
import nu.yona.app.api.model.NewDevice;
import nu.yona.app.api.model.NewDeviceRequest;
import nu.yona.app.api.model.OTPVerficationCode;
import nu.yona.app.api.model.PinResetDelay;
import nu.yona.app.api.model.PostBudgetYonaGoal;
import nu.yona.app.api.model.PostTimeZoneYonaGoal;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaMessages;
import nu.yona.app.utils.ApiList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by kinnarvasa on 28/03/16.
 */
public interface RestApi {

    /********
     * USER
     *
     * @param yonaPassword the yona password
     * @param body         the body
     * @return the call
     */
    @POST(ApiList.USER)
    Call<User> registerUser(@Header(NetworkConstant.YONA_PASSWORD) String yonaPassword,
                            @Body RegisterUser body);

    /**
     * Update register user call.
     *
     * @param url          the url
     * @param yonaPassword the yona password
     * @param body         the body
     * @return the call
     */
    @PUT
    Call<User> updateRegisterUser(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String yonaPassword,
                                  @Body RegisterUser body);

    /**
     * Override register user call.
     *
     * @param yonaPassword the yona password
     * @param otp          the otp
     * @param body         the body
     * @return the call
     */
    @POST(ApiList.USER)
    Call<User> overrideRegisterUser(@Header(NetworkConstant.YONA_PASSWORD) String yonaPassword, @Query("overwriteUserConfirmationCode") String otp,
                                    @Body RegisterUser body);

    /**
     * Gets user.
     *
     * @param url          the url
     * @param yonaPassword the yona password
     * @return the user
     */
    @GET
    Call<User> getUser(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String yonaPassword);

    /**
     * Verify mobile number call.
     *
     * @param url      the url
     * @param password the password
     * @param code     the code
     * @return the call
     */
    @POST
    Call<User> verifyMobileNumber(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password,
                                  @Body OTPVerficationCode code);

    /**
     * Resend otp call.
     *
     * @param url      the url
     * @param password the password
     * @return the call
     */
    @POST
    Call<Void> resendOTP(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);

    /**
     * Request user override call.
     *
     * @param number the number
     * @return the call
     */
    @POST(ApiList.ADMIN_OVERRIDE_USER)
    Call<Void> requestUserOverride(@Query("mobileNumber") String number);

    /**
     * Delete user call.
     *
     * @param url      the url
     * @param password the password
     * @return the call
     */
    @DELETE
    Call<Void> deleteUser(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);

    /******** USER ************/

    /********
     * RESET PIN
     *
     * @param url      the url
     * @param password the password
     * @return the call
     */
    @POST
    Call<PinResetDelay> requestPinReset(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);

    /**
     * Verify pin call.
     *
     * @param url      the url
     * @param password the password
     * @param code     the code
     * @return the call
     */
    @POST
    Call<Void> verifyPin(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Body OTPVerficationCode code);

    /**
     * Clear pin call.
     *
     * @param url      the url
     * @param password the password
     * @return the call
     */
    @POST
    Call<Void> clearPin(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);

    /******** RESET PIN ************/

    /********
     * DEVICE
     *
     * @param url              the url
     * @param password         the password
     * @param newDeviceRequest the new device request
     * @return the call
     */
    @PUT
    Call<Void> addDevice(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password,
                         @Body NewDeviceRequest newDeviceRequest);

    /**
     * Delete device call.
     *
     * @param url      the url
     * @param password the password
     * @return the call
     */
    @DELETE
    Call<Void> deleteDevice(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);

    /**
     * Check device call.
     *
     * @param mobileNumber the mobile number
     * @param password     the password
     * @return the call
     */
    @GET(ApiList.NEW_DEVICE_REQUEST)
    Call<NewDevice> checkDevice(@Path("mobileNumber") String mobileNumber, @Header(NetworkConstant.YONA_NEW_PASSWORD) String password);

    /******** DEVICE ************/

    /********
     * ActivityCategory
     *
     * @return the activity categories
     */
    @GET(ApiList.ACTIVITY_CATEGORIES)
    Call<ActivityCategories> getActivityCategories();

    /******** ActivityCategory ************/

    /********
     * GOALS
     *
     * @param url      the url
     * @param password the password
     * @return the user goals
     */
    @GET
    Call<Goals> getUserGoals(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);

    /**
     * Put user goals call.
     *
     * @param url                the url
     * @param password           the password
     * @param postBudgetYonaGoal the post budget yona goal
     * @return the call
     */
    @POST
    Call<Goals> putUserGoals(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Body PostBudgetYonaGoal postBudgetYonaGoal);

    /**
     * Put user goals call.
     *
     * @param url                  the url
     * @param password             the password
     * @param postTimeZoneYonaGoal the post time zone yona goal
     * @return the call
     */
    @POST
    Call<Goals> putUserGoals(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Body PostTimeZoneYonaGoal postTimeZoneYonaGoal);

    /**
     * Delete user goal call.
     *
     * @param url      the url
     * @param password the password
     * @return the call
     */
    @DELETE
    Call<Void> deleteUserGoal(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);

    /**
     * Update user goal call.
     *
     * @param url                the url
     * @param password           the password
     * @param message            the message
     * @param postBudgetYonaGoal the post budget yona goal
     * @return the call
     */
    @PUT
    Call<Goals> updateUserGoal(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Query("message") String message, @Body PostBudgetYonaGoal postBudgetYonaGoal);

    /**
     * Update user goal call.
     *
     * @param url                  the url
     * @param password             the password
     * @param message              the message
     * @param postTimeZoneYonaGoal the post time zone yona goal
     * @return the call
     */
    @PUT
    Call<Goals> updateUserGoal(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Query("message") String message, @Body PostTimeZoneYonaGoal postTimeZoneYonaGoal);

    /******** GOALS ************/

    /********
     * FRIENDS / BUDDY
     *
     * @param url      the url
     * @param password the password
     * @param buddy    the buddy
     * @return the call
     */
    @POST
    Call<YonaBuddy> addBuddy(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Body AddBuddy buddy);

    /**
     * Gets buddy.
     *
     * @param url      the url
     * @param password the password
     * @return the buddy
     */
    @GET
    Call<Buddy> getBuddy(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);

    /**
     * Delete buddy call.
     *
     * @param url      the url
     * @param password the password
     * @return the call
     */
    @DELETE
    Call<Void> deleteBuddy(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);

    /******** FRIENDS / BUDDY ************/

    /********
     * NOTIFICATION MANAGER
     *
     * @param url      the url
     * @param password the password
     * @param size     the size
     * @param page     the page
     * @return the messages
     */
    @GET
    Call<YonaMessages> getMessages(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password,
                                   @Query("size") int size, @Query("page") int page);

    /**
     * Delete message call.
     *
     * @param url      the url
     * @param password the password
     * @return the call
     */
    @DELETE
    Call<Void> deleteMessage(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password);


    /**
     * Post message call.
     *
     * @param url      the url
     * @param password the password
     * @param body     the body
     * @return the call
     */
    @POST
    Call<Void> postMessage(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Body MessageBody body);
    /******** NOTIFICATION MANAGER ************/
}
