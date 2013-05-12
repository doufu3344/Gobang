package com.df.mainActivity;

import com.df.chessboard.ChessboardActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	private Button btn_hum_com;
	private Button btn_hum_bluet;
	private Button btn_hum_two;
	private Button btn_exit;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     
        btn_hum_com = (Button)findViewById(R.id.btn_hum_com);
        btn_hum_bluet = (Button)findViewById(R.id.btn_hum_bluet);
        btn_hum_two = (Button)findViewById(R.id.btn_hum_two);
        btn_exit = (Button)findViewById(R.id.btn_exit);
        
        btn_hum_com.setOnClickListener(this);
        btn_hum_bluet.setOnClickListener(this);
        btn_hum_two.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {

		if(v == btn_hum_com){
			Intent hum_comIntent = new Intent(MainActivity.this, ChessboardActivity.class);
			hum_comIntent.setFlags(0);
			startActivity(hum_comIntent);
		}
			
		if(v == btn_hum_bluet){
			Intent hum_bluet = new Intent(MainActivity.this, ChessboardActivity.class);
			hum_bluet.setFlags(1);
			startActivity(hum_bluet);
		}
		
		if(v == btn_hum_two){
			Intent hum_two = new Intent(MainActivity.this, ChessboardActivity.class);
			hum_two.setFlags(2);
			startActivity(hum_two);
		}
		
		if(v == btn_exit){
			Dialog dialog = new AlertDialog.Builder(this).
					setIcon(android.R.drawable.btn_star).setTitle(R.string.player).setMessage(R.string.quit_confirm).
					setPositiveButton(R.string.quit_yes, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							android.os.Process.killProcess(android.os.Process.myPid());   //获取PID 
							System.exit(0);
						}
					}).
					setNegativeButton(R.string.quit_no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(MainActivity.this, R.string.go_on_playing, Toast.LENGTH_LONG).show();
						}
				}).create();
				dialog.show();
		}
	}
}