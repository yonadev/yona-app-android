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
    ACTION_DASHBOARD("nu.yona.app.action.DASHBOARD"),
    ACTION_FRIENDS("nu.yona.app.action.FRIENDS"),
    ACTION_CHALLENGES("nu.yona.app.action.CHALLENGES"),
    ACTION_SETTINGS("nu.yona.app.action.SETTINGS"),
    ACTION_PROFILE("nu.yona.app.action.PROFILE"),
    ACTION_MESSAGE("nu.yona.app.action.MESSAGE"),
    ACTION_CHALLENGES_GOAL("nu.yona.app.action.CHALLENGES_GOAL");

    private static Map<String, IntentEnum> nameToEnumMapping = new HashMap<String, IntentEnum>();

    static {
        for (IntentEnum intentEnum : IntentEnum.values()) {
            nameToEnumMapping.put(intentEnum.actionString, intentEnum);
        }
    }

    private String actionString;

    IntentEnum(String actionString) {
        this.actionString = actionString;
    }

    public static IntentEnum fromName(String actionString) {
        return nameToEnumMapping.get(actionString);
    }

    public String getActionString() {
        return actionString;
    }
}
