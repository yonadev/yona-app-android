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

    public static final int EVENT_SIGNUP_STEP_ONE_NEXT = 1;
    public static final int EVENT_SIGNUP_STEP_ONE_ALLOW_NEXT = 2;

    public static final int EVENT_SIGNUP_STEP_TWO_NEXT = 3;
    public static final int EVENT_SIGNUP_STEP_TWO_ALLOW_NEXT = 4;

    public static final int EVENT_PASSCODE_STEP_ONE = 5;
    public static final int EVENT_PASSCODE_STEP_TWO = 6;
    public static final int EVENT_PASSCODE_ERROR = 7;
    public static final int EVENT_PASSCODE_RESET = 8;

    public static final int EVENT_OTP_RESEND = 9;

    public static final int EVENT_TOUR_COMPLETE = 10;

    public static final int EVENT_UPDATE_GOALS = 11;

    private final Set<EventChangeListener> listeners = new HashSet<EventChangeListener>();

    /**
     * @param listener do register listener to listen event changes, (Generally in onCreate())
     */
    public void registerListener(EventChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
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
