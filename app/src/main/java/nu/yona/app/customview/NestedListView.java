/*
 *  Copyright (c) 2016 Stichting Yona Foundation
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 *
 */

package nu.yona.app.customview;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by kinnarvasa on 16/06/16.
 */
public class NestedListView extends ListView implements NestedScrollingChild
{

	private final NestedScrollingChildHelper mScrollingChildHelper;

	/**
	 * Instantiates a new Nested list view.
	 *
	 * @param context the context
	 */
	public NestedListView(Context context)
	{
		super(context);
		mScrollingChildHelper = new NestedScrollingChildHelper(this);
		setNestedScrollingEnabled(true);
	}

	/**
	 * Instantiates a new Nested list view.
	 *
	 * @param context the context
	 * @param attrs   the attrs
	 */
	public NestedListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mScrollingChildHelper = new NestedScrollingChildHelper(this);
		setNestedScrollingEnabled(true);
	}

	@Override
	public boolean isNestedScrollingEnabled()
	{
		return mScrollingChildHelper.isNestedScrollingEnabled();
	}

	@Override
	public void setNestedScrollingEnabled(boolean enabled)
	{
		mScrollingChildHelper.setNestedScrollingEnabled(enabled);
	}

	@Override
	public boolean startNestedScroll(int axes)
	{
		return mScrollingChildHelper.startNestedScroll(axes);
	}

	@Override
	public void stopNestedScroll()
	{
		mScrollingChildHelper.stopNestedScroll();
	}

	@Override
	public boolean hasNestedScrollingParent()
	{
		return mScrollingChildHelper.hasNestedScrollingParent();
	}

	@Override
	public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
										int dyUnconsumed, int[] offsetInWindow)
	{
		return mScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
				dxUnconsumed, dyUnconsumed, offsetInWindow);
	}

	@Override
	public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow)
	{
		return mScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
	}

	@Override
	public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed)
	{
		return mScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
	}

	@Override
	public boolean dispatchNestedPreFling(float velocityX, float velocityY)
	{
		return mScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
	}
}
