package com.wong.amelia.attachfiledemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.Random;

/**
 * Created by amelia on 8/4/16.
 */
public class AttachFileAnimView extends View {

    ValueAnimator scaleVa;
    ValueAnimator fileVa;
    ValueAnimator circleVa;
    PathMeasure pathMeasure;

    Path circlePath;
    Path clipPath;
    Path tempPath;
    Path lowPath;
    Path highPath;

    Paint paint;
    Paint lightGreyPaint;
    Paint greenPaint;
    Paint greenFullPaint;
    Paint whiteFullPaint;
    Paint clippaint;

    float width;
    float height;
    float radius;
    float cx;
    float cy;
    float clipWidth;
    float fileTop;
    int fileTopOffset = 30;

    float fileWidth;
    float fileHeight;

    //ratio
    float radiusRatio = 0.25f;
    float clipWidthRatio = 0.231f;
    float r2Ratio = 0.6667f;
    float r3Ratio = 0.333f;
    float l1Ratio = 1;
    float l2Ratio = 1.389f;
    float fileWidthRatio = 1.355f;
    float fileHeightRatio = 2.0f;//1.806f;

    int circleGreyStrokeWidth = 10;
    int circleGreenStrokeWidth = 15;
    int clipStrokeWidth = 13;

    public static int mState;
    public static final int NORMAL_STATE = 0;
    public static final int ANIM_STATE = 1;
    public static final int END_STATE = 2;
    Bitmap mBitmap;
    Canvas mCanvas;

    int lightgrey = Color.rgb(220, 220, 220);
    int darkgrey = Color.rgb(88, 94, 105);
    int green = Color.rgb(94, 166, 61);

    public AttachFileAnimView(Context context) {
        super(context);
        initPaints();
    }

    public AttachFileAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    public AttachFileAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
    }

    private void initPaints() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);

        //circle
        lightGreyPaint = new Paint(paint);
        lightGreyPaint.setStrokeWidth(circleGreyStrokeWidth);
        lightGreyPaint.setColor(lightgrey);

        greenPaint = new Paint(paint);
        greenPaint.setStrokeWidth(circleGreenStrokeWidth);
        greenPaint.setColor(green);

        whiteFullPaint = new Paint(paint);
        whiteFullPaint.setColor(Color.WHITE);
        whiteFullPaint.setStyle(Paint.Style.FILL);

        //clip
        clippaint = new Paint(paint);
        clippaint.setColor(darkgrey);
        clippaint.setStrokeWidth(clipStrokeWidth);

        //file
        greenFullPaint = new Paint(paint);
        greenFullPaint.setStyle(Paint.Style.FILL);
        greenFullPaint.setColor(green);
        greenFullPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

    }

    private void initPaths() {
        //radius = getWidth/6
        width = getWidth();
        height = getHeight();

        cx = width / 2;
        cy = height / 2;

        radius = width*radiusRatio;

        fileTop = cy + radius + fileTopOffset;


        // 回形针和圆圈解耦
        clipWidth = width*radiusRatio*clipWidthRatio*2;
        circlePath = new Path();
        circlePath.addCircle(cx, cy, radius, Path.Direction.CW);


        clipPath = new Path();
        highPath = new Path();
        lowPath = new Path();
        setClipPath(clipPath);

        //
        tempPath = new Path();
        pathMeasure = new PathMeasure(circlePath, false);

        fileWidth = clipWidth*fileWidthRatio;
        fileHeight = clipWidth * fileHeightRatio;

        mBitmap = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

    }

    private void setRadius(float radius) {
//        clipWidth = radius/2;
        if (circlePath != null) {
            circlePath.reset();
        } else {
            circlePath = new Path();
        }

//        if(clipPath != null){
//            clipPath.reset();
//        } else {
//            clipPath = new Path();
//        }
        circlePath.addCircle(cx, cy, radius, Path.Direction.CW);


//        setClipPath(clipPath);
        invalidate();
    }


    public void setClipPath(Path path) {

        float r1 = clipWidth/2;
        float r2 = r1*r2Ratio;
        float r3 = r1*r3Ratio;
        float l1 = clipWidth*l1Ratio;
        float l2 = clipWidth*l2Ratio;

        float startX = cx - clipWidth/2;
        float startY = cy-clipWidth*22/62;



//        int k = clipWidth / 3;
//        int r1 = (int) (1.5 * k);
//        int r2 = k;
//        int r3 = k / 2;

        path.reset();
        highPath.reset();
        lowPath.reset();

        lowPath.moveTo(startX,startY);
        lowPath.lineTo(startX, startY+l1);//1
        RectF rect1 = new RectF(startX, startY+l1-r1, startX+2*r1, startY+l1+r1);
        lowPath.addArc(rect1, 0f, 180f);//2
        lowPath.moveTo(startX+2*r1, startY+l1);
        lowPath.lineTo(startX+2*r1, startY+l1-l2);//3
        path.addPath(lowPath);
        RectF rect2 = new RectF(startX+2*r1-2*r2, startY+l1-l2-r2, startX+2*r1, startY+l1-l2+r2);
        highPath.addArc(rect2, 0f, -180f);//4
        highPath.moveTo(startX+2*r1-2*r2, startY+l1-l2);
        highPath.lineTo(startX+2*r1-2*r2, startY+l1);//5
        RectF rect3 = new RectF(startX+2*r1-2*r2, startY+l1-r3, startX+2*r1-2*r2+2*r3, startY+l1+r3);
        highPath.addArc(rect3, 0f, 180f);//6
        highPath.moveTo(startX+2*r1-2*r2+2*r3, startY+l1);
        highPath.lineTo(startX+2*r1-2*r2+2*r3, startY);//7
        path.addPath(highPath);

    }

    public void startAnim() {
        if (mState == ANIM_STATE) {
            return;
        }
        mState = ANIM_STATE;
        scaleVa = ValueAnimator.ofFloat(0, 1);
        scaleVa.setInterpolator(new BounceInterpolator());
        scaleVa.setDuration(1000);
        final Random random = new Random();
        scaleVa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                Log.e("RIKA", "VAL:" + animation.getAnimatedFraction());
                radius = (int) (getWidth() / 4 + 10 * animation.getAnimatedFraction());
                setRadius(radius);

            }
        });
        scaleVa.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pathMeasure = new PathMeasure(circlePath, false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {


            }
        });
//        va.start();
        ValueAnimator rotateAnim = ValueAnimator.ofFloat(0, 1);
        rotateAnim.setDuration(2000);
        rotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = animation.getAnimatedFraction();
                if (pathMeasure != null) {
                    float length = pathMeasure.getLength();
                    pathMeasure.getSegment(0f, length * progress, tempPath, true);
                    invalidate();
                }
            }
        });
//        rotateAnim.start();

        final ValueAnimator fileAnim = ValueAnimator.ofFloat(0, 1);
        fileAnim.setDuration(2000);
        fileAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = animation.getAnimatedFraction();
                fileTop = (int) (cy + radius - progress * 1.3 * radius);
                invalidate();
            }
        });

        rotateAnim.setStartDelay(100);
        fileAnim.setStartDelay(100);
        AnimatorSet set = new AnimatorSet();
        set.play(scaleVa).before(rotateAnim);
        set.play(rotateAnim).before(fileAnim);
        set.start();


    }

    public void endAnim() {

    }

    public void pauseAnim() {

    }

    public void resumeAnim() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mState) {
            case NORMAL_STATE:
                initPaths();
                drawNormalState(canvas);
                break;
            case ANIM_STATE:
//                drawAnimCircle(canvas);
                drawNormalState(canvas);
//                drawAnimCircle(canvas);
//                drawFile(canvas,fileTop);
                break;

        }
    }

    public void drawNormalState(Canvas canvas) {

        canvas.drawBitmap(mBitmap, getLeft(), getTop(), null);
        mCanvas.drawCircle(cx, cy, radius, whiteFullPaint);
        mCanvas.drawCircle(cx, cy, radius, paint);
        mCanvas.drawPath(lowPath, clippaint);
        drawFile(mCanvas, fileTop);
        mCanvas.drawPath(highPath, clippaint);
        drawAnimCircle(mCanvas);

    }

    public void drawAnimCircle(final Canvas canvas) {

        if (tempPath == null || tempPath.isEmpty())
            return;

        canvas.drawPath(tempPath, greenPaint);
    }

    public void drawFile(Canvas canvas, float top) {

//        Path path = new Path();


        canvas.drawRoundRect(cx - fileWidth / 2, top, cx + fileWidth*0.667f, top + fileHeight, 15,15,greenFullPaint);
        Paint clearpaint = new Paint(greenFullPaint);
        clearpaint.setColor(Color.WHITE);
        clearpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRoundRect(cx + fileWidth*0.667f-0.4f*fileWidth, top + fileHeight-0.4f*fileWidth, cx + fileWidth*0.667f+10, top + fileHeight+10, 15,15,clearpaint);

        float cornerWidth = 0.4f*fileWidth;
        Path corner1 = new Path();
        corner1.moveTo(cx + fileWidth*0.667f,top + fileHeight-cornerWidth);
        corner1.lineTo(cx + fileWidth*0.667f-cornerWidth,top + fileHeight);
        corner1.lineTo(cx + fileWidth*0.667f-cornerWidth,top + fileHeight-cornerWidth);
        corner1.close();

        Path corner2 = new Path();
        corner2.moveTo(cx + fileWidth*0.667f-cornerWidth,top + fileHeight);
        corner2.lineTo(cx + fileWidth*0.667f-cornerWidth,top + fileHeight-cornerWidth);
        corner2.lineTo(cx + fileWidth*0.667f-cornerWidth*2,top + fileHeight);
        corner2.close();


        Paint overpaint1 =  new Paint(greenFullPaint);
        Paint overpaint2 =  new Paint(overpaint1);
        overpaint2.setColor(Color.rgb(82,149,53));
        overpaint1.setColor(Color.rgb(122,198,81));
        overpaint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
        canvas.drawPath(corner1,overpaint1);
        canvas.drawPath(corner2,overpaint2);




    }

}
