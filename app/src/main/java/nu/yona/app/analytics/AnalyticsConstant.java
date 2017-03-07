/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.analytics;

/**
 * Created by kinnarvasa on 02/09/16.
 */

public interface AnalyticsConstant {
    String APP_KEY = "UA-83625831-1";

    /**
     * String Keys for Screens
     **/
    String SCREEN_BASE_FRAGMENT = "Undefined";
    String SCREEN_PRIVACY = "Privacy";
    String SCREEN_SETTINGS = "Settings";
    String SCREEN_PROFILE = "ProfileScreen";
    String SCREEN_EDIT_PROFILE = "EditProfile";
    String CHALLENGES_SCREEN = "Challenges";
    String BUDGET_GOAL_SCREEN = "Credit";
    String TIMEZONE_GOAL_SCREEN = "TimeZone";
    String NOGO_GOAL_SCREEN = "NoGo";
    String PASSCODE_SCREEN = "PasscodeScreen";
    String REENTER_PASSCODE_SCREEN = "ReEnterPasscodeScreen";
    String VERIFY_PIN_BEFORE_RESET = "VerifyPinBeforeResetScreen";
    String REGISTRATION_STEP_ONE = "RegistrationStepOneScreen";
    String REGISTRATION_STEP_TWO = "RegistrationStepTwoScreen";
    String FIRST_PASSCODE_SCREEN = "NewPasscodeScreen";
    String USER_BLOCK_VIEW = "UserBlockScreen";
    String TIMER_VIEW = "TimerView";
    String SECOND_PASSCODE_SCREEN = "ReEnterNewPasscodeScreen";
    String LOGIN_PASSCODE_SCREEN = "LoginPasscodeScreen";
    String VERIFY_PASSCODE_SCREEN = "VerfiyPasscodeScreen";
    String LOGIN_SCREEN = "LoginScreen";
    String OTP_SCREEN = "OtpScreen";
    String DASHBOARD_SCREEN = "DashboardView";
    String ADD_GOAL = "Add Goal";
    String CHALLENGES_GOAL_DETAIL = "ChallengesGoalDetail";
    String FRIENDS_SCREEN = "FriendsTimelineScreen";
    String ADD_FRIEND = "AddFriend";
    String DAY_ACTIVITY_DETAIL_SCREEN = "DayActivityDetailScreen";
    String ADMIN_MESSAGE_SCREEN = "AdminMessageScreen";
    String WEEK_ACTIVITY_DETAIL_SCREEN = "WeekActivityDetailScreen";
    String NEXT = "Next";
    String PREVIOUS = "Previous";
    String SEND = "SendComment";
    String NOTIFICATION = "Notification";
    String SAVE = "Save";
    String DELETE_GOAL = "DeleteGoal";
    String SAVE_GOAL = "SaveGoal";
    String FRIEND_REQUEST_SCREEN = "FriendRequest";
    String FRIEND_TIMELINE = "FriendTimeline";
    String LAUNCH_ACTIVITY = "LaunchActivity";
    String WELCOME_CARROUSEL = "WelcomeCarrousel";
    /**
     * String Keys for back pressed
     **/
    String BACK_FROM_SCREEN_PRIVACY = "Privacy - Back Pressed";
    String BACK_FROM_SCREEN_SETTINGS = "Settings - Back Pressed";
    String BACK_FROM_CHALLENGES_SCREEN = "Challenges - Back Pressed";
    String BACK_FROM_PROFILE_SCREEN = "Profile - Back Pressed";
    String BACK_FROM_EDIT_PROFILE = "EditProfile - Back Pressed";
    String BACK_FROM_PASSCODE_SCREEN = "Passcode = Back Pressed";
    String BACK_FROM_REGISTRATION_STEP_ONE = "RegistrationStepOneScreen - Back Pressed";
    String BACK_FROM_REGISTRATION_STEP_TWO = "RegistrationStepTwoScreen - Back Pressed";
    String BACK_FROM_LOGIN_SCREEN = "LoginScreen - Back Pressed";
    String BACK_FROM_OTP_SCREEN = "OtpScreen - Back Pressed";
    String BACK_FROM_CHALLENGES_GOAL_DETAIL = "ChallengesGoalDetail - Back Pressed";
    String BACK_FROM_DAY_ACTIVITY_DETAIL_SCREEN = "DayActivityDetailScreen - Back Pressed";
    String BACK_FROM_WEEK_ACTIVITY_DETAIL_SCREEN = "WeekActivityDetailScreen - Back Pressed";
    String BACK_FROM_DASHBOARD = "Dashboard - Back Pressed";
    String BACK_FROM_NOTIFICATION = "Notification - Back Pressed";
    String BACK_FROM_FRIEND_REQUEST = "FriendRequest - Back Pressed";
}
