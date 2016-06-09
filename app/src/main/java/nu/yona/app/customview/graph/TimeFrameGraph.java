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

    private static int MINUTES_PER_DAY = 1440;

    private float noOfSection;

    private List<TimeZoneSpread> mListZoneSpread;
    private Canvas mCanvas;

    private float mStartPoint;
    private float mEndPoint;
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

        /*updateGraphPoint();*/
    }

    private void updateGraphPoint() {

       /* for (int i = 0; i <= mListOfEndTimes.size() - 1; i++) {
            int endTime = mListOfEndTimes.get(i);
            float cStartPoint = mListOfStartTimes.get();
            float cEndPoint = mListOfEndTimes.get(i);
        }

        for (int i = 0; i <= mListOfStartTimes.size() - 1; i++) {
            float cStartPoint = mListOfStartTimes.get(i) * noOfSection - (20 * scaleFactor);
            float cEndPoint = mListOfEndTimes.get(i) * noOfSection - (25 * scaleFactor);
            Paint cPaint = new Paint();
            cPaint.setStrokeWidth(1);

            //Todo - check the color and assigned that color
            RectF cRect = new RectF(cStartPoint, 0, cEndPoint, 100); //left, top , right, bottom
            mCanvas.drawRect(cRect, cPaint); //rectF, paint
        }*/

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;
        float fullWidth = canvas.getWidth();
        float height = scaleFactor * 25;

        //no of section should be 96 , thats why 1440/15 = 96, we will get that array
        noOfSection = MINUTES_PER_DAY / 15;

        //first bar
        float left = 0, top = 0; // basically (X1, Y1)

        float right = fullWidth; // width (distance from X1 to X2)
        float bottom = top + height; // height (distance from Y1 to Y2)

        mStartPoint = 0;
        mMiddlePoint = (fullWidth / 2);
        mEndPoint = right;


        RectF myRectum = new RectF(left, top, right, bottom);
        mCanvas.drawRect(myRectum, linePaint);


        //draw graphics of sun and moon
        mCanvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icon_moon)), mStartPoint, top + height, null);
        mCanvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icn_sun)), mMiddlePoint, top + height, null);
        mCanvas.drawBitmap(drawableToBitmap(ContextCompat.getDrawable(mContext, R.drawable.icon_moon)), mEndPoint - height, top + height, null);

        Paint mTextPaint = new Paint();
        mTextPaint.setColor(GraphUtils.COLOR_WHITE_THREE);
        mTextPaint.setTextSize(scaleFactor * 12);
        mTextPaint.setStrokeWidth(3);

        //todraw text from height
        float heightDraw = bottom + (15 * scaleFactor);

        //dividing into 7 section from 96
        float textPoint = noOfSection;

        textPoint = textPoint + textPoint;
        mCanvas.drawText("04:00", textPoint, heightDraw, mTextPaint);
        textPoint = textPoint + textPoint;
        mCanvas.drawText("08:00", textPoint, heightDraw, mTextPaint);
        textPoint = textPoint + textPoint;
        mCanvas.drawText("16:00", textPoint, heightDraw, mTextPaint);
        textPoint = textPoint + (noOfSection * 2);
        mCanvas.drawText("20:00", textPoint, heightDraw, mTextPaint);
        updateGraphPoint();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }
}
