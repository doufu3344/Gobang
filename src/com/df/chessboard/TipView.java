package com.df.chessboard;

import com.df.mainActivity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class TipView extends View{

	private int Who;
	private int IsFirst;
	private int pointSize;
	private int convert_p;
	private final Paint paint;
	Bitmap bitmap1,bitmap2;
	private int Mode;//0-电脑,其他人人

	
	public TipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
	}

	public int SetWho(int who){
		Who=who;
		return Who;
	}
	public int SetMode(int mode){
		Mode=mode;
		return Mode;
	}
	public int SetFirst(int first){
		IsFirst=first;
		return IsFirst;
	}
	
	public int SetpointSize(int pointSize){
		this.pointSize = pointSize;
		return this.pointSize;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas){
		
		TipView view = (TipView)this.findViewById(R.id.tipview);
		LayoutParams lp = view.getLayoutParams();
		lp.width = 10*pointSize;
		lp.height = 3*pointSize;
		view.setLayoutParams(lp);
	
		if(Mode != 1){
			bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.player1);
			if(Mode == 0){
				bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.android);
			}else
				bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.player2);
		}
		else{
			if(IsFirst == 0){
				bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.player1);
				bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.player2_bt);
			}
			else{
				bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.player1_bt);
				bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.player2);

			}
		}
		
		convert_p = pointSize/3;
		Rect rect1 = new Rect(0,0,3*pointSize,3*pointSize);
		Rect rect2 = new Rect(7*pointSize,0,10*pointSize,3*pointSize);
		//canvas.drawBitmap(mBitmap, null, new Rect(0, 0, 200, 200), null);
		paint.setAntiAlias(true);
		canvas.drawBitmap(bitmap1, null, rect1, null);
		canvas.drawBitmap(bitmap2, null, rect2, null);
	
		
		DrawTips(canvas);
		
	}
	
	@SuppressLint("WrongCall")
	public void DrawTips(Canvas canvas){
		
		paint.setAntiAlias(false);
		paint.setColor(Color.RED);
		canvas.drawLine(pointSize*5, 0, pointSize*5,pointSize*4, paint);
		canvas.drawLine(0, 0, pointSize*10, 0, paint);
		//canvas.drawLine(0, 0, 0,pointSize*3, paint);
		//canvas.drawLine(pointSize*10-1, 0, pointSize*10-1,pointSize*3, paint);
		canvas.drawLine(0, pointSize*3-1, pointSize*10,pointSize*3-1, paint);
		paint.setAntiAlias(true);
		if(Who == 0){
			paint.setColor(Color.BLACK);
			canvas.drawCircle(pointSize*4, pointSize*3/2, 2*convert_p, paint);
		}
		else{
			paint.setColor(Color.WHITE);
			canvas.drawCircle(pointSize*6, pointSize*3/2, 2*convert_p, paint);
		}
		Refresh();		
	}
	
    public void Refresh(){  
        //触发onDraw函数  
        TipView.this.invalidate();  
    }

}
