/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;

/**
 * Created by bhargavsuthar on 25/07/16.
 */
public class StickyHeaderHolder extends RecyclerView.ViewHolder
{

	private YonaFontTextView headerText;

	public StickyHeaderHolder(View itemView)
	{
		super(itemView);
		headerText = (YonaFontTextView) itemView.findViewById(R.id.msg_header);
	}

	public YonaFontTextView getHeaderText()
	{
		return headerText;
	}

	public void setHeaderText(YonaFontTextView headerText)
	{
		this.headerText = headerText;
	}
}
