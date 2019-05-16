package com.ulfy.android.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class ShapeLayout extends FrameLayout {
    public static final int SHAPE_CIRCLE = 1;       // 圆形
    public static final int SHAPE_RECT = 2;         // 矩形
    private Shape shape;                            // 形状
    private Drawable shapeBackground;               // 背景

    public ShapeLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ShapeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ShapeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ShapeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapeLayout);

            // 处理形状参数 如果没有设置形状的值，则不错任何处理
            if (typedArray.hasValue(R.styleable.ShapeLayout_layout_shape)) {
                int shapeType = typedArray.getInt(R.styleable.ShapeLayout_layout_shape, 0);
                // 圆形则直接设置一个默认的圆形
                if (shapeType == SHAPE_CIRCLE) {
                    shape = new CircleShape();
                }
                // 矩形则根据对四个角的设置进行处理
                else if (shapeType == SHAPE_RECT) {
                    RectShape rectShape = new RectShape();
                    // 设置顺序按照先总后分的方式设置，因此分别设置可覆盖部分总的设置
                    if (typedArray.hasValue(R.styleable.ShapeLayout_rect_radius)) {
                        float radius = typedArray.getDimension(R.styleable.ShapeLayout_rect_radius, 0);
                        rectShape.setRadius(radius);
                    }
                    if (typedArray.hasValue(R.styleable.ShapeLayout_rect_radius_left_top)) {
                        float radiusLeftTop = typedArray.getDimension(R.styleable.ShapeLayout_rect_radius_left_top, 0);
                        rectShape.setRadiusLeftTop(radiusLeftTop);
                    }
                    if (typedArray.hasValue(R.styleable.ShapeLayout_rect_radius_right_top)) {
                        float radiusRightTop = typedArray.getDimension(R.styleable.ShapeLayout_rect_radius_right_top, 0);
                        rectShape.setRadiusRightTop(radiusRightTop);
                    }
                    if (typedArray.hasValue(R.styleable.ShapeLayout_rect_radius_right_bottom)) {
                        float radiusRightBottom = typedArray.getDimension(R.styleable.ShapeLayout_rect_radius_right_bottom, 0);
                        rectShape.setRadiusRightBottom(radiusRightBottom);
                    }
                    if (typedArray.hasValue(R.styleable.ShapeLayout_rect_radius_left_bottom)) {
                        float radiusLeftBottom = typedArray.getDimension(R.styleable.ShapeLayout_rect_radius_left_bottom, 0);
                        rectShape.setRadiusLeftBottom(radiusLeftBottom);
                    }
                    shape = rectShape;
                }
            }

            // 处理背景参数
            if (typedArray.hasValue(R.styleable.ShapeLayout_shape_background)) {
                shapeBackground = typedArray.getDrawable(R.styleable.ShapeLayout_shape_background);
                if (shapeBackground != null) {
                    // 设置一个默认的透明背景用于出发onDraw方法
                    setBackground(new ColorDrawable(Color.TRANSPARENT));
                }
            }

            typedArray.recycle();
        }
    }

    /**
     * 对背景进行裁切
     */
    @Override protected void onDraw(Canvas canvas) {
        if (shape == null) {
            super.onDraw(canvas);
        } else {
            if (canvas.getWidth() != 0 && canvas.getHeight() != 0) {
                int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
                if (shapeBackground != null) {
                    shapeBackground.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    shapeBackground.draw(canvas);
                }
                super.onDraw(canvas);
                shape.draw(canvas, new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
                canvas.restoreToCount(layerId);
            }
        }
    }

    /**
     * 对内容进行裁切
     */
    @Override protected void dispatchDraw(Canvas canvas) {
        if (shape == null) {
            super.dispatchDraw(canvas);
        } else {
            if (canvas.getWidth() != 0 && canvas.getHeight() != 0) {
                int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
                super.dispatchDraw(canvas);
                shape.draw(canvas, new RectF(getPaddingLeft(), getPaddingTop(), canvas.getWidth() - getPaddingRight(), canvas.getHeight() - getPaddingBottom()), 0, 0, 0, 0);
                canvas.restoreToCount(layerId);
            }
        }
    }

    /**
     * 设置形状
     */
    public ShapeLayout setShape(Shape shape) {
        this.shape = shape;
        this.invalidate();
        return this;
    }

    /**
     * 设置背景颜色
     */
    public ShapeLayout setShapeBackgroundColor(@ColorInt int color) {
        return setShapeBackground(new ColorDrawable(color));
    }

    /**
     * 设置背景资源
     */
    public ShapeLayout setShapeBackgroundResource(@DrawableRes int resid) {
        return setShapeBackground(resid != 0 ? getResources().getDrawable(resid) : null);
    }

    /**
     * 设置背景
     */
    public ShapeLayout setShapeBackground(Drawable shapeBackground) {
        this.shapeBackground = shapeBackground;
        setBackground(shapeBackground == null ? null : new ColorDrawable(Color.TRANSPARENT));
        this.invalidate();
        return this;
    }

    /**
     * 形状定义接口
     */
    public interface Shape {
        void draw(Canvas canvas, RectF drawAreaRectF, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom);
    }

    /**
     * 圆形形状
     */
    public static class CircleShape implements Shape {
        private Paint paint;

        public CircleShape() {
            paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        }

        @Override public void draw(Canvas canvas, RectF clipAreaRectF, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
            canvas.drawBitmap(generateSrcBitmap(clipAreaRectF), clipAreaRectF.left, clipAreaRectF.top, paint);
        }

        private Bitmap generateSrcBitmap(RectF clipAreaRectF) {
            Bitmap bitmap = Bitmap.createBitmap((int) (clipAreaRectF.right - clipAreaRectF.left), (int) (clipAreaRectF.bottom - clipAreaRectF.top), Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(bitmap);
            // 填充黑色背景
            Paint dstPaint = new Paint();
            dstPaint.setColor(Color.BLACK);
            dstPaint.setStyle(Paint.Style.FILL);
            bitmapCanvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), dstPaint);
            // 扣出透明的内切圆
            Paint srcPaint = new Paint();
            srcPaint.setColor(Color.WHITE);
            srcPaint.setAntiAlias(true);
            srcPaint.setStyle(Paint.Style.FILL);
            srcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            float cx = bitmap.getWidth() / 2;
            float cy = bitmap.getHeight() / 2;
            float radius = cx < cy ? cx : cy;
            bitmapCanvas.drawCircle(cx, cy, radius, srcPaint);
            return bitmap;
        }
    }

    /**
     * 矩形形状
     */
    public static class RectShape implements Shape {
        private float radiusLeftTop;
        private float radiusRightTop;
        private float radiusRightBottom;
        private float radiusLeftBottom;
        private Paint paint;

        public RectShape() {
            paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        }

        @Override public void draw(Canvas canvas, RectF clipAreaRectF, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
            canvas.drawBitmap(generateSrcBitmap(clipAreaRectF, paddingLeft, paddingTop, paddingRight, paddingBottom), clipAreaRectF.left, clipAreaRectF.top, paint);
        }

        private Bitmap generateSrcBitmap(RectF clipAreaRectF, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
            Bitmap bitmap = Bitmap.createBitmap((int) (clipAreaRectF.right - clipAreaRectF.left), (int) (clipAreaRectF.bottom - clipAreaRectF.top), Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(bitmap);
            // 填充黑色背景
            Paint dstPaint = new Paint();
            dstPaint.setColor(Color.BLACK);
            dstPaint.setStyle(Paint.Style.FILL);
            bitmapCanvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), dstPaint);
            // 扣出透明的内切矩形
            Paint srcPaint = new Paint();
            srcPaint.setColor(Color.WHITE);
            srcPaint.setAntiAlias(true);
            srcPaint.setStyle(Paint.Style.FILL);
            srcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            Path path = new Path();
            float[] radius = {
                    radiusLeftTop + paddingLeft, radiusLeftTop + paddingTop,
                    radiusRightTop + paddingRight, radiusRightTop + paddingTop,
                    radiusRightBottom + paddingRight, radiusRightBottom + paddingBottom,
                    radiusLeftBottom + paddingLeft, radiusLeftBottom + paddingBottom
            };
            path.addRoundRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), radius, Path.Direction.CW);
            bitmapCanvas.drawPath(path, srcPaint);
            return bitmap;
        }

        public RectShape setRadius(float radius) {
            setRadiusLeftTop(radius);
            setRadiusRightTop(radius);
            setRadiusRightBottom(radius);
            setRadiusLeftBottom(radius);
            return this;
        }

        public RectShape setRadiusLeftTop(float radiusLeftTop) {
            this.radiusLeftTop = radiusLeftTop;
            return this;
        }

        public RectShape setRadiusRightTop(float radiusRightTop) {
            this.radiusRightTop = radiusRightTop;
            return this;
        }

        public RectShape setRadiusRightBottom(float radiusRightBottom) {
            this.radiusRightBottom = radiusRightBottom;
            return this;
        }

        public RectShape setRadiusLeftBottom(float radiusLeftBottom) {
            this.radiusLeftBottom = radiusLeftBottom;
            return this;
        }
    }
}
