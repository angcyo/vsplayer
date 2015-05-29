package com.linux.vshow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

public class ScrollFontUp extends TextView {
	private Paint paint = null;
	private String text = "";
	private float speed = 2f;
	private float step = 0f;
	private List<String> textList = new ArrayList<String>();

	public ScrollFontUp(Context context) {
		super(context);
	}

	public ScrollFontUp(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollFontUp(Context context, AttributeSet attrs, int defStyle) {
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
		paint.setColor(fontColor);
		float length = 0;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			if (length < width) {
				builder.append(text.charAt(i));
				length += paint.measureText(text.substring(i, i + 1));
				if (i == text.length() - 1) {
					textList.add(builder.toString());
				}
			} else {
				textList.add(builder.toString().substring(0,
						builder.toString().length() - 1));
				builder.delete(0, builder.length() - 1);
				length = paint.measureText(text.substring(i, i + 1));
				i--;
			}
		}
	}

	public void onDraw(Canvas canvas) {
		if (textList.size() == 0)
			return;
		for (int i = 0; i < textList.size(); i++) {
			canvas.drawText(textList.get(i), 0, this.getHeight() + (i + 1)
					* paint.getTextSize() - step, getPaint());
		}
		invalidate();
		step = step + speed;
		if (step >= this.getHeight() + textList.size() * paint.getTextSize()) {
			step = 0;
		}
	}
}
