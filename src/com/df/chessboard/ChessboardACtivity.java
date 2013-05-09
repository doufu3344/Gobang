package com.df.chessboard;

import com.df.mainActivity.R;
import com.df.player.Player;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

public class ChessboardACtivity extends Activity implements OnClickListener{

	private static int Who;//当前下棋人
	private static int Winner;//胜出者
	private static boolean IsBegin;
	private static int Mode = 0; //0-人机,1-人人联网,2-人人
	private static int First = 0;//0-0黑子先,1-白子先
	public static int Look = 0;//1-查看棋盘
	public static int IsTip = 0;//比赛结果对话框，设置此标记解决显示两次的问题。1已经提示，0未提示
	public static int who_undo = 0;
	private Button btn_undo,btn_back,btn_replay;
	
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothAdapter mBluetoothAdapter = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chessboard);

		Who = First;
		Winner = -1;
	
		btn_undo = (Button) this.findViewById(R.id.undo);
		btn_back = (Button) this.findViewById(R.id.back);
		btn_undo.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_replay = (Button) this.findViewById(R.id.replay);
		btn_replay.setOnClickListener(this);
		btn_replay.setVisibility(View.INVISIBLE);
		
		Intent intent = getIntent();  
		Mode = intent.getFlags();
		
		if(Mode ==1){
			
	        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	        if (mBluetoothAdapter == null) {
	            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
	            finish();
	            return;
	        }
			
		
	        if (!mBluetoothAdapter.isEnabled()) {
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	        }
			Intent serverIntent = new Intent(this, com.df.bluetooth.DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
		}
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras().getString(com.df.bluetooth.DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

                // Attempt to connect to the device
//              mChatService.connect(device);

                String str = device.getName()+" "+getString(R.string.whiche_is_connected);
                Toast.makeText(ChessboardACtivity.this, str, Toast.LENGTH_LONG).show();
                
            }
        }
	}
	
	protected void keyDialog() {
		AlertDialog.Builder builder = new Builder(ChessboardACtivity.this);
		builder.setMessage(R.string.return_confirm);
		builder.setTitle(R.string.player);
		
		builder.setPositiveButton(R.string.quit_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton(R.string.quit_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Toast.makeText(ChessboardACtivity.this, R.string.go_on_playing, Toast.LENGTH_LONG).show();
			}
		});
		builder.create().show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			keyDialog();
			return true;
		}
		return false;
	}
	
	public void onClick(View v){

		if(v == btn_undo){
			Dialog dialog = new AlertDialog.Builder(this).
				setIcon(android.R.drawable.btn_star).setTitle(R.string.player).setMessage(R.string.undo_comfirm).
				setPositiveButton(R.string.quit_yes, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(ChessboardACtivity.this, R.string.bad_boy,Toast.LENGTH_LONG).show();
						ChessboardView view= (ChessboardView)ChessboardACtivity.this.findViewById(R.id.view1);
						
						if(Mode == 0)
							who_undo=0;
						else if(Mode == 1)
//*************************************
							who_undo = 0;
//***************************************
						else
							who_undo = Who==0?1:0;
						Who = view.undo(who_undo);
						Winner = -1;
						IsBegin = true;
						Look = 0;
					}
				}).
				setNegativeButton(R.string.quit_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(ChessboardACtivity.this, R.string.good_boy, Toast.LENGTH_LONG).show();
					}
			}).create();
			dialog.show();
		}
		if(v == btn_back){
			Dialog dialog = new AlertDialog.Builder(this).
					setIcon(android.R.drawable.btn_star).setTitle(R.string.player).setMessage(R.string.return_confirm).
					setPositiveButton(R.string.quit_yes, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//System.exit(1);
							finish();
						}
					}).
					setNegativeButton(R.string.quit_no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(ChessboardACtivity.this, R.string.go_on_playing, Toast.LENGTH_LONG).show();
						}
				}).create();
				dialog.show();
		}
		if(v == btn_replay){
			final ChessboardView view= (ChessboardView) this.findViewById(R.id.view1);
			Dialog dialog = new AlertDialog.Builder(this).
				setIcon(android.R.drawable.btn_star).setTitle(R.string.player).setMessage(R.string.replay_confirm).
				setPositiveButton(R.string.quit_yes, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						IsTip =0;
						Winner = -1;
						IsBegin = false;
						Who = First;
						view.Initboard();
						btn_replay.setVisibility(View.INVISIBLE);
						Toast.makeText(ChessboardACtivity.this, R.string.replay_tip, Toast.LENGTH_LONG).show();
					}
				}).
				setNegativeButton(R.string.quit_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
			}).create();
			dialog.show();
		}
	}
	
	public int setMode(int mode){
		Mode = mode;
		return Mode;
	}
	
	public int setFirst(int first){
		First = first;
		return First;
	}
    
	@Override
	public boolean onTouchEvent(final MotionEvent event){
		
		final ChessboardView view= (ChessboardView) this.findViewById(R.id.view1);	
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		view.SetLocation(location);
		
		TipView tipview=(TipView)this.findViewById(R.id.tipview);
		tipview.SetWho(Who);
		tipview.SetMode(Mode);
		tipview.SetpointSize(view.GetpointSize());
		tipview.Refresh();
		
		if( Winner==-1){//游戏仍在进行没有结束
			if(Mode == 0){
				if(Who==0){
					if(view.InsertChess(event.getRawX(),event.getRawY(),Who)){
						if((Winner = view.GetWin()) != 0){
							IsBegin = true;
							Who =1;
						}
					}
				}
	
				Player player = new Player();
				player.SetList(view.GetList());
				
				while(Winner == -1 && Who ==1){//While语句测试时用，当真正ai算法开发后可以改if，不改也可
					player.computerCoor(Who,Mode);
					if(view.InsertChess(Player.Geta(),Player.Getb(),Who))
						if((Winner = view.GetWin()) != 0){
							IsBegin = true;
							Who =0;		
						}
				}		
			}//人机模式		
			if(Mode == 1){
				if(Who==0){
					if(view.InsertChess(event.getRawX(),event.getRawY(),Who)){
						if((Winner = view.GetWin()) != 0){
							IsBegin = true;
							Who =1;
						}
					}
				}
				Player player = new Player();
				player.SetList(view.GetList());
				
				while(Winner == -1 && Who ==1){//While语句测试时用，当真正ai算法开发后可以改if，不改也可
					player.computerCoor(Who,Mode);
					if(view.InsertChess(Player.Geta(),Player.Getb(),Who))
						if((Winner = view.GetWin()) != 0){
							IsBegin = true;
							Who =0;		
						}
				}
			}
			if(Mode == 2){
				if(view.InsertChess(event.getRawX(),event.getRawY(),Who)){
					IsBegin = true;
					if((Winner = view.GetWin()) != 0)
						Who = Who==0?1:0;
				}
			}//人人对战模式
		}
		
		if(Winner != -1 && IsTip ==0 && Look != 1){//显示结果对话框
			String win;
			if(Winner == 0)
				win = this.getString(R.string.win_black);
			else
				win = this.getString(R.string.win_white);
			Dialog dialog = new AlertDialog.Builder(this).
				setIcon(android.R.drawable.btn_star).setTitle(R.string.gameover).setMessage(win).
				setPositiveButton(R.string.watchboard, new DialogInterface.OnClickListener(){
					@Override//返回棋局查看
					public void onClick(DialogInterface dialog, int which) {
						Look =1;
						IsTip =0;
						IsBegin = true;
					}
				}).
				setNegativeButton(R.string.replay, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						IsTip =0;
						Winner = -1;
						Who = First;
						IsBegin = false;
						btn_replay.setVisibility(View.INVISIBLE);
						view.Initboard();
					}
			}).create();
			dialog.show();
			IsTip = 1;
		}
		if(IsBegin == true){
			TextView tv = (TextView)this.findViewById(R.id.textView1);
			tv.setText(null);
			btn_replay.setVisibility(View.VISIBLE);
		}
		return true;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
