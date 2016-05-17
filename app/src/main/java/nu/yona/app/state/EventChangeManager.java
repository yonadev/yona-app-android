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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kinnarvasa on 31/03/16.
 */
public class EventChangeManager {

    /**
     * The constant EVENT_SIGNUP_STEP_ONE_NEXT.
     */
    public static final int EVENT_SIGNUP_STEP_ONE_NEXT = 1;
    /**
     * The constant EVENT_SIGNUP_STEP_ONE_ALLOW_NEXT.
     */
    public static final int EVENT_SIGNUP_STEP_ONE_ALLOW_NEXT = 2;

    /**
     * The constant EVENT_SIGNUP_STEP_TWO_NEXT.
     */
    public static final int EVENT_SIGNUP_STEP_TWO_NEXT = 3;
    /**
     * The constant EVENT_SIGNUP_STEP_TWO_ALLOW_NEXT.
     */
    public static final int EVENT_SIGNUP_STEP_TWO_ALLOW_NEXT = 4;

    /**
     * The constant EVENT_PASSCODE_STEP_ONE.
     */
    public static final int EVENT_PASSCODE_STEP_ONE = 5;
    /**
     * The constant EVENT_PASSCODE_STEP_TWO.
     */
    public static final int EVENT_PASSCODE_STEP_TWO = 6;
    /**
     * The constant EVENT_PASSCODE_ERROR.
     */
    public static final int EVENT_PASSCODE_ERROR = 7;
    /**
     * The constant EVENT_PASSCODE_RESET.
     */
    public static final int EVENT_PASSCODE_RESET = 8;

    /**
     * The constant EVENT_OTP_RESEND.
     */
    public static final int EVENT_OTP_RESEND = 9;

    /**
     * The constant EVENT_TOUR_COMPLETE.
     */
    public static final int EVENT_TOUR_COMPLETE = 10;

    /**
     * The constant EVENT_UPDATE_GOALS.
     */
    public static final int EVENT_UPDATE_GOALS = 11;

    /**
     * The constant EVENT_CONTAT_CHOOSED.
     */
    public static final int EVENT_CONTAT_CHOOSED = 12;

    /**
     * The constant EVENT_CLOSE_YONA_ACTIVITY.
     */
    public static final int EVENT_CLOSE_YONA_ACTIVITY = 13;

    /**
     * The constant EVENT_RECEIVED_PHOTO.
     */
    public static final int EVENT_RECEIVED_PHOTO = 14;

    /**
     * The constant EVENT_USER_UPDATE.
     */
    public static final int EVENT_USER_UPDATE = 15;

    public static final int EVENT_USER_NOT_EXIST = 16;

    private final Set<EventChangeListener> listeners = new HashSet<>();

    /**
     * Register listener.
     *
     * @param listener do register listener to listen event changes, (Generally in onCreate())
     */
    public void registerListener(EventChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Un register listener.
     *
     * @param listener do unregister everytime when that screen/ class is no more in use. (Generally in onDestroy())
     */
    public void unRegisterListener(EventChangeListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    /**
     * To clear all registered listener
     */
    public void clearListeners() {
        listeners.clear();
    }

    /**
     * Notify change.
     *
     * @param eventType Eventtype define in EventChangeManager
     * @param object    object to pass with listener from one activity/fragment to another.
     */
    public void notifyChange(int eventType, Object object) {
        for (EventChangeListener listener : listeners) {
            if (listener != null) {
                listener.onStateChange(eventType, object);
            }
        }
    }
}
