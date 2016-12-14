package com.zhang.myclock.ui.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.zhang.myclock.R;


public class AutoScaleFrameLayout extends FrameLayout {
    private static final String TAG = "AutoScaleFrameLayout";
    private static final boolean DEBUG = false;

    private boolean isAutoScale = true;
    private AutoScaleHelper as = null;

    public AutoScaleFrameLayout(Context context) {
        super(context);
        initFromAttributes(context, null);
    }

    public AutoScaleFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFromAttributes(context, attrs);
    }

    public AutoScaleFrameLayout(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        initFromAttributes(context, attrs);
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        int deviceWidth = 0;
        int deviceHeight = 0;

        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScaleLayout);
            isAutoScale = a.getBoolean(R.styleable.AutoScaleLayout_autoScale, true);
            deviceWidth = a.getInt(R.styleable.AutoScaleLayout_designWidth, -1);
            deviceHeight = a.getInt(R.styleable.AutoScaleLayout_designHeight, -1);
            if (DEBUG) {
                Log.d(TAG, "in initFromAttributes(), isAutoScale = " + isAutoScale);
            }
            a.recycle();
        }

        as = AutoScaleHelper.getInstance(context);
        as.setDeviceWidth(deviceWidth);
        as.setDeviceHeight(deviceHeight);

        setOnHierarchyChangeListener(new OnHierarchyChangeListener() {

            @Override
            public void onChildViewRemoved(View parent, View child) {
                if (DEBUG) {
                    Log.d(TAG, "in onChildViewRemoved()");
                }
            }

            @Override
            public void onChildViewAdded(View parent, View child) {
                if (DEBUG) {
                    Log.d(TAG, "in onChildViewAdded()");
                }
                if (isAutoScale) {
                    as.adjustViewAtts(child);
                }
            }
        });
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        LayoutParams lp = new LayoutParams(getContext(), attrs);
        int width = lp.width;
        int height = lp.height;
        if (isAutoScale && lp.autoScale) {
            as.scale(lp);
            lp.isScaled = true;
        }
        if (DEBUG) {
            Log.d(TAG, "in generateLayoutParams(AttributeSet attrs), width=" + width + ", height=" + height
                    + "; after scale, width=" + lp.width + ", height=" + lp.height);
        }

        return lp;
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams(
            android.view.ViewGroup.LayoutParams p) {
        LayoutParams lp = new LayoutParams(p);

        int width = lp.width;
        int height = lp.height;
        if (isAutoScale && lp.autoScale) {
            as.scale(lp);
            lp.isScaled = true;
        }
        if (DEBUG) {
            Log.d(TAG, "in generateLayoutParams(LayoutParams p), width=" + width + ", height=" + height
                    + "; after scale, width=" + lp.width + ", height=" + lp.height + "; arg p=" + p);
        }
        return lp;
    }

    @Override
    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        int width = p.width;
        int height = p.height;

        boolean isChecked = p instanceof LayoutParams;
        if (isChecked) {
            LayoutParams lp = (LayoutParams) p;
            if (isAutoScale && lp.autoScale && (false == lp.isScaled)) {
                as.scale(lp);
            }
            if (DEBUG) {
                Log.d(TAG, "in checkLayoutParams(AutoScaleFrameLayout.LayoutParams p), width=" + width + ", height=" + height
                        + "; after scale, width=" + lp.width + ", height=" + lp.height + "; arg p=" + p);
            }
        } else {
            isChecked = p instanceof FrameLayout.LayoutParams;
            if (isChecked) {
                FrameLayout.LayoutParams llp = (FrameLayout.LayoutParams) p;
                if (isAutoScale) {
                    as.scale(llp);
                }
                if (DEBUG) {
                    Log.d(TAG,
                            "in checkLayoutParams(FrameLayout.LayoutParams p), width="
                                    + width + ", height=" + height
                                    + "; after scale, width=" + llp.width
                                    + ", height=" + llp.height + "; arg p=" + p);
                }
            }
        }

        return isChecked;
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public boolean autoScale = true;
        public boolean isScaled = false;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            if (null != attrs) {
                TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.AutoScaleLayout);
                autoScale = a.getBoolean(R.styleable.AutoScaleLayout_autoScale, true);
                if (DEBUG) {
                    Log.d(TAG, "in LayoutParams(), autoScale = " + autoScale);
                }
                a.recycle();
            }
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(android.widget.FrameLayout.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

    }

}
