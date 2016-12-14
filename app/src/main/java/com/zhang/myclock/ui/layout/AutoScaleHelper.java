package com.zhang.myclock.ui.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint.FontMetrics;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.lang.reflect.Field;

/*
 * 可调整以下属性：
 * paddingLeft
 * paddingTop
 * paddingRight
 * paddingBottom
 *
 * leftMargin
 * topMargin
 * rightMargin
 * bottomMargin
 *
 * TextSize
 * TextPadding
 *
 * drawablePadding
 *
 */
public class AutoScaleHelper {
	private static final String TAG = "AutoScaleHelper";
	private static final boolean DEBUG = false;

	// design的默认尺寸
	private static final int DEFAULT_DEVICE_WIDTH = 1080;
	private static final int DEFAULT_DEVICE_HEIGHT = 1920;


	private int deviceWidth = DEFAULT_DEVICE_WIDTH;

	public int getDeviceWidth() {
		return deviceWidth;
	}

	public void setDeviceWidth(int deviceWidth) {
		if (0 < deviceWidth) {
			this.deviceWidth = deviceWidth;
			getScale(context);
		}

		if (DEBUG) {
			Log.d(TAG, "in setDeviceWidth(deviceWidth=" + deviceWidth + "), this.deviceWidth=" + this.deviceWidth);
		}
	}

	private int deviceHeight = DEFAULT_DEVICE_HEIGHT;

	public int getDeviceHeight() {
		return deviceHeight;
	}

	public void setDeviceHeight(int deviceHeight) {
		if (0 < deviceHeight) {
			this.deviceHeight = deviceHeight;
			getScale(context);
		}

		if (DEBUG) {
			Log.d(TAG, "in setDeviceHeight(deviceHeight=" + deviceHeight + "), this.deviceHeight=" + this.deviceHeight);
		}
	}

	public void setDefaultDeviceSize() {
		setDeviceWidth(DEFAULT_DEVICE_WIDTH);
		setDeviceHeight(DEFAULT_DEVICE_HEIGHT);

		if (DEBUG) {
			Log.d(TAG, "in setDefaultDeviceSize() ");
		}
	}

	private float scaleH = 0;
	private float scaleW = 0;
	private float scale = 0;

	private static AutoScaleHelper as = null;

	public static synchronized AutoScaleHelper getInstance(Context context) {
		if (null == as) {
			as = new AutoScaleHelper(context);
		}

		return as;
	}

	private Context context;
	private AutoScaleHelper(Context context) {
		this.context = context;
		getScale(context);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void adjustViewAtts(View view) {
		if (null == view) {
			return;
		}

		ViewGroup.LayoutParams lp = view.getLayoutParams();
		if (lp instanceof AutoScaleRelativeLayout.LayoutParams) {
			AutoScaleRelativeLayout.LayoutParams rlp = (AutoScaleRelativeLayout.LayoutParams) lp;
			if (false == rlp.autoScale) {
				return;
			}
		} else if (lp instanceof LayoutParams) {

		} else if (lp instanceof AutoScaleLinearLayout.LayoutParams) {
			AutoScaleLinearLayout.LayoutParams llp = (AutoScaleLinearLayout.LayoutParams) lp;
			if (false == llp.autoScale) {
				return;
			}
		} else if (lp instanceof LinearLayout.LayoutParams) {

		} else if (lp instanceof AutoScaleFrameLayout.LayoutParams) {
			AutoScaleFrameLayout.LayoutParams flp = (AutoScaleFrameLayout.LayoutParams) lp;
			if (false == flp.autoScale) {
				return;
			}
		} else if (lp instanceof FrameLayout.LayoutParams) {

		} else {
			return;
		}

		int minWidth = view.getMinimumWidth();
		int scaledMinWidth = Math.round(scale * minWidth);
		if (DEBUG) {
			if (0 < minWidth) {
				Log.d(TAG, "in adjustViewAtts(), minWidth=" + minWidth
						+ ", scaledMinWidth=" + scaledMinWidth);
			}
		}
		view.setMinimumWidth(scaledMinWidth);

		int minHeight = view.getMinimumHeight();
		view.setMinimumHeight(Math.round(scale * minHeight));

		int paddingLeft = view.getPaddingLeft();
		int paddingTop = view.getPaddingTop();
		int paddingRight = view.getPaddingRight();
		int paddingBottom = view.getPaddingBottom();

		view.setPadding(Math.round(scale * paddingLeft),
				Math.round(scale * paddingTop),
				Math.round(scale * paddingRight),
				Math.round(scale * paddingBottom));
		if (DEBUG) {
			Log.d(TAG,
					"in adjustViewAtts(), padding(l,t,r,b)=[" + paddingLeft
							+ ", " + paddingTop + ", " + paddingRight + ", "
							+ paddingBottom + "]"
							+ "; after scale, padding(l,t,r,b)=["
							+ view.getPaddingLeft() + ", "
							+ view.getPaddingTop() + ", "
							+ view.getPaddingRight() + ", "
							+ view.getPaddingBottom() + "]");
		}

		if (view instanceof TextView) {
			TextView tv = (TextView) view;

			minWidth = tv.getMinWidth();
			tv.setMinWidth(Math.round(scale * minWidth));

			minHeight = tv.getMinHeight();
			tv.setMinHeight(Math.round(scale * minHeight));

			float size = tv.getTextSize();
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, scale * size);
			if (DEBUG) {
				Log.d(TAG, "in adjustViewAtts(), textSize=" + size
						+ "; after sclae, textSize=" + tv.getTextSize());
			}

			if (Build.VERSION_CODES.JELLY_BEAN <= Build.VERSION.SDK_INT) {
				float add = tv.getLineSpacingExtra();
				float mult = tv.getLineSpacingMultiplier();

				FontMetrics fm = tv.getPaint().getFontMetrics();
				float leading = Math.abs(fm.ascent - fm.top)
						+ Math.abs(fm.bottom - fm.descent) + fm.leading;
				if (leading < add) {
					add -= leading;
					tv.setLineSpacing(scale * add, mult);
					if (DEBUG) {
						Log.d(TAG,
								"in adjustViewAtts(), LineSpacingExtra=" + add
										+ "; after sclae, LineSpacingExtra="
										+ tv.getLineSpacingExtra()
										+ ", fontMetrics="
										+ tv.getPaint().getFontMetricsInt());
					}
				}

			}
		}

		if (view instanceof EditText) {
			TextView et = (TextView) view;
			int drawablePadding = et.getCompoundDrawablePadding();
			et.setCompoundDrawablePadding((int) (drawablePadding * scale));
			if (DEBUG) {
				Log.d(TAG,
						"in adjustViewAtts(), drawablePadding="
								+ drawablePadding
								+ "; after sclae, ScaleddrawablePadding="
								+ et.getCompoundDrawablePadding());
			}
		}

	}

	public float getScaleH() {
		return scaleH <= 0 ? 1.0f : scaleH;
	}

	public float getScaleW() {
		return scaleW <= 0 ? 1.0f : scaleW;
	}

	public float getScale() {
		return scale;
	}

	private void getScale(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();

		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels - getStatusBarHeight(context)
				- getNavigationBarHeight(context);
		if (DEBUG) {
			Log.d(TAG, "in getScale(), screen(w,h)=[" + screenWidth + ", "
					+ screenHeight + "]; " + "DisplayMetrics=[" + dm.toString()
					+ "]");
		}

		if ((deviceWidth != 0) && (deviceHeight != 0)) {
			scaleW = (float) screenWidth / deviceWidth;
			scaleW = scaleW > 0 ? scaleW : 1;
			scaleH = (float) screenHeight / deviceHeight;
			scaleH = scaleH > 0 ? scaleH : 1;
			if (DEBUG) {
				Log.d(TAG, "in getScale(), scaleW = " + scaleW + ", scaleH = "
						+ scaleH);
			}

			// 取最小比例
			/*if (scaleW < scaleH) {
				scale = scaleW;
			} else {
				scale = scaleH;
			}*/
			// portrait模式，比例按照宽计算
			scale = scaleW;
			scaleH = scaleW = scale;
			if (DEBUG) {
				Log.d(TAG, "in getScale(), after adjust, scaleW=" + scaleW
						+ ", scaleH=" + scaleH + ", scale=" + scale);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void scale(ViewGroup.LayoutParams lp) {
		if (isNotSpecialMode(lp.width)) {
			int width = Math.round(lp.width * scaleW);
			if (DEBUG) {
				Log.d(TAG, "in scale(), orig width=" + lp.width
						+ ", scaled width=" + width);
			}
			lp.width = width;
		}

		if (isNotSpecialMode(lp.height)) {
			int height = Math.round(lp.height * scaleH);
			if (DEBUG) {
				Log.d(TAG, "in scale(), orig height=" + lp.height
						+ ", scaled height=" + height);
			}
			lp.height = height;
		}

		if (lp instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams tmplp = (ViewGroup.MarginLayoutParams) lp;

			int topMargin = tmplp.topMargin;
			int bottomMargin = tmplp.bottomMargin;
			int leftMargin = tmplp.leftMargin;
			int rightMargin = tmplp.rightMargin;
			tmplp.topMargin = Math.round(scale * tmplp.topMargin);
			tmplp.bottomMargin = Math.round(scale * tmplp.bottomMargin);
			tmplp.leftMargin = Math.round(scale * tmplp.leftMargin);
			tmplp.rightMargin = Math.round(scale * tmplp.rightMargin);
			if (DEBUG) {
				Log.d(TAG, "in scale(), margin(l,t,r,b)=[" + leftMargin + ", "
						+ topMargin + ", " + rightMargin + ", " + bottomMargin
						+ "]" + "; after scale, margin(l,t,r,b)=["
						+ tmplp.leftMargin + ", " + tmplp.topMargin + ", "
						+ tmplp.rightMargin + ", " + tmplp.bottomMargin + "]");
			}
		}
	}

	@SuppressWarnings("deprecation")
	private boolean isNotSpecialMode(int size) {
		boolean bNotSpecial = true;
		if ((LayoutParams.WRAP_CONTENT == size)
				|| (LayoutParams.MATCH_PARENT == size)
				|| (LayoutParams.FILL_PARENT == size)) {

			bNotSpecial = false;
		}
		return bNotSpecial;
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			if (DEBUG) {
				Log.v(TAG, "in getStatusBarHeight(), statusBarHeight="
						+ statusBarHeight);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	/*
	 * public static int getStatusBarHeight(Context context) { Resources
	 * resources = context.getResources(); int resourceId =
	 * resources.getIdentifier("status_bar_height", "dimen", "android"); int
	 * height = resources.getDimensionPixelSize(resourceId); if (DEBUG) {
	 * Log.v(TAG, "Status height:" + height); } return height; }
	 */

	public static int getNavigationBarHeight(Context context) {
		int height = 0;
		try {
			if (checkDeviceHasNavigationBar(context)) {
				Resources resources = context.getResources();
				int resourceId = resources.getIdentifier(
						"navigation_bar_height", "dimen", "android");
				height = resources.getDimensionPixelSize(resourceId);
			}
		} catch (Exception e) {
			Log.e(TAG, "in getNavigationBarHeight()" + height, e);
		}
		if (DEBUG) {
			Log.v(TAG, "Navi height:" + height);
		}
		return height;
	}

	public static boolean checkDeviceHasNavigationBar(Context context) {
		// 通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
		boolean hasMenuKey = ViewConfiguration.get(context)
				.hasPermanentMenuKey();
		boolean hasBackKey = KeyCharacterMap
				.deviceHasKey(KeyEvent.KEYCODE_BACK);

		if (DEBUG) {
			Log.v(TAG, "hasMenuKey=" + hasMenuKey + ", hasBackKey="
					+ hasBackKey);
		}
		if (!hasMenuKey && !hasBackKey) {
			// 做任何你需要做的,这个设备有一个导航栏
			return true;
		}
		return false;
	}
}
