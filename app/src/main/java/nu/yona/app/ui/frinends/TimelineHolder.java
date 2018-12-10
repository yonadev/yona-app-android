/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.frinends;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;
import nu.yona.app.customview.graph.TimeBucketGraph;
import nu.yona.app.customview.graph.TimeFrameGraph;
import nu.yona.app.enums.ChartTypeEnum;

/**
 * Created by bhargavsuthar on 28/06/16.
 */
public class TimelineHolder extends RecyclerView.ViewHolder
{

	private YonaFontTextView mHeaderCategoryTypeGoal;
	private YonaFontTextView mHeaderCategoryGoalTime;
	private ImageView mUserIcon;
	private TimeBucketGraph mTimebucketGraph;
	private TimeFrameGraph mTimeFrameGraph;
	private ImageView mNogoImage;
	private YonaFontTextView mTxtNogo;
	private YonaFontTextView mTxtNogoTime;
	private YonaFontTextView profileImageTxt;
	private View view;

	public TimelineHolder(View itemView, View.OnClickListener listener, ChartTypeEnum chartTypeEnum)
	{
		super(itemView);
		this.view = itemView;
		itemView.setOnClickListener(listener);
		initHeaderControl();
		switch (chartTypeEnum)
		{
			case NOGO_CONTROL:
				initNoGOControlView();
				break;
			case TIME_BUCKET_CONTROL:
				initTimeBucketControlView();
				break;
			case TIME_FRAME_CONTROL:
				initTimeFrameControlView();
				break;
			default:
				break;
		}
	}

	private void initHeaderControl()
	{
		mHeaderCategoryTypeGoal = (YonaFontTextView) view.findViewById(R.id.txtHeaderSection);
		mHeaderCategoryGoalTime = (YonaFontTextView) view.findViewById(R.id.txtHeaderMinuten);
	}

	private void initNoGOControlView()
	{
		mTxtNogo = (YonaFontTextView) view.findViewById(R.id.txtNoGoText);
		mTxtNogoTime = (YonaFontTextView) view.findViewById(R.id.txtNogoTime);
		mNogoImage = (ImageView) view.findViewById(R.id.img_nogo_user_icon);
	}

	private void initTimeBucketControlView()
	{
		mTimebucketGraph = (TimeBucketGraph) view.findViewById(R.id.timeBucketGraph);
		mUserIcon = (ImageView) view.findViewById(R.id.img_user_icon);
		profileImageTxt = (YonaFontTextView) view.findViewById(R.id.profileTextIcon);

	}

	private void initTimeFrameControlView()
	{
		mTimeFrameGraph = (TimeFrameGraph) view.findViewById(R.id.timeFrameControl);
		mUserIcon = (ImageView) view.findViewById(R.id.img_user_icon);
		profileImageTxt = (YonaFontTextView) view.findViewById(R.id.profileTextIcon);
	}


	public YonaFontTextView getmHeaderCategoryTypeGoal()
	{
		return mHeaderCategoryTypeGoal;
	}

	public void setmHeaderCategoryTypeGoal(YonaFontTextView mHeaderCategoryTypeGoal)
	{
		this.mHeaderCategoryTypeGoal = mHeaderCategoryTypeGoal;
	}

	public YonaFontTextView getmHeaderCategoryGoalTime()
	{
		return mHeaderCategoryGoalTime;
	}

	public void setmHeaderCategoryGoalTime(YonaFontTextView mHeaderCategoryGoalTime)
	{
		this.mHeaderCategoryGoalTime = mHeaderCategoryGoalTime;
	}

	public ImageView getmUserIcon()
	{
		return mUserIcon;
	}

	public void setmUserIcon(ImageView mUserIcon)
	{
		this.mUserIcon = mUserIcon;
	}

	public TimeBucketGraph getmTimebucketGraph()
	{
		return mTimebucketGraph;
	}

	public void setmTimebucketGraph(TimeBucketGraph mTimebucketGraph)
	{
		this.mTimebucketGraph = mTimebucketGraph;
	}

	public TimeFrameGraph getmTimeFrameGraph()
	{
		return mTimeFrameGraph;
	}

	public void setmTimeFrameGraph(TimeFrameGraph mTimeFrameGraph)
	{
		this.mTimeFrameGraph = mTimeFrameGraph;
	}

	public ImageView getmNogoImage()
	{
		return mNogoImage;
	}

	public void setmNogoImage(ImageView mNogoImage)
	{
		this.mNogoImage = mNogoImage;
	}

	public YonaFontTextView getmTxtNogo()
	{
		return mTxtNogo;
	}

	public void setmTxtNogo(YonaFontTextView mTxtNogo)
	{
		this.mTxtNogo = mTxtNogo;
	}

	public YonaFontTextView getmTxtNogoTime()
	{
		return mTxtNogoTime;
	}

	public void setmTxtNogoTime(YonaFontTextView mTxtNogoTime)
	{
		this.mTxtNogoTime = mTxtNogoTime;
	}

	public View getView()
	{
		return view;
	}

	public void setView(View view)
	{
		this.view = view;
	}

	public YonaFontTextView getProfileImageTxt()
	{
		return this.profileImageTxt;
	}

	public void setProfileImageTxt(YonaFontTextView profileImageTxt)
	{
		this.profileImageTxt = profileImageTxt;
	}
}
