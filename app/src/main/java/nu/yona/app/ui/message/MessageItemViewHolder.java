/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.message;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;

/**
 * Created by bhargavsuthar on 10/05/16.
 */
public class MessageItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView img_avtar;
    public ImageView img_status;
    public YonaFontTextView txtTitleMsg, txtFooterMsg;

    public MessageItemViewHolder(View itemView) {
        super(itemView);
        img_avtar = (ImageView) itemView.findViewById(R.id.img_user_icon);
        img_status = (ImageView) itemView.findViewById(R.id.img_status);
        txtTitleMsg = (YonaFontTextView) itemView.findViewById(R.id.txt_title);
        txtFooterMsg = (YonaFontTextView) itemView.findViewById(R.id.txt_footer);
    }
}
