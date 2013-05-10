package com.df.chessboard;

import com.df.bluetooth.BluetoothService;
import com.df.mainActivity.R;
import com.df.player.Player;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class ChessboardACtivity extends Activity implements OnClickListener{

	private static int Who;//当前下棋人
	private static int Winner;//胜出者
	private static boolean IsBegin;
	private static boolean one = true;
	private static int Mode = 0; //0-人机,1-人人联网,2-人人
	private static int IsFirst = 0;//0-我先,1-对手先
	public static int Look = 0;//1-查看棋盘
	public static int IsTip = 0;//比赛结果对话框，设置此标记解决显示两次的问题。1已经提示，0未提示
	public static int who_undo = 0;
	private Button btn_undo,btn_back,btn_replay;
	
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    // Name of the connected device
    private String mConnectedDeviceName = null;
    private String message_read = " ";
    //private String message_write = " ";

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothService mBluetoothService = null;

    private TextView mTitle;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.chessboard);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);
		
		
		Who = IsFirst;
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
	            Toast.makeText(this, getString(R.string.bluetooth_unavailable), Toast.LENGTH_LONG).show();
	            finish();
	            return;
	        }
			
		
	        if (!mBluetoothAdapter.isEnabled()) {
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	        }
	        else{
				if(mBluetoothService == null)
					mBluetoothService = new BluetoothService(this, mHandler);
	        }
			Intent serverIntent = new Intent(this, com.df.bluetooth.DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
		}
		
	}

	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                    if(Mode == 1){
                    	mTitle.setText(R.string.title_connected_to);
                    	mTitle.append(" ");
                    	mTitle.append(mConnectedDeviceName);
            			com.df.bluetooth.DeviceListActivity.activity.finish();
                    }
                    //mConversationArrayAdapter.clear();
                    break;
                case BluetoothService.STATE_CONNECTING:
                    if(Mode == 1){
                    	mTitle.setText(R.string.title_connecting);
            			com.df.bluetooth.DeviceListActivity.activity.finish();
                    }
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                    if(Mode == 1)
                    	mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
            	String str= "";
            	if(msg.getData().getString(TOAST).equals("connect_error"))
            		str = getString(R.string.unable_connect);
            	if(msg.getData().getString(TOAST).equals("connection_lost"))
            		str = getString(R.string.connection_lost);
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                break;
	/*        case MESSAGE_WRITE:
	            byte[] writeBuf = (byte[]) msg.obj;
	            // construct a string from the buffer
	            String writeMessage = new String(writeBuf);
	            message_write = writeMessage;
	            break;*/
	        case MESSAGE_READ:
	            byte[] readBuf = (byte[]) msg.obj;
	            // construct a string from the valid bytes in the buffer
	            String readMessage = new String(readBuf, 0, msg.arg1);
	            message_read = readMessage;
				if(message_read.length()==7){
					String data[] = new String[3];
					data = message_read.split("-", 3);
					int who = Integer.parseInt(data[0]);
					Who = who==0?1:0;
					int a = Integer.parseInt(data[1]);
					int b = Integer.parseInt(data[2]);
					ChessboardView view = (ChessboardView)ChessboardACtivity.this.findViewById(R.id.view1);
					view.InsertChess(a,b,who);
					view.Refresh();
					if((Winner = view.GetWin()) != 0){
						IsBegin = true;
					}
					Check();
					
					if(one)
						IsFirst = Who;
					one = false;
					
					TipView tipview = (TipView)ChessboardACtivity.this.findViewById(R.id.tipview);
					tipview.SetWho(Who);
					tipview.Refresh();
					
					TextView textv = (TextView)ChessboardACtivity.this.findViewById(R.id.textView1);
					textv.setText(null);
				}
	            break;
            }
        }
    };
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras().getString(com.df.bluetooth.DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                	// Get the BLuetoothDevice object
               	if(!address.equals("error")){
               		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

               		// Attempt to connect to the device
               		mBluetoothService.connect(device);
               		
               		String str = device.getName()+" "+getString(R.string.which_is_connecting);
               		
               		String thisname = BluetoothAdapter.getDefaultAdapter().getName();
               		
               		if(device.getName().compareTo(thisname)<0)
               			IsFirst = 0;
               		else
               			IsFirst = 1;
    				Who = IsFirst;

					
				TextView tv = (TextView)this.findViewById(R.id.textView2);
				tv.setText(message_read+"|First:"+String.valueOf(IsFirst));
               		
               		Toast.makeText(ChessboardACtivity.this, str, Toast.LENGTH_LONG).show();
               	}
               	else{
               		Toast.makeText(ChessboardACtivity.this, getString(R.string.reselect_device), Toast.LENGTH_LONG).show();
               		finish();
               	}
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
							who_undo = IsFirst;
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
						Who = IsFirst;
						view.Initboard();
						btn_replay.setVisibility(View.INVISIBLE);
						message_read = " ";
						one = true;
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
		IsFirst = first;
		return IsFirst;
	}
    
	@SuppressLint("NewApi")
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
				//Who = IsFirst;
				if(IsFirst==0 && Who == 0){

					if(view.InsertChess(event.getRawX(),event.getRawY(),Who)){
						one = false;
						int a = view.Coor2SubX(event.getRawX());
						int b = view.Coor2SubY(event.getRawY());				
						String sa = "";
						if(a<10)
							sa = "0"+String.valueOf(a);
						else
							sa = String.valueOf(a);
						String sb = "";
						if(b<10)
							sb = "0"+String.valueOf(b);
						else
							sb = String.valueOf(b);
										
						sendMessage(String.valueOf(Who)+"-"+sa+"-"+sb);
						
						if((Winner = view.GetWin()) != 0){
							IsBegin = true;
							Who =1;
						}
					}
				}
				else if(IsFirst==1 && Who == 1 && !one){

					if(view.InsertChess(event.getRawX(),event.getRawY(),Who)){
						int a = view.Coor2SubX(event.getRawX());
						int b = view.Coor2SubY(event.getRawY());				
						String sa = "";
						if(a<10)
							sa = "0"+String.valueOf(a);
						else
							sa = String.valueOf(a);
						String sb = "";
						if(b<10)
							sb = "0"+String.valueOf(b);
						else
							sb = String.valueOf(b);
										
						sendMessage(String.valueOf(Who)+"-"+sa+"-"+sb);
						
						if((Winner = view.GetWin()) != 0){
							IsBegin = true;
							Who =0;
						}
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
		
		Check();

		TipView tv = (TipView)ChessboardACtivity.this.findViewById(R.id.tipview);
		tv.Refresh();
		if(IsBegin == true){
			TextView textv = (TextView)this.findViewById(R.id.textView1);
			textv.setText(null);
			btn_replay.setVisibility(View.VISIBLE);
		}
		return true;
	}
	
	private void Check(){
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
						message_read = " ";
					}
				}).
				setNegativeButton(R.string.replay, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						IsTip =0;
						Winner = -1;
						Who = IsFirst;
						IsBegin = false;
						message_read = " ";
						one = true;
						btn_replay.setVisibility(View.INVISIBLE);
						ChessboardView view = (ChessboardView)ChessboardACtivity.this.findViewById(R.id.view1);
						view.Initboard();
					}
			}).create();
			dialog.show();
			IsTip = 1;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	
	
    @Override
    public synchronized void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
              mBluetoothService.start();
            }
        }
    }
    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mBluetoothService != null) mBluetoothService.stop();
    }
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mBluetoothService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0);
        }
    }
	
	
	
	
	
}
