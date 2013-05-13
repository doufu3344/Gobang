package com.df.chessboard;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
import android.util.Log;
import android.view.KeyEvent;
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
public class ChessboardActivity extends Activity implements OnClickListener{

	private static final String TAG = "ChessboardActivity";
    private static final boolean D = false;	
	
	private static int Who;//当前下棋人
	private static int Winner;//胜出者
	private static boolean IsBegin;
	private static boolean IsRun = false;
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
	private static boolean IsConnect = false;

    private TextView mTitle;
	
    Timer timer = new Timer();
    Handler handler;
	int t_black=0;
	int t_white=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.chessboard);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);
		
		if(Mode == 1)
			Who = IsFirst;
		else
			Who = 0;
		Winner = -1;
		IsTip =0;
		Look = 0;
		one = true;
	
		btn_undo = (Button) this.findViewById(R.id.undo);
		btn_back = (Button) this.findViewById(R.id.back);
		btn_undo.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_replay = (Button) this.findViewById(R.id.replay);
		btn_replay.setOnClickListener(this);
		btn_replay.setVisibility(View.INVISIBLE);
		
		final TextView time_black = (TextView)this.findViewById(R.id.blacktime);
		final TextView time_white = (TextView)this.findViewById(R.id.whitetime);
	    // 定义Handler
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
							super.handleMessage(msg);
				//handler处理消息
				if(msg.arg1==1 && msg.arg2==0){//黑子	
					String time = convertTime(t_black++);
					time_black.setText(time);
				}
				else if(msg.arg1==1 && msg.arg2==1){//白子
					String time = convertTime(t_white++);
					time_white.setText(time);
				}
			}
		};
		
		Intent intent = getIntent();  
		Mode = intent.getFlags();
		if(Mode ==1){
			
			TipView tv = (TipView)this.findViewById(R.id.tipview);
			tv.SetMode(Mode);
			
			btn_replay.setText(R.string.giveup);
			
	        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	        
	        if (mBluetoothAdapter == null) {
	            Toast.makeText(this, getString(R.string.bluetooth_unavailable), Toast.LENGTH_LONG).show();
	            finish();
	            return;
	        }

			Intent serverIntent = new Intent(this, com.df.bluetooth.DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
		}
	
		Time();
	}
	
    @Override
    public void onStart() {
    	super.onStart();
    	if(D) Log.e(TAG, "++ ON START ++");
    	if(Mode == 1){
	    	// If BT is not on, request that it be enabled.
	    	// setupChat() will then be called during onActivityResult
	    	if (!mBluetoothAdapter.isEnabled()) {
	    		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	    		// Otherwise, setup the chat session
	    	} else {
	    		if (mBluetoothService == null)
	    			mBluetoothService = new BluetoothService(this, mHandler);
	    	}
    	}
    }
	
    @Override
    public synchronized void onPause() {
    	super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

   @Override
    public void onStop() {
	   super.onStop();
	   if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    	// Stop the Bluetooth services
    	if(Mode == 1)
    		if (mBluetoothService != null) mBluetoothService.stop();
    	if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
    @Override
    public synchronized void onResume() {
    	super.onResume();
    	if(D) Log.e(TAG, "+ ON RESUME +");
	
    	// Performing this check in onResume() covers the case in which BT was
    	// not enabled during onStart(), so we were paused to enable it...
    	// onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
    	if(Mode == 1)
	    	if (mBluetoothService != null) {
	           // Only if the state is STATE_NONE, do we know that we haven't started already
	    		if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
	    			// Start the Bluetooth services
	    			mBluetoothService.start();
	    		}
	    	}
    }

	
	private void Time(){
		timer.schedule(new TimerTask() {
			// TimerTask 是个抽象类,实现的是Runable类
			public void run() {
				//定义一个消息传过去
				Message msg = new Message();
				if(IsRun){
					if(Who == 0){
						msg.arg1 = 1;
						msg.arg2 = 0;
					}
					else if(Who == 1){
						msg.arg1 = 1;
						msg.arg2 = 1;
					}
					else{
						msg.arg1 = 0;
						msg.arg2 = 0;
					}
					handler.sendMessage(msg);
				}
			}
		},0, 1000);	
	}

	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
            	IsConnect = false;
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                    if(Mode == 1){
                    	mTitle.setText(R.string.title_connected_to);
                    	mTitle.append(" ");
                    	mTitle.append(mConnectedDeviceName);
                    	IsConnect = true;
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
                	break;
                case BluetoothService.STATE_NONE:
                    if(Mode == 1){
                    	mTitle.setText(R.string.title_not_connected);
                    }
                    break;
                }
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), getString(R.string.title_connected_to)
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
           		
                String thisname = BluetoothAdapter.getDefaultAdapter().getName();
                
                if(mConnectedDeviceName.compareTo(thisname)<0)
           			IsFirst = 0;
           		else
           			IsFirst = 1;
        		
                if(D) Log.d(TAG,"+++IsFirst:"+String.valueOf(IsFirst)+"++++");

                setFirst();
                break;
            case MESSAGE_TOAST:
            	String str= "";
            	if(msg.getData().getString(TOAST).equals("connect_error"))
            		str = getString(R.string.unable_connect);
            	if(msg.getData().getString(TOAST).equals("connection_lost")){
            		str = getString(R.string.connection_lost);
            		if(Mode == 1 && IsConnect){
                		IsRun = false;
                     	one = true;
                     	IsConnect = false;
                     	finish();
                     }
            	}
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                break;
	        case MESSAGE_READ:
	            byte[] readBuf = (byte[]) msg.obj;
	            // construct a string from the valid bytes in the buffer
	            String readMessage = new String(readBuf, 0, msg.arg1);
	            message_read = readMessage;
	            
	            if(D) Log.d(TAG,"++message= "+message_read);
	            
	            ReadMessage();
	            
	            break;
            }
        }
        
    };
    
    private void ReadMessage(){
        if(D)
        	Toast.makeText(getApplicationContext(), message_read, Toast.LENGTH_SHORT).show();

    	if(message_read.length()==19){
			String data[] = new String[5];
			data = message_read.split("-", 5);
			int who = Integer.parseInt(data[0]);
			Who = who==0?1:0;
			int a = Integer.parseInt(data[1]);
			int b = Integer.parseInt(data[2]);
			ChessboardView view = (ChessboardView)ChessboardActivity.this.findViewById(R.id.view1);
			view.InsertChess(a,b,who);
			view.Refresh();
			if((Winner = view.GetWin()) != 0){
				IsBegin = true;	
			}
			else if(Winner != -1){
				IsRun = false;
			}
			Check();
			
			if(one){
				IsFirst = Who;
				IsRun = true;
				one = false;
			}
			
			TextView textv = (TextView)ChessboardActivity.this.findViewById(R.id.textView1);
			textv.setText(null);
			
			TipView tipview = (TipView)ChessboardActivity.this.findViewById(R.id.tipview);
			tipview.SetWho(Who);
			tipview.SetFirst(IsFirst);
			tipview.SetpointSize(view.GetpointSize());
			tipview.setVisibility(View.VISIBLE);
			tipview.Refresh();
			
			String time_b[] = new String[2];
			time_b = data[3].split(":",2);
			String time_w[] = new String[2];
			time_w = data[4].split(":",2);
			t_black = Integer.parseInt(time_b[0])*60+Integer.parseInt(time_b[1]);
			t_white = Integer.parseInt(time_w[0])*60+Integer.parseInt(time_w[1]);
		}
		else if(message_read.length()==5){//悔棋
			String data[] = new String[2];
			data = message_read.split("-", 2);
			final int who = Integer.parseInt(data[0]);
			int a = Integer.parseInt(data[1]);
			if(a == 000){//同意悔棋
				Toast.makeText(ChessboardActivity.this, getString(R.string.agree_toast), Toast.LENGTH_LONG).show();
				ChessboardView view = (ChessboardView)ChessboardActivity.this.findViewById(R.id.view1);
				Who = view.undo(IsFirst);
			}
			else if(a == 111){//不同意
				Toast.makeText(ChessboardActivity.this,  getString(R.string.disagree_toast), Toast.LENGTH_LONG).show();
			}
			else if( a == 100){//要求悔棋
				Dialog dialog = new AlertDialog.Builder(this).
						setIcon(android.R.drawable.btn_star).setTitle(R.string.ask_undo_title).setMessage(R.string.ask_undo_message).
						setPositiveButton(R.string.agree, new DialogInterface.OnClickListener(){
							@Override//agree
							public void onClick(DialogInterface dialog, int which) {
								int w = who==0?1:0;
								String agree = String.valueOf(w)+"-000";
								sendMessage(agree);
								who_undo = who;
								ChessboardView view = (ChessboardView)ChessboardActivity.this.findViewById(R.id.view1);
								Who = view.undo(who_undo);
							}
						}).
						setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								int w = who==0?1:0;
								String disagree = String.valueOf(w)+"-111";
								sendMessage(disagree);
							}
					}).create();
					dialog.show();
			}
		}
		else if(message_read.length()==6){//quit&&replay
			final TextView time_black = (TextView)this.findViewById(R.id.blacktime);
			final TextView time_white = (TextView)this.findViewById(R.id.whitetime);
			String data[] = new String[2];
			data = message_read.split("-", 2);
			int a = Integer.parseInt(data[1]);
			if(a == 0000){//quit
				one = true;
				t_black = 0;t_white = 0;
				time_black.setText(null);time_white.setText(null);
				IsRun = false;
				timer.cancel();
				Who = 0;
				Toast.makeText(ChessboardActivity.this, getString(R.string.quit_toast), Toast.LENGTH_LONG).show();
				finish();
			}
			else if(a == 1111){//replay
				IsTip =0;
				Winner = -1;
				Look = 0;
				IsBegin = false;
				Who = IsFirst;
				ChessboardView view= (ChessboardView) ChessboardActivity.this.findViewById(R.id.view1);
				view.Initboard();
				btn_replay.setVisibility(View.INVISIBLE);
				message_read = " ";
				TipView tview= (TipView) ChessboardActivity.this.findViewById(R.id.tipview);
				tview.SetWho(IsFirst);
				tview.SetFirst(IsFirst);
				tview.Refresh();
				one = true;
				t_black = 0;t_white = 0;
				time_black.setText(null);time_white.setText(null);
				IsRun = false;
				Toast.makeText(ChessboardActivity.this, R.string.replay_tip, Toast.LENGTH_LONG).show();
			}
		}
		else if(message_read.length()==4){//先后手
			if(message_read.equals("1111")){//对方设置我为先手
				IsFirst = 0;
				Who = 0;
				Toast.makeText(ChessboardActivity.this, R.string.first_toast, Toast.LENGTH_LONG).show();
			}
			else if(message_read.equals("0000")){//对方为先手
				IsFirst = 1;
				Who = 1;
				Toast.makeText(ChessboardActivity.this, R.string.second_toast, Toast.LENGTH_LONG).show();
			}
			
		}
		else if(message_read.length()==1){
			if(message_read.equals("1")){
				setFirst();			
			}
		}
    }

    private void setFirst(){
		if(D) Log.d(TAG, "+++set Isfirst  OUTOUTOUT  ++++");
		try {
		    Thread.sleep(1000);
		    if(D) Log.d(TAG,"+++sleep 1s++++");
		}catch(InterruptedException e)
		{
			if(D) Log.d(TAG,"+++sleep 1s ferror++++");
		}
		if(IsFirst == 1){
			/*Dialog dialogA = new AlertDialog.Builder(this).
	    			setIcon(android.R.drawable.btn_star).setTitle(R.string.set_first_ti).setMessage(R.string.is_you_first).
	    			setPositiveButton(R.string.quit_yes, new DialogInterface.OnClickListener(){
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) {
	    					
	    				}
	    			}).
	    			setNegativeButton(R.string.quit_no, new DialogInterface.OnClickListener() {
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) {

	    				}
	    		}).create();
	    		dialogA.show();*/
			if(D) Log.d(TAG, "+++set Isfirst  INININI  ++++");
			Random ran =new Random(System.currentTimeMillis()); 
			int a = ran.nextInt(2);
			if(a == 0){
				ChessboardActivity.this.sendMessage("0");
				Dialog dialog = new AlertDialog.Builder(this).
		    			setIcon(android.R.drawable.btn_star).setTitle(R.string.set_first_ti).setMessage(R.string.is_you_first).
		    			setPositiveButton(R.string.quit_yes, new DialogInterface.OnClickListener(){
		    				@Override
		    				public void onClick(DialogInterface dialog, int which) {
		    					Toast.makeText(ChessboardActivity.this, R.string.first_toast, Toast.LENGTH_LONG).show();
		    					sendMessage("0000");
		    					IsFirst = 0;
		    					Who = 0;
		    				}
		    			}).
		    			setNegativeButton(R.string.quit_no, new DialogInterface.OnClickListener() {
		    				@Override
		    				public void onClick(DialogInterface dialog, int which) {
		    					Toast.makeText(ChessboardActivity.this, R.string.set_other_first, Toast.LENGTH_LONG).show();
		    					sendMessage("1111");
		    					IsFirst = 1;
		    					Who = 1;
		    				}
		    		}).create();
		    		dialog.show();
			}
			else{
				ChessboardActivity.this.sendMessage("1");
			}
		}
	}

	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(D) Log.d(TAG, "+ onActivityResult requestcode +"+String.valueOf(requestCode));
		if(D) Log.d(TAG, "+ onActivityResult resultcode +"+String.valueOf(resultCode));

		switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras().getString(com.df.bluetooth.DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                	// Get the BLuetoothDevice object
               	if(!address.equals("error") && !address.equals("go_pair")){
               		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

               		// Attempt to connect to the device
               		mBluetoothService.connect(device);
               		
               		String str = device.getName()+" "+getString(R.string.which_is_connecting);
               		
    				Who = IsFirst;
               		
               		Toast.makeText(ChessboardActivity.this, str, Toast.LENGTH_LONG).show();
               		
               		setFirst();
               	}
               	else if(address.equals("go_pair")){
               		Intent serverIntent = new Intent(this, com.df.bluetooth.DeviceListActivity.class);
        			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
               	}
               	else{
               		Toast.makeText(ChessboardActivity.this, getString(R.string.reselect_device), Toast.LENGTH_LONG).show();
               		finish();
               	}
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
            	mBluetoothService = new BluetoothService(this, mHandler);
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bluetooth_unavailable, Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        }
		
		if(D) Log.d(TAG, "+ onActivityResult end +"+String.valueOf(resultCode));
	}
	
	protected void keyDialog() {
		AlertDialog.Builder builder = new Builder(ChessboardActivity.this);
		builder.setMessage(R.string.return_confirm);
		builder.setTitle(R.string.player);
		
		builder.setPositiveButton(R.string.quit_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Who = 0;
				timer.cancel();
				IsRun = false;
				t_black = 0;t_white = 0;
				final TextView time_black = (TextView)ChessboardActivity.this.findViewById(R.id.blacktime);
				final TextView time_white = (TextView)ChessboardActivity.this.findViewById(R.id.whitetime);
				time_black.setText(null);time_white.setText(null);
				finish();
			}
		});
		builder.setNegativeButton(R.string.quit_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Toast.makeText(ChessboardActivity.this, R.string.go_on_playing, Toast.LENGTH_LONG).show();
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
						Toast.makeText(ChessboardActivity.this, R.string.bad_boy,Toast.LENGTH_LONG).show();
						ChessboardView view= (ChessboardView)ChessboardActivity.this.findViewById(R.id.view1);
						
						if(Mode == 0){
							who_undo=0;
							Who = view.undo(who_undo);
						}
						else if(Mode == 1){
							who_undo = IsFirst;
							sendMessage(String.valueOf(who_undo)+"-100");
							Toast.makeText(ChessboardActivity.this, getString(R.string.wait_agree),Toast.LENGTH_LONG).show();
						}
						else{
							who_undo = Who==0?1:0;
							Who = view.undo(who_undo);
						}
						Winner = -1;
						IsBegin = true;
						Look = 0;
					}
				}).
				setNegativeButton(R.string.quit_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(ChessboardActivity.this, R.string.good_boy, Toast.LENGTH_LONG).show();
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
							if(Mode == 1){
								String str = String.valueOf(IsFirst)+"-0000";
								sendMessage(str);
							}
							IsRun = false;
							Who = 0;
							one = true;
							timer.cancel();
							t_black = 0;t_white = 0;
							final TextView time_black = (TextView)ChessboardActivity.this.findViewById(R.id.blacktime);
							final TextView time_white = (TextView)ChessboardActivity.this.findViewById(R.id.whitetime);
							time_black.setText(null);time_white.setText(null);
							finish();
						}
					}).
					setNegativeButton(R.string.quit_no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(ChessboardActivity.this, R.string.go_on_playing, Toast.LENGTH_LONG).show();
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
						Look = 0;
						IsBegin = false;
						Who = IsFirst;
						view.Initboard();
						btn_replay.setVisibility(View.INVISIBLE);
						btn_undo.setVisibility(View.VISIBLE);

						t_black = 0;t_white = 0;
						IsRun = false;
						TipView tview= (TipView) ChessboardActivity.this.findViewById(R.id.tipview);
						tview.SetWho(IsFirst);
						tview.SetFirst(IsFirst);
						tview.Refresh();
						if(Mode == 1){
							String str = String.valueOf(Who)+"-1111";
							sendMessage(str);
						}
						one = true;
						
						final TextView time_black = (TextView)ChessboardActivity.this.findViewById(R.id.blacktime);
						final TextView time_white = (TextView)ChessboardActivity.this.findViewById(R.id.whitetime);
						time_black.setText(null);time_white.setText(null);
						Toast.makeText(ChessboardActivity.this, R.string.replay_tip, Toast.LENGTH_LONG).show();
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
		tipview.SetFirst(IsFirst);
		tipview.SetpointSize(view.GetpointSize());
		tipview.Refresh();
				
		if( Winner==-1){//游戏仍在进行没有结束
			if(Mode == 0){
				if(one)
					Who = 0;
				if(Who==0){
					if(view.InsertChess(event.getRawX(),event.getRawY(),Who)){
						if(one){
							IsRun = true;
							one = false;
						}
						if((Winner = view.GetWin()) != 0){
							IsBegin = true;
							Who =1;
						}
					}
				}
				else if(Who == 1 && !one){
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
				
			}//人机模式		
			if(Mode == 1){				
				//Who = IsFirst;
				if(IsFirst==0 && Who == 0){

					if(view.InsertChess(event.getRawX(),event.getRawY(),Who)){
						if(one){
							IsRun = true;
							one = false;
						}
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
										
						sendMessage(String.valueOf(Who)+"-"+sa+"-"+sb+"-"+convertTime(t_black)+"-"+convertTime(t_white));
						
						if((Winner = view.GetWin()) != 0){
							IsBegin = true;
							Who =1;
						}
					}
				}
				else if(IsFirst==1 && Who == 1 && !one){

					if(view.InsertChess(event.getRawX(),event.getRawY(),Who)){
						//IsRun = true;
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
										
						sendMessage(String.valueOf(Who)+"-"+sa+"-"+sb+"-"+convertTime(t_black)+"-"+convertTime(t_white));
						
						if((Winner = view.GetWin()) != 0){
							IsBegin = true;
							Who =0;
						}
					}
				}
			}
			if(Mode == 2){
				if(one){
					Who = 0;
					one = false;
				}
				if(view.InsertChess(event.getRawX(),event.getRawY(),Who)){
					if(one)
						IsRun = true;
					IsBegin = true;
					if((Winner = view.GetWin()) != 0)
						Who = Who==0?1:0;
				}
			}//人人对战模式
		}
		view.Refresh();
		Check();
		
		tipview.Refresh();
		if(IsBegin == true){
			TextView textv = (TextView)this.findViewById(R.id.textView1);
			textv.setText(null);
			btn_replay.setVisibility(View.VISIBLE);
		}
		return true;
	}
	
	private void Check(){
		if(Winner != -1 && IsTip ==0 && Look != 1){//显示结果对话框
			IsRun = false;
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
						Who = 2;
						IsRun = false;
						Look =1;
						IsTip =0;
						IsBegin = true;
						message_read = " ";
						t_black = 0;t_white = 0;
						btn_undo.setVisibility(View.INVISIBLE);
					}
				}).
				setNegativeButton(R.string.replay, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(Mode == 1 && Winner != IsFirst)
							setFirst();
						if(Mode == 1){
							message_read = " ";
							sendMessage(String.valueOf(IsFirst)+"-1111");
						}
						Who = 2;
						Look = 0;
						IsTip =0;
						Winner = -1;
						Who = IsFirst;
						IsBegin = false;
						one = true;
						btn_replay.setVisibility(View.INVISIBLE);
						t_black = 0;t_white = 0;
						IsRun = false;
						final TextView time_black = (TextView)ChessboardActivity.this.findViewById(R.id.blacktime);
						final TextView time_white = (TextView)ChessboardActivity.this.findViewById(R.id.whitetime);
						time_black.setText(null);time_white.setText(null);
						ChessboardView view = (ChessboardView)ChessboardActivity.this.findViewById(R.id.view1);
						view.Initboard();
					}
			}).create();
			dialog.show();
			IsTip = 1;
		}
	}

	private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
		if(IsConnect){
			if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
	            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
	            return;
	        }
	
	        // Check that there's actually something to send
	        if (message.length() > 0) {
	            byte[] send = message.getBytes();
	            mBluetoothService.write(send);
	        }
		}
    }

	private String convertTime(int time){
		if(time <10)
			return "00:0"+String.valueOf(time);
		else if(time <60)
			return "00:"+String.valueOf(time);
		else{
			String min = "";
			String sec = "";
			if(time%60<10)
				sec = "0"+String.valueOf(time%60);
			else
				sec = String.valueOf(time%60);
			if(time/60 <10)
				min = "0"+String.valueOf(time/60);
			else
				min = String.valueOf(time/60);
			return min+":"+sec;
		}
	}
}
