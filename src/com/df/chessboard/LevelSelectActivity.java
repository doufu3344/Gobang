package com.df.chessboard;

import com.df.gobang.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

public class LevelSelectActivity extends Activity implements OnClickListener{
	private RadioButton RaBtn_easy;
//	private RadioButton RaBtn_normal;
	private RadioButton RaBtn_hard;
	public static String EXTRA_LEVEL = "computer level";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.levelselect);
		// Set result CANCELED incase the user backs out
		setResult(Activity.RESULT_CANCELED);
		
		RaBtn_easy = (RadioButton) this.findViewById(R.id.radioButtonEasy);
//		RaBtn_normal = (RadioButton) this.findViewById(R.id.radioButtonNormal);
		RaBtn_hard = (RadioButton) this.findViewById(R.id.radioButtonHard);
		RaBtn_easy.setOnClickListener(this);
//		RaBtn_normal.setOnClickListener(this);
		RaBtn_hard.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if( v == RaBtn_easy){
			Intent intent = new Intent();
			intent.putExtra(EXTRA_LEVEL, "easy");
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
/*		if( v == RaBtn_normal){
			Intent intent = new Intent();
			intent.putExtra(EXTRA_LEVEL, "normal");
			setResult(Activity.RESULT_OK, intent);
			finish();
		}*/
		if( v == RaBtn_hard){
			Intent intent = new Intent();
			intent.putExtra(EXTRA_LEVEL, "hard");
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}
		
}
