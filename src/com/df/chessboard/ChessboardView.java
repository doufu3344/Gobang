package com.df.chessboard;

import java.util.ArrayList;
import java.util.List;

import com.df.mainActivity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ChessboardView extends View{
	
	private static int maxX=15;
    private static int maxY=15;//棋盘大小15＊15
    private static int XOffset;
    private static int YOffset;//棋盘位置
    private static int pointSize;//棋盘网格大小
	private int whowin=-1;//赢棋人
    private static int Top,Left;
    private Paint paint = new Paint();

    private SoundPool pool;
    private final int sourceid;
    
    public ChessboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		pool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		sourceid = pool.load(context, R.raw.chess, 0);
	}
	
	@SuppressLint("DrawAllocation")
	@Override  
    protected void onDraw(Canvas canvas){
		
		paint.setColor(Color.BLACK);
		DrawChessboardLines(canvas);
		DrawBlackPiont(canvas);	
		DrawChess(canvas);
		
		if(chess.size()>0){//最后一个棋子特殊
			float a,b;
			Chess ch= chess.get(chess.size()-1);
			a = Sub2CoorX(ch.getX());
			b = Sub2CoorY(ch.getY());
			DrawLastBox(canvas,a,b);
		}
	}
	
	@Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
		//设置X、Y座标微调值，目的整个框居中
		pointSize = w/(maxX);
		XOffset = ((w - (pointSize * maxX)) / 2);  
		YOffset = pointSize;
		//YOffset=((h - (pointSize * maxY)) / 2);  
		
		View view = (View)this.findViewById(R.id.view1);
		LayoutParams lp = view.getLayoutParams();
		lp.height = lp.width = w;
		view.setLayoutParams(lp);
		Top=view.getTop();
		Left=view.getLeft();
		
		//创建棋盘上的线条  
		Initboard();
		CreateLines();
		CreateBlackPoints();
    }  

    private class Line{  
        float xStart,yStart,xStop,yStop;  
        public Line(float xStart, float yStart, float xStop, float yStop) {  
            this.xStart = xStart;  
            this.yStart = yStart;  
            this.xStop = xStop;  
            this.yStop = yStop;  
        }  
    }    
	private List<Line> lines = new ArrayList<Line>();
	private void CreateLines(){//产生棋盘上所有的线 
        for (int i = 0; i < maxX; i++) {//竖线  
            lines.add(new Line(XOffset+i*pointSize+pointSize/2, YOffset-pointSize/2, XOffset+i*pointSize+pointSize/2, YOffset+maxY*pointSize-3*pointSize/2));  
        }  
        for (int i = 0; i < maxY; i++) {//横线  
            lines.add(new Line(XOffset+pointSize/2, YOffset+i*pointSize-pointSize/2, XOffset+maxX*pointSize-+pointSize/2, YOffset+i*pointSize-pointSize/2));  
        }  
    }
    private void DrawChessboardLines(Canvas canvas){//画棋盘中的线
        /*for (Line line : lines) {  
            canvas.drawLine(line.xStart, line.yStart, line.xStop, line.yStop, paint);  
        } */
    	int l=lines.size();
    	paint.setAntiAlias(false);
    	for (int i=1;i<l-1;++i) {  
            if(i!=0||i!=maxX-1||i!=maxX)
    		canvas.drawLine(lines.get(i).xStart, lines.get(i).yStart, lines.get(i).xStop, lines.get(i).yStop, paint);  
        }
    	
    	paint.setAntiAlias(true);
    	canvas.drawLine(lines.get(0).xStart, lines.get(0).yStart, lines.get(0).xStop, lines.get(0).yStop, paint);  
    	canvas.drawLine(lines.get(maxX-1).xStart, lines.get(maxX-1).yStart, lines.get(maxX-1).xStop, lines.get(maxX-1).yStop, paint);  
    	canvas.drawLine(lines.get(maxX).xStart, lines.get(maxX).yStart, lines.get(maxX).xStop, lines.get(maxX).yStop, paint);  
    	canvas.drawLine(lines.get(l-1).xStart, lines.get(l-1).yStart, lines.get(l-1).xStop, lines.get(l-1).yStop, paint);  

    }
    
    private void DrawLastBox(Canvas canvas,float a,float b){//画棋最后下的期的框
    	Line[] box = new Line[4];
    	
    	//方框左竖线
    	box[0] = new Line(a-pointSize/2,b-pointSize/2,a-pointSize/2,b+pointSize/2);
    	//方框右竖线
    	box[1] = new Line(a+pointSize/2,b-pointSize/2,a+pointSize/2,b+pointSize/2);
    	//方框上横线
    	box[2] = new Line(a-pointSize/2,b-pointSize/2,a+pointSize/2,b-pointSize/2);
    	//方框下横线
    	box[3] = new Line(a-pointSize/2,b+pointSize/2,a+pointSize/2,b+pointSize/2);
    	
    	paint.setColor(Color.GREEN);
    	for (int i=0;i<4;++i) {  
            canvas.drawLine(box[i].xStart,box[i].yStart,box[i].xStop,box[i].yStop,paint);  
        }
    }

	private List<Point> blackpoints = new ArrayList<Point>();
    private void CreateBlackPoints(){//创建棋盘中五个黑点坐标
    	blackpoints.add(new Point(XOffset+pointSize/2+pointSize*3, YOffset-pointSize/2+pointSize*3));
    	blackpoints.add(new Point(XOffset+pointSize/2+pointSize*3,YOffset-pointSize/2+pointSize*11));
    	blackpoints.add(new Point(XOffset+pointSize/2+pointSize*11,YOffset-pointSize/2+pointSize*3));
    	blackpoints.add(new Point(XOffset+pointSize/2+pointSize*11,YOffset-pointSize/2+pointSize*11));
    	blackpoints.add(new Point(XOffset+pointSize/2+pointSize*7,YOffset-pointSize/2+pointSize*7));
    }
    private void DrawBlackPiont(Canvas canvas){//画棋盘中5个黑点
    	for (Point point : blackpoints) {
    		canvas.drawCircle(point.x, point.y, pointSize/5,paint);
    	}
    }

	public List<Chess> chess = new ArrayList<Chess>();//落子顺序
	public int [][]chess_Array = new int[maxX][maxY];//chess_Array数组，0-无子，1-黑子，2-白子。

	public boolean InsertChess(float x, float y, int id){//坐标做参数插入棋子
		int a = Coor2SubX(x);
		int b = Coor2SubY(y);
		if(IsChess(a,b,id)){
			chess.add(new Chess(a, b ,id));
			chess_Array[a][b]= id+1;
			Refresh();
			if(!chess.isEmpty()){
				Chess che=chess.get(chess.size()-1);
				if(Judge(che))
					whowin=che.getId();
			}
			playSound();
			return true;
		}
		return false;
    }
	public boolean InsertChess(int a, int b, int id){//数组脚标做参数插入棋子
		if(IsChess(a,b,id)){
			chess.add(new Chess(a, b ,id));
			chess_Array[a][b]= id+1;
			Refresh();
			if(!chess.isEmpty()){
				Chess che=chess.get(chess.size()-1);
				if(Judge(che))
					whowin=che.getId();
			}
			playSound();
			return true;
		}
		return false;
    }
	private void playSound(){
		//播放音频，第二个参数为左声道音量;
		//第三个参数为右声道音量;第四个参数为优先级；
		//第五个参数为循环次数:0不循环，-1循环;
		//第六个参数为速率，速率最低0.5最高为2，1代表正常速度  
        pool.play(sourceid, (float)0.5, (float)0.5, 0, 0, 1);
	}
	private boolean IsChess(int a, int b,int id){
		if(a>=0 && a<maxX && b>=0 && b<maxY){
	    		if(chess_Array[a][b]!=0)
	    			return false;
	    	return true;
		}else
			return false;
	}
	
	public void DrawChess(Canvas canvas){//画棋子
		for (Chess ch : chess){
			if(ch.getId() == 1)
				paint.setColor(Color.WHITE);
			if(ch.getId() == 0)
				paint.setColor(Color.BLACK);
			float x = Sub2CoorX(ch.getX());
			float y = Sub2CoorY(ch.getY());
			canvas.drawCircle(x, y, pointSize/2, paint);
		}			
	}
	
	private float Sub2CoorX(int a){//脚标转坐标
		return a*pointSize+XOffset+pointSize/2;
	}
	private float Sub2CoorY(int b){//脚标转坐标
		return b*pointSize+YOffset-pointSize/2;
	}
	public int Coor2SubX(float x){//坐标转脚标
		return (int)(x-Left)/pointSize;
	}
	public int Coor2SubY(float y){//坐标转脚标
		return (int)(y-Top)/pointSize;
	}

	public void SetLocation(int[] location){
		Left = location[0];
		Top = location[1];
	}
	
    public void Refresh(){//触发onDraw函数
        ChessboardView.this.invalidate();  
    }  
 
    public int[][] GetPoints(){//获得棋面情况 
        return chess_Array;
    }
      
    
    public void Initboard(){//初始化棋盘
        chess.clear();  
        for (int i = 0; i < maxX; i++) {  
            for (int j = 0; j < maxY; j++) {  
                chess_Array[i][j] = 0;
            }  
        }
        whowin = -1;
        Refresh();
    }
    
    public int undo(int who){//who请求悔棋
    	if(!chess.isEmpty()){
    		Chess last = chess.get(chess.size()-1);
    		int i;
    		if(last.getId()!=who)
    			i=2;
    		else
    			i=1;
    		for(;i>0 && chess.size()>0;--i){
    			chess_Array[last.getX()][last.getY()] = 0;
    			chess.remove(chess.size()-1);
    			if(chess.size()>0)
    				last = chess.get(chess.size()-1);
    		}
    	}
    	whowin = -1;
    	Refresh();
		return who;
    }
    
    public boolean Judge(Chess chess){//判断是否结束
    	int a,b,id,count;
    	a = chess.getX();
    	b = chess.getY();
    	id = chess.getId()+1;
    	
    	//横向检查，先向左，再向右
    	count = 1;
    	for(int i=0;i<5;++i)
    		if((a-i)>0 && chess_Array[a-i-1][b]==id)
    			++count;
    		else
    			break;
    	int temp = count;
    	for(int i=0;i<5-temp;++i)
    		if((a+i)<maxX-1 && chess_Array[a+i+1][b]==id)
    			++count;
    		else
    			break;
    	if(count==5)
    		return true;
    	
    	//竖向检查，先向上，再向下
    	count = 1;
    	for(int i=0;i<5;++i)
    		if((b-i)>0 && chess_Array[a][b-i-1]==id)
    			++count;
    		else
    			break;
    	temp = count;
    	for(int i=0;i<5-temp;++i)
    		if((b+i)<maxY-1 && chess_Array[a][b+i+1]==id)
    			++count;
    		else
    			break;
    	if(count==5)
    		return true;
    	    	
    	//“”\“方向向检查，先向左上，再向右下
    	count = 1;
    	for(int i=0;i<5;++i)
    		if((a-i)>0 && (b-i)>0 && chess_Array[a-i-1][b-i-1]==id)
    			++count;
    		else
    			break;
    	temp = count;
    	for(int i=0;i<5-temp;++i)
    		if((a+i)<maxX-1 && (b+i)<maxY-1 && chess_Array[a+i+1][b+i+1]==id)
    			++count;
    		else
    			break;
    	if(count==5)
    		return true;
    	
    	//"/"方向检查，先向左下，再向右上
    	count = 1;
    	for(int i=0;i<5;++i)
    		if((a-i)>0 && (b+i)<maxY-1 && chess_Array[a-i-1][b+i+1]==id)
    			++count;
    		else
    			break;
    	temp = count;
    	for(int i=0;i<5-temp;++i)
    		if((a+i)<maxX-1 && (b-i)>0 && chess_Array[a+i+1][b-i-1]==id)
    			++count;
    		else
    			break;
    	if(count==5)
    		return true;
    	return false;
    	
    }
    
    public int SetWhowin(){
    	whowin=-1;
    	return whowin;
    }
    public int[] GetMaxXY(){
    	int [] mxy= new int[]{maxX,maxY};
    	return mxy;
    }
    public int GetpointSize(){
    	return pointSize;
    }
    public int GetWin(){
    	return whowin;
    }
    public int[][] GetList(){
    	return chess_Array;
    }

}
