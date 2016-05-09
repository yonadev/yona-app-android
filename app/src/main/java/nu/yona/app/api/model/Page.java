/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kinnarvasa on 09/05/16.
 */
public class Page {
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("totalElements")
    @Expose
    private Integer totalElements;
    @SerializedName("totalPages")
    @Expose
    private Integer totalPages;
    @SerializedName("number")
    @Expose
    private Integer number;

    /**
     * @return The size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @param size The size
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @return The totalElements
     */
    public Integer getTotalElements() {
        return totalElements;
    }

    /**
     * @param totalElements The totalElements
     */
    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    /**
     * @return The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages The totalPages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @return The number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * @param number The number
     */
    public void setNumber(Integer number) {
        this.number = number;
    }
}
