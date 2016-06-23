/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.api.model;

import android.content.ContentValues;

/**
 * Created by bhargavsuthar on 23/06/16.
 */
public class YonaHeaderTheme extends BaseEntity {

    private int header_rightIcon;

    private int header_leftIcon;

    private String header_title;

    private int toolbar;

    private int headercolor;

    private String fetchUrl;

    private boolean isBuddyFlow;

    public boolean isBuddyFlow() {
        return isBuddyFlow;
    }

    public void setBuddyFlow(boolean buddyFlow) {
        isBuddyFlow = buddyFlow;
    }

    public String getFetchUrl() {
        return fetchUrl;
    }

    public void setFetchUrl(String fetchUrl) {
        this.fetchUrl = fetchUrl;
    }

    public int getHeader_rightIcon() {
        return header_rightIcon;
    }

    public void setHeader_rightIcon(int header_rightIcon) {
        this.header_rightIcon = header_rightIcon;
    }

    public int getHeader_leftIcon() {
        return header_leftIcon;
    }

    public void setHeader_leftIcon(int header_leftIcon) {
        this.header_leftIcon = header_leftIcon;
    }

    public String getHeader_title() {
        return header_title;
    }

    public void setHeader_title(String header_title) {
        this.header_title = header_title;
    }

    public int getToolbar() {
        return toolbar;
    }

    public void setToolbar(int toolbar) {
        this.toolbar = toolbar;
    }

    public int getHeadercolor() {
        return headercolor;
    }

    public void setHeadercolor(int headercolor) {
        this.headercolor = headercolor;
    }

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }
}
