/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.enums;

/**
 * Created by kinnarvasa on 07/06/16.
 */

public enum ChartTypeEnum {

    TIME_BUCKET_CONTROL("TIME_BUCKET_CONTROL", 1),
    TIME_FRAME_CONTROL("TIME_FRAME_CONTROL", 2),
    NOGO_CONTROL("NOGO_CONTROL", 3),
    WEEK_SCORE_CONTROL("WEEK_SCORE_CONTROL", 4),
    SPREAD_CONTROL("SPREAD_CONTROL", 5),
    BADGE_CONTROL("BADGE_CONTROL", 6),
    ENCOURAGEMENT_CONTROL("ENCORAGEMENT_CONTROL", 7),
    CHAT_CONTROL("CHAT_CONTROL", 8),
    NONE_NONE("NONE_NONE", 9);

    private String chartType;
    private int id;

    ChartTypeEnum(String chartType, int id) {
        this.chartType = chartType;
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getChartType() {
        return this.chartType;
    }

    public static ChartTypeEnum getChartTypeEnum(int id) {
        for (ChartTypeEnum v : values()) {
            if (v.getId() == id) {
                return v;
            }
        }
        return NONE_NONE;
    }
}
