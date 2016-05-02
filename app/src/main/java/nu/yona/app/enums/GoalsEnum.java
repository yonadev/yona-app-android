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
    /**
     * Budget goal goals enum.
     */
    BUDGET_GOAL("BudgetGoal"),
    /**
     * Time zone goal goals enum.
     */
    TIME_ZONE_GOAL("TimeZoneGoal"),
    /**
     * Nogo goals enum.
     */
    NOGO("NOGO"); // we will never received from server as Type, we need to identify by maxDurationMinutes

    private static final Map<String, GoalsEnum> nameToEnumMapping = new HashMap<>();

    static {
        for (GoalsEnum goalsEnum : GoalsEnum.values()) {
            nameToEnumMapping.put(goalsEnum.actionString, goalsEnum);
        }
    }

    private final String actionString;

    GoalsEnum(String actionString) {
        this.actionString = actionString;
    }

    /**
     * From name goals enum.
     *
     * @param actionString the action string
     * @return the goals enum
     */
    public static GoalsEnum fromName(String actionString) {
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
