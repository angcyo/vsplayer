package com.linux.vshow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

public class ScrollFontLeft extends TextView {
	private float textLength = 0f;
	private float y = 0f;
	private float temp_view_plus_text_length = 0.0f;
	private Paint paint = null;
	private String text = "";
	private float speed = 2f;
	private float step = 0f;

	public ScrollFontLeft(Context context) {
		super(context);
	}

	public ScrollFontLeft(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollFontLeft(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public void init(float width, int backColor, String fontContent,
			int fontType, int fontColor, float fontSpeed, float fontSize,
			float fjianju, int fjiacu, int fdixian, int fxieti, int fzhongxian,
			int fduiqi) {
		fontContent=fontContent.replaceAll("<BR>", "\n");
		float sxd=fjianju-fontSize;
		setLineSpacing(px2dip(getContext(), sxd), 0.85f);
		setBackgroundColor(backColor);
		setTextColor(fontColor);
		if(fjiacu==1)
		{
			setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		}
		if(fdixian==1)
		{
			getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
		}
		if(fxieti==1)
		{
			setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
		}
		if(fzhongxian==1)
		{
			getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG );
		}
		if(fduiqi==0)
		{
            setGravity(Gravity.LEFT);
		}
		else if(fduiqi==1)
		{
			setGravity(Gravity.CENTER);
		}
		else if(fduiqi==2)
		{
			setGravity(Gravity.RIGHT);
		}
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
		setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);
		text = fontContent;
		paint = super.getPaint();
		textLength = paint.measureText(text);
		if (step == 0f) {
			step = textLength;
		}
		temp_view_plus_text_length = width + textLength;
		y = fontSize * 0.9f;
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
