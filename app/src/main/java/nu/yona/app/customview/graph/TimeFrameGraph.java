package nu.yona.app.customview.graph;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import nu.yona.app.R;
import nu.yona.app.api.model.TimeZoneSpread;

/**
 * Created by bhargavsuthar on 08/06/16.
 */
public class TimeFrameGraph extends BaseView {

    private final int mNoParts = 96;
    private final int mMinPerParts = 15;
    private List<TimeZoneSpread> mListZoneSpread;
    private Canvas mCanvas;
    private float mStartPoint;
    private float mMiddlePoint;

    /**
     * Instantiates a new Time frame graph.
     *
     * @param context the context
     */
    public TimeFrameGraph(Context context) {
        super(context);
        init();
    }

    /**
     * Instantiates a new Time frame graph.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public TimeFrameGraph(Context context, AttributeSet attrs) {
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
    public TimeFrameGraph(Context context, AttributeSet attrs, int defStyleAttr) {
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
    public TimeFrameGraph(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mListZoneSpread = new ArrayList<TimeZoneSpread>();
    }

    /**
     * Chart value pre.
     *
     * @param mListZoneSpread the m list zone spread
     */
    public void chartValuePre(List<TimeZoneSpread> mListZoneSpread) {
        this.mListZoneSpread = mListZoneSpread;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;
        float fullWidth = canvas.getWidth();
        float height = scaleFactor * GraphUtils.HEIGHT_BAR;

        //first bar
        float left = 0, top = 0; // basically (X1, Y1)

        float right = fullWidth; // width (distance from X1 to X2)
        float bottom = top + height; // height (distance from Y1 to Y2)

        mStartPoint = 0;
        mMiddlePoint = (fullWidth / 2);

        RectF myRectum = new RectF(left, top, right, bottom);
        mCanvas.drawRect(myRectum, linePaint);

        //todraw text from height
        float heightDraw = bottom + (20 * scaleFactor);

        Bitmap moonBitmap = drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icon_moon));
        float bitmapWidth = moonBitmap.getWidth() / 2;
        //draw graphics of sun and moon
        mCanvas.drawBitmap(moonBitmap, mStartPoint, bottom, null);
        mCanvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icn_sun)), mMiddlePoint - bitmapWidth, bottom, null);


        Typeface timeFrameTypeFace = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + "roboto-regular.ttf");
        Paint mTextPaint = new Paint();
        mTextPaint.setColor(GraphUtils.COLOR_BULLET_DOT);
        mTextPaint.setTextSize(scaleFactor * 14);
        mTextPaint.setStrokeWidth(8);
        mTextPaint.setTypeface(timeFrameTypeFace);

        float spreadtime = fullWidth;

        float mPartSize = spreadtime / mNoParts;

        float minValue = mPartSize / mMinPerParts;

        float textPoint = (mMiddlePoint / 2) / 2;
        mCanvas.drawText(mContext.getString(R.string.four_hours), textPoint, heightDraw, mTextPaint);
        float textPoint2 = textPoint * 2 + ((textPoint / 2));
        mCanvas.drawText(mContext.getString(R.string.eight_hours), textPoint2, heightDraw, mTextPaint);
        float textPoint3 = textPoint * 5;
        mCanvas.drawText(mContext.getString(R.string.sixteen_hours), textPoint3 - bitmapWidth, heightDraw, mTextPaint);
        float textPoint4 = textPoint * 6 + ((textPoint / 2));
        mCanvas.drawText(mContext.getString(R.string.twenty_hours), textPoint4 - bitmapWidth, heightDraw, mTextPaint);
        float textPoint5 = textPoint * 7 + ((textPoint / 2));
        mCanvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icon_moon)), textPoint5, bottom, null);

        if (mListZoneSpread != null && mListZoneSpread.size() > 0) {
            float currentIndex = 0;
            float currentStartPos;
            float currentEndPos;
            for (TimeZoneSpread timeZoneSpread : mListZoneSpread) {
                currentEndPos = 0;
                Paint mZonePaint = new Paint();
                mZonePaint.setStrokeWidth(1);
                currentStartPos = currentIndex * mPartSize;
                currentEndPos = (minValue * timeZoneSpread.getUsedValue()) + currentStartPos;
                mZonePaint.setColor(timeZoneSpread.getColor());
                mCanvas.drawRect(currentStartPos, top, currentEndPos, bottom, mZonePaint);
                currentIndex++;
            }
        }

    }
}
