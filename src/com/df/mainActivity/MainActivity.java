package com.df.mainActivity;

import com.df.chessboard.ChessboardActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	private ImageButton btn_hum_com;
	private ImageButton btn_hum_bluet;
	private ImageButton btn_hum_two;
	private ImageButton btn_exit;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
     
        btn_hum_com = (ImageButton)findViewById(R.id.btn_hum_com);
        btn_hum_bluet = (ImageButton)findViewById(R.id.btn_hum_bluet);
        btn_hum_two = (ImageButton)findViewById(R.id.btn_hum_two);
        btn_exit = (ImageButton)findViewById(R.id.btn_exit);
        
        btn_hum_com.setOnClickListener(this);
        btn_hum_bluet.setOnClickListener(this);
        btn_hum_two.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
            
        SoundPlayer.init(this);
        SoundPlayer.startMusic();
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

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        if(SoundPlayer.isMusicSt())
        	menu.add(0, 0, 0, R.string.music_off);
        else
        	menu.add(0, 0, 0, R.string.music_on);
        return true;
     }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 0){
        	if(SoundPlayer.isMusicSt()){
        		SoundPlayer.setMusicSt(false);
        	}
        	else{
        		SoundPlayer.init(this);
        		SoundPlayer.setMusicSt(true);
        		SoundPlayer.startMusic();
        	}
            return true;
        }
        return false;
    }

}