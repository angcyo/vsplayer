package com.linux.vshow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class ScrollFont extends TextView {
	private float textLength = 0f;
	private float y = 0f;
	private float temp_view_plus_text_length = 0.0f;
	private Paint paint = null;
	private String text = "";
	private float speed = 2f;
	private float step = 0f;

	public ScrollFont(Context context) {
		super(context);
	}

	public ScrollFont(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollFont(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void init(float width, int backColor, String fontContent,
			int fontType, int fontColor, float fontSpeed, float fontSize) {
		setIncludeFontPadding(false);
		setBackgroundColor(backColor);
		setTextColor(fontColor);
		if (fontType > 0) {
			try {
				Typeface face = Typeface.createFromAsset(getContext()
						.getAssets(), "fonts/" + fontType + ".ttf");
				setTypeface(face);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		// /0ºÚÌå 1ËÎÌå 2¿¬Ìå 3Á¥Êé 4Ó×Ô² 5ÐÂÎº 6²ÊÔÆ
		speed = fontSpeed;
		setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
		text = fontContent;
		paint = super.getPaint();
		textLength = paint.measureText(text);
		if (step == 0f) {
			step = textLength;
		}
		temp_view_plus_text_length = width + textLength;
		y = fontSize;
		paint.setColor(fontColor);
		invalidate();
	}

	public void onDraw(Canvas canvas) {
		canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
		step += speed;
		if (step > temp_view_plus_text_length + textLength)
			step = textLength;
		invalidate();
	}
}
