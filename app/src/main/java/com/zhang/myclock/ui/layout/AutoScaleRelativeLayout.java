package com.zhang.myclock.ui.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zhang.myclock.R;


public class AutoScaleRelativeLayout extends RelativeLayout {
	private static final String TAG = "AutoScaleRelativeLayout";
	private static final boolean DEBUG = false;
	
	private boolean isAutoScale = true;
	private AutoScaleHelper as = null;
	
	public AutoScaleRelativeLayout(Context context, AttributeSet attrs,
								   int defStyle) {
		super(context, attrs, defStyle);
		initFromAttributes(context, attrs);
	}
	public AutoScaleRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFromAttributes(context, attrs);
	}

	public AutoScaleRelativeLayout(Context context) {
		super(context);
		initFromAttributes(context, null);
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
	protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
		int width = p.width;
		int height = p.height;

		boolean isChecked = p instanceof AutoScaleRelativeLayout.LayoutParams;
		if (isChecked) {
			LayoutParams lp = (LayoutParams)p;
			if (isAutoScale && lp.autoScale && (false == lp.isScaled)) {
				as.scale(lp);
			}
			if (DEBUG) {
				Log.d(TAG, "in checkLayoutParams(AutoScaleRelativeLayout.LayoutParams p), width=" + width + ", height=" + height
						+ "; after scale, width=" + lp.width + ", height=" + lp.height + "; arg p=" + p);
			}
		} else {
			isChecked = p instanceof RelativeLayout.LayoutParams;
			if (isChecked) {
				RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) p;
				if (isAutoScale) {
					as.scale(rlp);
				}
				if (DEBUG) {
					Log.d(TAG, "in checkLayoutParams(RelativeLayout.LayoutParams p), width="
									+ width + ", height=" + height
									+ "; after scale, width=" + rlp.width
									+ ", height=" + rlp.height + "; arg p=" + p);
				}
			}
		}

		return isChecked;
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
	
	public static class LayoutParams extends RelativeLayout.LayoutParams {
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

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            //Log.d(TAG, "LayoutParams(LayoutParams source");
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            //Log.d(TAG, "LayoutParams(MarginLayoutParams source");
        }
		 
	 }

}
