package nu.yona.app.customview.graph;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.TimeZoneSpread;

/**
 * Created by bhargavsuthar on 08/06/16.
 */
public class TimeFrameGraph extends BaseView
{

	private static final int NO_PARTS = 96;
	private static final int MIN_PER_PARTS = 15;
	long animationDuration = 1000;
	private List<TimeZoneSpread> mListZoneSpread;
	private float mStartPoint;
	private float mMiddlePoint;
	private float endPoint;
	private float currentStartPos = 0;
	private float currentEndPos;
	boolean isCanvasInvalidated = false;
	private List<GraphData> graphDataList;

	/**
	 * Instantiates a new Time frame graph.
	 *
	 * @param context the context
	 */
	public TimeFrameGraph(Context context)
	{
		super(context);
		init();
	}

	/**
	 * Instantiates a new Time frame graph.
	 *
	 * @param context the context
	 * @param attrs   the attrs
	 */
	public TimeFrameGraph(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	/**
	 * Instantiates a new Time frame graph.
	 *
	 * @param context      the context
	 * @param attrs        the attrs
	 * @param defStyleAttr the def style attr
	 */
	public TimeFrameGraph(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

	/**
	 * Instantiates a new Time frame graph.
	 *
	 * @param context      the context
	 * @param attrs        the attrs
	 * @param defStyleAttr the def style attr
	 * @param defStyleRes  the def style res
	 */
	public TimeFrameGraph(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init()
	{
		mListZoneSpread = new ArrayList<TimeZoneSpread>();
		this.postInvalidate();
	}

	float fullWidth;
	float left, top;
	float height;
	float right;
	float bottom;
	float mPartSize;
	float minValue;

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		if (mListZoneSpread != null)
		{
			chartValuePre(mListZoneSpread);
		}
	}

	/**
	 * Chart value pre.
	 *
	 * @param mListZoneSpread the m list zone spread
	 */
	public void chartValuePre(List<TimeZoneSpread> mListZoneSpread)
	{
		position = 0;
		this.mListZoneSpread = mListZoneSpread;
		graphDataList = new ArrayList<>();

		fullWidth = getWidth();
		height = scaleFactor * GraphUtils.HEIGHT_BAR;

		//first bar
		left = 0;
		top = 0; // basically (X1, Y1)

		right = fullWidth; // width (distance from X1 to X2)
		bottom = top + height; // height (distance from Y1 to Y2)

		mStartPoint = 0;
		mMiddlePoint = (fullWidth / 2);

		float spreadtime = fullWidth;

		mPartSize = spreadtime / NO_PARTS;

		minValue = mPartSize / MIN_PER_PARTS;


		if (mListZoneSpread != null && mListZoneSpread.size() > 0)
		{
			currentStartPos = 0;
			currentEndPos = 0;
			for (int i = 0; i < (mListZoneSpread.size() - 1); i++)
			{
				Paint mZonePaint = new Paint();
				mZonePaint.setStrokeWidth(1);
				if (currentStartPos == 0)
				{
					currentStartPos = mListZoneSpread.get(i).getIndex() * mPartSize;
				}
				else
				{
					if (mListZoneSpread.get(i).getIndex() == mListZoneSpread.get(i - 1).getIndex())
					{
						currentStartPos = (mListZoneSpread.get(i).getIndex() * mPartSize) + (minValue * mListZoneSpread.get(i - 1).getUsedValue());
					}
					else
					{
						currentStartPos = mListZoneSpread.get(i).getIndex() * mPartSize;
					}
				}
				currentEndPos = (minValue * mListZoneSpread.get(i).getUsedValue()) + currentStartPos;
				mZonePaint.setColor(mListZoneSpread.get(i).getColor());

				GraphData newGraphData = new GraphData(currentStartPos, currentEndPos, mZonePaint);
				/*graphDataList.add(newGraphData);*/
				udpateGraphData(newGraphData, i);
				startAnimation();
			}
		}

	}

	private int position;

	private void udpateGraphData(GraphData mGraphdata, int pos)
	{
		GraphData currentGraphData = mGraphdata;
		if (graphDataList.size() > 0)
		{
			GraphData previousGraphData = graphDataList.get(position - 1);
			if (currentGraphData.getPaint().getColor() == GraphUtils.COLOR_PINK && previousGraphData.getPaint().getColor() == GraphUtils.COLOR_PINK)
			{
				previousGraphData.setEndPoint(currentGraphData.getEndPoint());
			}
			else
			{
				graphDataList.add(currentGraphData);
				position++;
			}
		}
		else
		{
			graphDataList.add(currentGraphData);
			position++;
		}

	}

	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);


		RectF myRectum = new RectF(left, top, right, bottom);
		canvas.drawRect(myRectum, linePaint);

		//todraw text from height
		float heightDraw = bottom + (GraphUtils.MARGIN_TOP * scaleFactor);

		Bitmap moonBitmap = drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icon_moon));
		float bitmapWidth = moonBitmap.getWidth() / 2;
		//draw graphics of sun and moon
		canvas.drawBitmap(moonBitmap, mStartPoint - (5 * scaleFactor), bottom + (5 * scaleFactor), null);
		canvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icn_sun)), mMiddlePoint - bitmapWidth, bottom + (5 * scaleFactor), null);

		float textPoint = (mMiddlePoint / 2) / 2;
		canvas.drawText(mContext.getString(R.string.four_hours), textPoint, heightDraw, getFontStyle());
		float textPoint2 = textPoint * 2 + ((textPoint / 2));
		canvas.drawText(mContext.getString(R.string.eight_hours), textPoint2, heightDraw, getFontStyle());
		float textPoint3 = textPoint * 5;
		canvas.drawText(mContext.getString(R.string.sixteen_hours), textPoint3 - bitmapWidth, heightDraw, getFontStyle());
		float textPoint4 = textPoint * 6 + ((textPoint / 2));
		canvas.drawText(mContext.getString(R.string.twenty_hours), textPoint4 - bitmapWidth, heightDraw, getFontStyle());
		float textPoint5 = textPoint * 7 + ((textPoint / 2)) - (scaleFactor * 2);
		canvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icon_moon)), textPoint5, bottom + (5 * scaleFactor), null);


		for (GraphData graphData : graphDataList)
		{
			graphData.draw(canvas, top, bottom, endPoint);
		}
	}


	public void startAnimation()
	{
		Animator anim = ObjectAnimator.ofFloat(this, "endPoint", 0, 1);
		anim.setDuration(animationDuration);
		anim.start();
	}

	/**
	 * do not remove this method, endPoint's ObjectAnimator is using this
	 */
	public void setEndPoint(float endPoint)
	{
		this.endPoint = endPoint;
		isCanvasInvalidated = true;
		invalidate();
	}
}
