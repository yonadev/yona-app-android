/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kinnarvasa on 21/03/16.
 */
public enum IntentEnum {
    /**
     * Action dashboard intent enum.
     */
    ACTION_DASHBOARD("nu.yona.app.action.DASHBOARD"),
    /**
     * Action friends intent enum.
     */
    ACTION_FRIENDS("nu.yona.app.action.FRIENDS"),
    /**
     * Action challenges intent enum.
     */
    ACTION_CHALLENGES("nu.yona.app.action.CHALLENGES"),
    /**
     * Action settings intent enum.
     */
    ACTION_SETTINGS("nu.yona.app.action.SETTINGS"),
    /**
     * Action profile intent enum.
     */
    ACTION_PROFILE("nu.yona.app.action.PROFILE"),

    /**
     * Action friend profile intent enum.
     */
    ACTION_FRIEND_PROFILE("nu.yona.app.action.FRIEND_PROFILE"),

    /**
     * Action edit profile intent enum.
     */
    ACTION_EDIT_PROFILE("nu.yona.app.action.EDIT_PROFILE"),
    /**
     * Action message intent enum.
     */
    ACTION_MESSAGE("nu.yona.app.action.MESSAGE"),
    /**
     * Action challenges goal intent enum.
     */
    ACTION_CHALLENGES_GOAL("nu.yona.app.action.CHALLENGES_GOAL"),
    /**
     * Action add friend intent enum.
     */
    ACTION_ADD_FRIEND("nu.yona.app.action.ADD_FRIEND"),

    /**
     * Action privacy policy intent enum.
     */
    ACTION_PRIVACY_POLICY("nu.yona.app.action.PRIVACY_POLICY"),
    /**
     * Action Friend Request intent enum.
     */
    ACTION_FRIEND_REQUEST("nu.yona.app.action.FRIEND_REQUEST"),

    /**
     * Action activity detail view intent enum.
     */
    ACTION_ACTIVITY_DETAIL_VIEW("nul.yona.app.action.ACTIVITY_DETAIL"),

    /**
     * Action week detail view intent enum.
     */
    ACTION_WEEK_DETAIL_VIEW("nu.yona.app.action.WEEK_DETAIL");

    private static final Map<String, IntentEnum> nameToEnumMapping = new HashMap<>();

    static {
        for (IntentEnum intentEnum : IntentEnum.values()) {
            nameToEnumMapping.put(intentEnum.actionString, intentEnum);
        }
    }

    private final String actionString;

    IntentEnum(String actionString) {
        this.actionString = actionString;
    }

    /**
     * From name intent enum.
     *
     * @param actionString the action string
     * @return the intent enum
     */
    public static IntentEnum fromName(String actionString) {
        return nameToEnumMapping.get(actionString);
    }

    /**
     * Gets action string.
     *
     * @return the action string
     */
    public String getActionString() {
        return actionString;
    }
}
