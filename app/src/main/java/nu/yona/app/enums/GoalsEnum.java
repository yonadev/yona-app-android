/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhargavsuthar on 15/04/16.
 */
public enum GoalsEnum {
    BUDGET_GOAL("BudgetGoal"),
    TIME_ZONE_GOAL("TimeZoneGoal");

    private static Map<String, GoalsEnum> nameToEnumMapping = new HashMap<String, GoalsEnum>();

    static {
        for (GoalsEnum goalsEnum : GoalsEnum.values()) {
            nameToEnumMapping.put(goalsEnum.actionString, goalsEnum);
        }
    }

    private String actionString;

    GoalsEnum(String actionString) {
        this.actionString = actionString;
    }

    public static GoalsEnum fromName(String actionString) {
        return nameToEnumMapping.get(actionString);
    }

    public String getActionString() {
        return actionString;
    }

}
