package nu.yona.app.customview.graph;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    private List<TimeZoneSpread> mListZoneSpread;
    private Canvas mCanvas;

    private float mStartPoint;
    private float mMiddlePoint;

    private float x_top = 0;
    private float x_bottom = x_top + 100;

    public TimeFrameGraph(Context context) {
        super(context);
        init();
    }

    public TimeFrameGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeFrameGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TimeFrameGraph(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mListZoneSpread = new ArrayList<TimeZoneSpread>();
    }

    public void chartValuePre(List<TimeZoneSpread> mListZoneSpread) {
        this.mListZoneSpread = mListZoneSpread;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;
        float fullWidth = canvas.getWidth();
        float height = scaleFactor * 25;

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

        //draw graphics of sun and moon
        mCanvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icon_moon)), mStartPoint, bottom, null);
        mCanvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icn_sun)), mMiddlePoint, bottom, null);


        Paint mTextPaint = new Paint();
        mTextPaint.setColor(GraphUtils.COLOR_BULLET_DOT);
        mTextPaint.setTextSize(scaleFactor * 14);
        mTextPaint.setStrokeWidth(8);

        float spreadtime = fullWidth;

        float mPartSize = spreadtime / 96;

        float minValue = mPartSize / 15;

        float textPoint = (mMiddlePoint / 2) / 2;
        mCanvas.drawText(mContext.getString(R.string.four_hours), textPoint, heightDraw, mTextPaint);
        float textPoint2 = textPoint * 2 + ((textPoint / 2));
        mCanvas.drawText(mContext.getString(R.string.eight_hours), textPoint2, heightDraw, mTextPaint);
        float textPoint3 = textPoint * 5;
        mCanvas.drawText(mContext.getString(R.string.sixteen_hours), textPoint3, heightDraw, mTextPaint);
        float textPoint4 = textPoint * 6 + ((textPoint / 2));
        mCanvas.drawText(mContext.getString(R.string.twenty_hours), textPoint4, heightDraw, mTextPaint);
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

    public static Bitmap drawableToBitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }
}
