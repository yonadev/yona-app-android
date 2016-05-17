/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.utils;

/**
 * Created by kinnarvasa on 21/04/16.
 */
public interface ServerErrorCode {
    /**
     * The constant USER_EXIST_ERROR.
     */
    String USER_EXIST_ERROR = "error.user.exists";

    /**
     * The constant ADD_BUDDY_USER_EXIST_ERROR.
     */
    String ADD_BUDDY_USER_EXIST_ERROR = "error.user.exists.created.on.buddy.request";
    /**
     * The constant USER_NOT_FOUND.
     * As per comment on: http://wiki.yona.nu/display/DEV/Flow+-+Unsubscribe?focusedCommentId=14843985#comment-14843985
     */
    String USER_NOT_FOUND = "error.user.not.found.id";
}
