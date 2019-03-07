/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.manager.network;

import nu.yona.app.api.model.ActivityCategories;
import nu.yona.app.api.model.AddBuddy;
import nu.yona.app.api.model.AppActivity;
import nu.yona.app.api.model.AppMetaInfo;
import nu.yona.app.api.model.DayActivity;
import nu.yona.app.api.model.EmbeddedYonaActivity;
import nu.yona.app.api.model.Goals;
import nu.yona.app.api.model.Message;
import nu.yona.app.api.model.MessageBody;
import nu.yona.app.api.model.NewDevice;
import nu.yona.app.api.model.NewDeviceRequest;
import nu.yona.app.api.model.OTPVerficationCode;
import nu.yona.app.api.model.PinResetDelay;
import nu.yona.app.api.model.PostBudgetYonaGoal;
import nu.yona.app.api.model.PostTimeZoneYonaGoal;
import nu.yona.app.api.model.ProfilePhoto;
import nu.yona.app.api.model.RegisterUser;
import nu.yona.app.api.model.User;
import nu.yona.app.api.model.WeekActivity;
import nu.yona.app.api.model.YonaBuddies;
import nu.yona.app.api.model.YonaBuddy;
import nu.yona.app.api.model.YonaMessage;
import nu.yona.app.api.model.YonaMessages;
import nu.yona.app.api.model.YonaUser;
import nu.yona.app.utils.ApiList;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by kinnarvasa on 28/03/16.
 */
public interface RestApi
{

	/********
	 * USER
	 *
	 * @param body         the body
	 * @return the call
	 */
	@POST(ApiList.USER)
	Call<User> registerUser(@Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage,
							@Body RegisterUser body);

	@PUT
	Call<User> registerUser(@Url String url, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage,
							@Body RegisterUser body);

	@PUT
	Call<User> updateRegisterUser(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String yonaPassword, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage,
								  @Body RegisterUser body);

	/**
	 * Override register user call.
	 *
	 * @param otp  the otp
	 * @param body the body
	 * @return the call
	 */
	@POST(ApiList.USER)
	Call<User> overrideRegisterUser(@Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage,
									@Query("overwriteUserConfirmationCode") String otp,
									@Body RegisterUser body);

	@PUT()
	@Multipart
	Call<ProfilePhoto> uploadUserPhoto(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String yonaPassword, @Part MultipartBody.Part file);

	/**
	 * Gets user.
	 *
	 * @param url the url
	 * @return the user
	 */
	@GET
	Call<YonaUser> readDeepLinkData(@Url String url, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/**
	 * Update register user call.
	 *
	 * @param url          the url
	 * @param yonaPassword the yona password
	 * @param body         the body
	 * @return the call
	 */

	/**
	 * Gets user.
	 *
	 * @param url          the url
	 * @param yonaPassword the yona password
	 * @return the user
	 */
	@GET
	Call<User> getUser(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String yonaPassword, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

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
								  @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage,
								  @Body OTPVerficationCode code);

	/**
	 * Resend otp call.
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the call
	 */
	@POST
	Call<Void> resendOTP(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/**
	 * Request user override call.
	 *
	 * @param number the number
	 * @return the call
	 */
	@POST(ApiList.ADMIN_OVERRIDE_USER)
	Call<Void> requestUserOverride(@Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage, @Query("mobileNumber") String number);

	/**
	 * Delete user call.
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the call
	 */
	@DELETE
	Call<Void> deleteUser(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/********
	 * RESET PIN
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the call
	 */
	@POST
	Call<PinResetDelay> requestPinReset(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/**
	 * Verify pin call.
	 *
	 * @param url      the url
	 * @param password the password
	 * @param code     the code
	 * @return the call
	 */
	@POST
	Call<Void> verifyPin(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage, @Body OTPVerficationCode code);

	/**
	 * Clear pin call.
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the call
	 */
	@POST
	Call<Void> clearPin(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

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
						 @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage,
						 @Body NewDeviceRequest newDeviceRequest);

	/**
	 * Delete device call.
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the call
	 */
	@DELETE
	Call<Void> deleteDevice(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/**
	 * Check device call.
	 *
	 * @param mobileNumber the mobile number
	 * @param password     the password
	 * @return the call
	 */
	@GET(ApiList.NEW_DEVICE_REQUEST)
	Call<NewDevice> checkDevice(@Path("mobileNumber") String mobileNumber, @Header(NetworkConstant.YONA_NEW_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/********
	 * ActivityCategory
	 *
	 * @return the activity categories
	 */
	@GET(ApiList.ACTIVITY_CATEGORIES)
	Call<ActivityCategories> getActivityCategories(@Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/********
	 * GOALS
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the user goals
	 */
	@GET
	Call<Goals> getUserGoals(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/**
	 * Put user goals call.
	 *
	 * @param url                the url
	 * @param password           the password
	 * @param postBudgetYonaGoal the post budget yona goal
	 * @return the call
	 */
	@POST
	Call<Goals> putUserGoals(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage, @Body PostBudgetYonaGoal postBudgetYonaGoal);

	/**
	 * Put user goals call.
	 *
	 * @param url                  the url
	 * @param password             the password
	 * @param postTimeZoneYonaGoal the post time zone yona goal
	 * @return the call
	 */
	@POST
	Call<Goals> putUserGoals(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage, @Body PostTimeZoneYonaGoal postTimeZoneYonaGoal);

	/**
	 * Delete user goal call.
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the call
	 */
	@DELETE
	Call<Void> deleteUserGoal(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

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
	Call<Goals> updateUserGoal(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage,
							   @Query("message") String message, @Body PostBudgetYonaGoal postBudgetYonaGoal);

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
	Call<Goals> updateUserGoal(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage,
							   @Query("message") String message, @Body PostTimeZoneYonaGoal postTimeZoneYonaGoal);

	/********
	 * FRIENDS / BUDDY
	 *
	 * @param url      the url
	 * @param password the password
	 * @param buddy    the buddy
	 * @return the call
	 */
	@POST
	Call<YonaBuddy> addBuddy(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage,
							 @Body AddBuddy buddy);

	/**
	 * Gets buddy.
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the buddy
	 */
	@GET
	Call<YonaBuddies> getBuddy(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/**
	 * Delete buddy call.
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the call
	 */
	@DELETE
	Call<Void> deleteBuddy(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);


	/********
	 * NOTIFICATION MANAGER
	 *
	 * @param nextUrl      the url
	 * @param password the password
	 * @return the messages
	 */
	@GET
	Call<YonaMessages> getMessages(@Url String nextUrl, @Header(NetworkConstant.YONA_PASSWORD) String password,
								   @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage,
								   @Query("onlyUnreadMessages") boolean isUnreadMessage);

	/**
	 * Delete message call.
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the call
	 */
	@DELETE
	Call<Void> deleteMessage(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);


	/**
	 * Post message call.
	 *
	 * @param url      the url
	 * @param password the password
	 * @param body     the body
	 * @return the call
	 */
	@POST
	Call<Void> postMessage(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage, @Body MessageBody body);

	/********
	 * ACTIVITY  @param url the url
	 *
	 * @param nextActivityUrl      the url
	 * @param password the password
	 * @return the daily activity
	 */
	@GET
	Call<EmbeddedYonaActivity> getActivities(@Url String nextActivityUrl, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/**
	 * Gets day detail activity.
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the day detail activity
	 */
	@GET
	Call<DayActivity> getDayDetailActivity(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/**
	 * Gets week detail activity.
	 *
	 * @param url      the url
	 * @param password the password
	 * @return the week detail activity
	 */
	@GET
	Call<WeekActivity> getWeekDetailActivity(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);

	/********
	 * APP ACTIVITY
	 *
	 * @param url      the url
	 * @param password the password
	 * @param activity the activity
	 * @return the call
	 */
	@POST
	Call<Void> postAppActivity(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage, @Body AppActivity activity);

	/********
	 * APP OPEN EVENT
	 *
	 * @param url      the url
	 * @param password the password
	 * @param appMetaInfo the activity
	 * @return the call
	 */
	@POST
	Call<Void> postOpenAppEvent(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage, @Body AppMetaInfo appMetaInfo);


	/**
	 * Gets with buddy activity.
	 *
	 * @param url      the url
	 * @param password the password
	 * @param size     the size
	 * @param page     the page
	 * @return the with buddy activity
	 */
	@GET
	Call<EmbeddedYonaActivity> getWithBuddyActivity(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage, @Query("size") int size, @Query("page") int page);

	@GET
	Call<EmbeddedYonaActivity> getComments(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage);


	@POST
	Call<YonaMessage> addComment(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage, @Body Message message);

	@POST
	Call<YonaMessage> replyComment(@Url String url, @Header(NetworkConstant.YONA_PASSWORD) String password, @Header(NetworkConstant.ACCEPT_LANGUAGE) String acceptLanguage, @Body MessageBody messageBody);
}
