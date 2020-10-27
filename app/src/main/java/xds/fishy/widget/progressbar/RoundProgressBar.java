package xds.fishy.widget.progressbar;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import xds.fishy.recorder.R;

public class RoundProgressBar extends View {

    private Paint mBackPaint, mProgPaint;
    private RectF mRectF;
    private int[] mColorArray;
    private int mProgress;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        @SuppressLint({"Recycle", "CustomViewStyleable"})
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

        mBackPaint = new Paint();
        mBackPaint.setStyle(Paint.Style.STROKE);
        mBackPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackPaint.setAntiAlias(true);
        mBackPaint.setDither(true);
        mBackPaint.setStrokeWidth(typedArray.getDimension(R.styleable.RoundProgressBar_circle_width, 5));
        mBackPaint.setColor(typedArray.getColor(R.styleable.RoundProgressBar_circle_color, Color.LTGRAY));

        mProgPaint = new Paint();
        mProgPaint.setStyle(Paint.Style.STROKE);
        mProgPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgPaint.setAntiAlias(true);
        mProgPaint.setDither(true);
        mProgPaint.setStrokeWidth(typedArray.getDimension(R.styleable.RoundProgressBar_progress_width, 10));
        mProgPaint.setColor(typedArray.getColor(R.styleable.RoundProgressBar_progress_color, Color.BLUE));

        int startColor = typedArray.getColor(R.styleable.RoundProgressBar_start_color, -1);
        int firstColor = typedArray.getColor(R.styleable.RoundProgressBar_end_color, -1);
        if (startColor != -1 && firstColor != -1) mColorArray = new int[]{startColor, firstColor};
        else mColorArray = null;

        mProgress = typedArray.getInteger(R.styleable.RoundProgressBar_progress, 0);
        typedArray.recycle();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWide = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewHigh = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int mRectLength = (int) ((Math.min(viewWide, viewHigh)) - (Math.max(mBackPaint.getStrokeWidth(),
                mProgPaint.getStrokeWidth())));
        int mRectL = getPaddingLeft() + (viewWide - mRectLength) / 2;
        int mRectT = getPaddingTop() + (viewHigh - mRectLength) / 2;
        mRectF = new RectF(mRectL, mRectT, mRectL + mRectLength, mRectT + mRectLength);

        if (mColorArray != null && mColorArray.length > 1)
            mProgPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(), mColorArray,
                    null, Shader.TileMode.MIRROR));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRectF, 0, 360, false, mBackPaint);
        canvas.drawArc(mRectF, 275, 360 * mProgress / 100f, false, mProgPaint);
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public void setProgress(int progress, long animTime) {
        if (animTime <= 0) setProgress(progress);
        else {
            ValueAnimator animator = ValueAnimator.ofInt(mProgress, progress);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mProgress = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.setInterpolator(new OvershootInterpolator());
            animator.setDuration(animTime);
            animator.start();
        }
    }

    public void setCircleWidth(int width) {
        mBackPaint.setStrokeWidth(width);
        invalidate();
    }

    public void setCircleColor(@ColorRes int color) {
        mBackPaint.setColor(ContextCompat.getColor(getContext(), color));
        invalidate();
    }

    public void setProgressWidth(int width) {
        mProgPaint.setStrokeWidth(width);
        invalidate();
    }

    public void setProgressColor(@ColorRes int color) {
        mProgPaint.setColor(ContextCompat.getColor(getContext(), color));
        mProgPaint.setShader(null);
        invalidate();
    }

    public void setProgressColor(@ColorRes int startColor, @ColorRes int firstColor) {
        mColorArray = new int[]{ContextCompat.getColor(getContext(), startColor),
                ContextCompat.getColor(getContext(), firstColor)};
        mProgPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(),
                mColorArray, null, Shader.TileMode.MIRROR));
        invalidate();
    }

    public void setProgressColor(@ColorRes int[] colorArray) {
        if (colorArray == null || colorArray.length < 2) return;
        mColorArray = new int[colorArray.length];
        for (int index = 0; index < colorArray.length; index++)
            mColorArray[index] = ContextCompat.getColor(getContext(), colorArray[index]);
        mProgPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(),
                mColorArray, null, Shader.TileMode.MIRROR));
        invalidate();
    }
}