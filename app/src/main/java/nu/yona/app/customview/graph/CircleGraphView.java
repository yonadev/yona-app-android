/*
 * Copyright (c) 2018 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.customview.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bhargavsuthar on 20/06/16.
 */
public class CircleGraphView
		extends View
{

	private final int strokeColor = GraphUtils.COLOR_WHITE_THREE;
	private final int strokeWidth = 2;
	private int fillColor = GraphUtils.COLOR_WHITE_THREE;

	/**
	 * Instantiates a new Circle graph view.
	 *
	 * @param context the context
	 */
	public CircleGraphView(Context context)
	{
		super(context);

		init();
	}

	/**
	 * Instantiates a new Circle graph view.
	 *
	 * @param context the context
	 * @param attrs   the attrs
	 */
	public CircleGraphView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);

		init();
	}

	/**
	 * Instantiates a new Circle graph view.
	 *
	 * @param context      the context
	 * @param attrs        the attrs
	 * @param defStyleAttr the def style attr
	 */
	public CircleGraphView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init()
	{
		this.setSaveEnabled(true);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		int w = this.getWidth();
		int h = this.getHeight();

		int ox = w / 2;
		int oy = h / 2;

		canvas.drawCircle(ox, oy, w / 2, getFill());
	}

	private Paint getStroke()
	{
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setStrokeWidth(strokeWidth);
		p.setColor(strokeColor);
		p.setStyle(Paint.Style.STROKE);
		return p;
	}

	private Paint getFill()
	{
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(fillColor);
		p.setStyle(Paint.Style.FILL);
		return p;
	}

	/**
	 * Sets fill color.
	 *
	 * @param fillColor the fill color
	 */
	public void setFillColor(int fillColor)
	{
		this.fillColor = fillColor;
	}

}
