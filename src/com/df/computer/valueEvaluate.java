package com.df.computer;

public class valueEvaluate {

	final private static int maxX = 15;
	final private static int maxY = 15;
	final private static int maxLine = 15;
	final private static int maxType = 8;

	final private static int Blankpos = 0;
	
	final private static int Black = 0;
	final private static int White = 1;
	
	final private static int Analyzed = -1;
	final private static int Not_Analyzed = 0;
	final private static int Five = 1;
	final private static int Four_live = 2;
	final private static int Four_sleep = 3;
	final private static int Three_live = 4;
	final private static int Three_sleep = 5;
	final private static int Two_live = 6;
	final private static int Two_sleep = 7;

	
	int[] LineResult = new int[maxLine];//
	int[][][] TotalResult = new int[maxX][maxY][4];
	private int[][] TypeCount = new int[2][maxType];//
	
	private int[][] PosValue = new int[][]{
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
			{0,1,2,2,2,2,2,2,2,2,2,2,2,1,0},
			{0,1,2,3,3,3,3,3,3,3,3,3,2,1,0},
			{0,1,2,3,4,4,4,4,4,4,4,3,2,1,0},
			{0,1,2,3,4,5,5,5,5,5,4,3,2,1,0},
			{0,1,2,3,4,5,6,6,6,5,4,3,2,1,0},
			{0,1,2,3,4,5,6,7,6,5,4,3,2,1,0},
			{0,1,2,3,4,5,6,6,6,5,4,3,2,1,0},
			{0,1,2,3,4,5,5,5,5,5,4,3,2,1,0},
			{0,1,2,3,4,4,4,4,4,4,4,3,2,1,0},
			{0,1,2,3,3,3,3,3,3,3,3,3,2,1,0},
			{0,1,2,2,2,2,2,2,2,2,2,2,2,1,0},
			{0,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
		};
	
	public int Evaluate(int[][] board, int turnto){

		for(int i=0; i<2; ++i)
			for(int j=0; j<maxType; ++j)
				TypeCount[i][j] = 0;
	
		for(int i=0; i<maxX; ++i)
			for(int j=0; j<maxY; ++j)
				for(int k=0; k<4; ++k)
					TotalResult[i][j][k] = Not_Analyzed;

		for(int i=0; i<maxX; ++i)
			for(int j=0; j<maxY; ++j){
				if(board[i][j] != Blankpos){
					if(TotalResult[i][j][0] == Not_Analyzed)
						Ana_Horizon(board,i,j);
					if(TotalResult[i][j][1] == Not_Analyzed)
						Ana_Vertical(board,i,j);
					if(TotalResult[i][j][2] == Not_Analyzed)
						Ana_Left(board,i,j);
					if(TotalResult[i][j][3] == Not_Analyzed)
						Ana_Right(board,i,j);
				}
			}
//////////////////////////////////////////////////////////////////////////////////////////////		
		for(int i=0; i<maxX; ++i)
			for(int j=0; j<maxY; ++j){
				int posType = board[i][j];
				if(posType != Blankpos){
				for(int k=0; k<4; ++k){
						switch(TotalResult[i][j][k]){
						case Five:
							TypeCount[posType-1][Five]++;break;
						case Four_live:
							TypeCount[posType-1][Four_live]++;break;
						case Four_sleep:
							TypeCount[posType-1][Four_sleep]++;break;
						case Three_live:
							TypeCount[posType-1][Three_live]++;break;
						case Three_sleep:
							TypeCount[posType-1][Three_sleep]++;break;
						case Two_live:
							TypeCount[posType-1][Two_live]++;break;
						case Two_sleep:
							TypeCount[posType-1][Two_sleep]++;break;		
						default:break;
						}//switch
					}//if(posType != Blankpos)
				}//for
			}
		
		if (turnto == Black)
		{
			if (TypeCount[Black][Five]>0)
				return -9999;
			if (TypeCount[White][Five]>0)
				return 9999;
		}
		else
		{
			if (TypeCount[Black][Five]>0)
				return 9999;
			if (TypeCount[White][Five]>0)
				return -9999;
		}
		if (TypeCount[White][Four_sleep] > 1)
			TypeCount[White][Four_live]++;

		if (TypeCount[Black][Four_sleep] > 1)
			TypeCount[Black][Four_live]++;


		int WValue=0,BValue=0;
		if (turnto == Black)
		{

			if (TypeCount[White][Four_live]>0)//白胜
			{
				return 9990;
			}

			if (TypeCount[White][Four_sleep]>0)//白胜
			{
				return 9980;
			}

			if (TypeCount[Black][Four_live]>0 )//黑胜
			{
				return -9970;
			}


			if (TypeCount[Black][Four_sleep]>0 && TypeCount[Black][Three_live]>0)//黑胜
			{
				return -9960;
			}

			if (TypeCount[White][Three_live]>0 && TypeCount[Black][Four_sleep] == 0)
			{
				return 9950;
			}

			if (TypeCount[Black][Three_live] > 1 && 
				TypeCount[White][Four_sleep] == 0 &&
				TypeCount[White][Three_live] == 0 &&
				TypeCount[White][Three_sleep] == 0)
			{
				return -9940;
			}


			if (TypeCount[White][Three_live] > 1)
				WValue += 2000;
			else{
				if (TypeCount[White][Three_live]>0)
					WValue += 200;
			}

			if (TypeCount[Black][Three_live] > 1)
				BValue += 500;
			else{
				if (TypeCount[Black][Three_live]>0)
					BValue += 100;
			}


			if (TypeCount[White][Three_sleep]>0)
				WValue += TypeCount[White][Three_sleep]*10;

			if (TypeCount[Black][Three_sleep]>0)
				BValue += TypeCount[Black][Three_sleep]*10;

			if (TypeCount[White][Two_live]>0)
				WValue += TypeCount[White][Two_live]*4;

			if (TypeCount[Black][Two_live]>0)
				BValue += TypeCount[Black][Two_live]*4;

			if (TypeCount[White][Two_sleep]>0)
				WValue += TypeCount[White][Two_sleep];

			if (TypeCount[Black][Two_sleep]>0)
				BValue += TypeCount[Black][Two_sleep];

		
		}
		else
		{
			if (TypeCount[Black][Four_live]>0)
			{
				return 9990;
			}
			
			if (TypeCount[Black][Four_sleep]>0)
			{
				return 9980;
			}

			if (TypeCount[White][Four_live]>0)
			{
				return -9970;
			}

			if (TypeCount[White][Four_sleep]>0 && TypeCount[White][Three_live]>0)
			{
				return -9960;
			}

			if (TypeCount[Black][Three_live]>0 && TypeCount[White][Four_sleep] == 0)
			{
				return 9950;
			}

			if (TypeCount[White][Three_live] > 1 && 
				TypeCount[Black][Four_sleep] == 0 &&
				TypeCount[Black][Three_live] == 0 &&
				TypeCount[Black][Three_sleep] == 0)
			{
				return -9940;
			}

			if (TypeCount[Black][Three_live] > 1)
				BValue += 2000;
			else{
				if (TypeCount[Black][Three_live]>0)
					BValue += 200;
			}

			if (TypeCount[White][Three_live] > 1)
				WValue += 500;
			else{
				if (TypeCount[White][Three_live]>0)
					WValue += 100;
			}

			

			if (TypeCount[White][Three_sleep]>0)
				WValue += TypeCount[White][Three_sleep]*10;

			if (TypeCount[Black][Three_sleep]>0)
				BValue += TypeCount[Black][Three_sleep]*10;

			if (TypeCount[White][Two_live]>0)
				WValue += TypeCount[White][Two_live]*4;

			if (TypeCount[Black][Two_live]>0)
				BValue += TypeCount[Black][Two_live]*4;

			if (TypeCount[White][Two_sleep]>0)
				WValue += TypeCount[White][Two_sleep];

			if (TypeCount[Black][Two_sleep]>0)
				BValue += TypeCount[Black][Two_sleep];

		}

		for (int i=0; i<maxX; i++)
			for (int j=0; j<maxY; j++)
			{
				int nStoneType = board[i][j];
				if (nStoneType != Blankpos)
				{
					if (nStoneType == 1)
						BValue += PosValue[i][j];
					else
						WValue += PosValue[i][j];
				}
			}
			

		if (turnto == White)
			return BValue - WValue;
		else
			return WValue - BValue;

	}
	
	private int Ana_Horizon(int board[][],int i, int j){//水平方向
		int[] tmp = new int[maxY];
		for(int k=0; k<maxY; ++k)
			tmp[k] = board[k][j];
		analyzeLine(tmp,maxY,i);
		for(int k=0; k<maxY; ++k)
			if(LineResult[k] != Not_Analyzed)
				TotalResult[k][j][0] = LineResult[k];
		return TotalResult[i][j][0];
	}
	private int Ana_Vertical(int board[][],int i, int j){//竖直方向
		analyzeLine(board[i],maxX,j);
		for(int k=0; k<maxX; ++k)
			if(LineResult[k] != Not_Analyzed)
				TotalResult[i][k][1] = LineResult[k];
		return TotalResult[i][j][1];
	}
	private int Ana_Left(int board[][],int i, int j){// ‘\‘方向
		int x,y,l,pos;
		if(i < j){//右上三角
			x = j-i;
			y = 0;
			l = maxX-x;
			pos = i;
		}
		else{//左三角
			x = 0;
			y = i-j;
			pos = j;
			l = maxY-y;
		}
		int[] tmp = new int[l];
		for(int k=0; k<l; ++k)
			tmp[k] = board[y+k][x+k];
		analyzeLine(tmp,l,pos);
		for(int k=0; k<l; ++k)
			if(LineResult[k] != Not_Analyzed)
				TotalResult[y+k][x+k][2] = LineResult[k];
		return TotalResult[i][j][2];		
	}
	private int Ana_Right(int board[][],int i, int j){// ‘／’方向
		int x,y,l,pos;
		if(maxX-j-1>i){
			x = 0;
			y = i+j;
			l = i+j+1;
			pos = j;
		}
		else{
			y = maxY-1;
			x = i+j+1-maxY;
			l = maxX-x;
			pos = maxY-1-i;
		}
		int[] tmp = new int[l];
		for(int k=0; k<l; ++k)
			tmp[k] = board[y-k][x+k];
		analyzeLine(tmp,l,pos);
		for(int k=0; k<l; ++k)
			if(LineResult[k] != Not_Analyzed)
				TotalResult[y-k][x+k][3] = LineResult[k];
		return TotalResult[i][j][3];
	}
		
	private boolean analyzeLine(int[]line, int length, int pos){
		int LeftEdge, RightEdge;
		int LeftRange, RightRange;
		int posType;

		if(length < 5){
			for( int i=0; i<length; ++i)
				LineResult[i] = Analyzed;
			return false;
		}
		
		for( int i=0; i<length; ++i)
			LineResult[i] = Not_Analyzed;
		
		posType = line[pos];
		LeftEdge = pos;
		RightEdge = pos;
		
		while(LeftEdge > 0){
			if(line[LeftEdge-1] != posType)
				break;
			LeftEdge--;
		}
		while(RightEdge < length-1){
			if(line[RightEdge+1] != posType)
				break;
			RightEdge++;
		}
		
		LeftRange = LeftEdge;
		RightRange = RightEdge;

		while( LeftRange > pos-5 && LeftRange > 0){
			if(line[LeftRange-1]==Blankpos || line[LeftRange-1]==posType)
				LeftRange--;
			else
				break;
		}

		while( RightRange < pos+5 && RightRange < length-1){
			if(line[RightRange+1]==Blankpos || line[RightRange+1] == posType)
				RightRange++;
			else
				break;
		}
		
		if(RightRange-LeftRange < 4){//不足5个位置
			for( int i=LeftRange; i<=RightRange; ++i){
				if(line[i] == posType ){
					LineResult[i] = Analyzed;
				}
			}
			return false;
		}

		for( int i=LeftEdge; i<=RightEdge; ++i){
			if(line[i] == posType ){
				LineResult[i] = Analyzed;
			}
		}
		
		if(RightEdge-LeftEdge > 3){//五连
			LineResult[pos] = Five;
			return true;
		}//五连
		
		if(RightEdge-LeftEdge == 3){//四连
			if(LeftRange<LeftEdge && RightRange>RightEdge)
				LineResult[pos] = Four_live;
			else
				LineResult[pos] = Four_sleep;
			return true;
		}//四连
		
		if(RightEdge-LeftEdge == 2){//三连
			if(LeftEdge==LeftRange){
				if(RightEdge<RightRange-1 && line[RightEdge+2]==posType){
					LineResult[pos] = Four_sleep;
					LineResult[RightEdge+2] = Analyzed;
				}
				else
					LineResult[pos] = Three_sleep;
			}
			else if(LeftEdge == LeftRange+1 ){
				if(RightEdge == RightRange-1)
					LineResult[pos] = Three_sleep;
				else
					LineResult[pos] = Three_live;
			}
			else if(LeftEdge > LeftRange+1 ){
				if(line[LeftEdge-2]==posType){
					LineResult[pos] = Four_sleep;
					LineResult[LeftEdge-2] = Analyzed;
				}
				else{
					if(RightEdge==RightRange)
						LineResult[pos] = Three_sleep;
					else if(RightEdge<RightRange-1 && line[RightEdge+2]==posType){
						LineResult[pos] = Four_sleep;
						LineResult[RightEdge+2] = Analyzed;
					}
					else
						LineResult[pos] = Three_live;
				}
			}
			return true;
		}//三连
		
		if(RightEdge-LeftEdge == 1){//二连
			if(LeftEdge == LeftRange){
				if(line[RightEdge+2]==posType){
					LineResult[RightEdge+2] = Analyzed;
					if(line[RightEdge+3]==posType){
						LineResult[pos] = Four_sleep;
						LineResult[RightEdge+3] = Analyzed;
					}
					else{
						LineResult[pos] = Three_sleep;
					}
				}
				else{
					if(line[RightEdge+3]==posType){
						LineResult[pos] = Three_sleep;
						LineResult[RightEdge+3] = Analyzed;
					}
					else{
						LineResult[pos] = Two_sleep;
					}
				}
				return true;
			}//LeftEdge == LeftRange
			if(RightEdge == RightRange){
				if(line[LeftEdge-2]==posType){
					LineResult[LeftEdge-2] = Analyzed;
					if(line[LeftEdge-3]==posType){
						LineResult[pos] = Four_sleep;
						LineResult[LeftEdge-3] = Analyzed;
					}
					else{
						LineResult[pos] = Three_sleep;
					}
				}
				else{
					if(line[LeftEdge-3]==posType){
						LineResult[pos] = Three_sleep;
						LineResult[LeftEdge-3] = Analyzed;
					}
					else{
						LineResult[pos] = Two_sleep;
					}
				}
				return true;
			}//
			if(LeftRange+1 == LeftEdge){
				if(line[RightEdge+2] == posType){
					LineResult[RightEdge+2] = Analyzed;
					if(RightEdge+3 <= RightRange)
						if(line[RightEdge+3]== posType){
							LineResult[RightEdge+3] = Analyzed;
							LineResult[pos] = Four_sleep;
						}
						else
							LineResult[pos] = Three_live;
					else
						LineResult[pos] = Three_sleep;
				}
				else{
					if(RightEdge+3 <= RightRange)
						if(line[RightEdge+3]== posType){
							LineResult[RightEdge+3] = Analyzed;
							LineResult[pos] = Three_sleep;
						}
						else
							LineResult[pos] = Two_live;
					else
						LineResult[pos] = Two_sleep;
				}
				return true;
			}
			if(RightRange-1 == RightEdge){
				if(line[LeftEdge-2] == posType){
					LineResult[LeftEdge-2] = Analyzed;
					if(LeftEdge-3 >= LeftRange)
						if(line[LeftEdge-3]== posType){
							LineResult[LeftEdge-3] = Analyzed;
							LineResult[pos] = Four_sleep;
						}
						else
							LineResult[pos] = Three_live;
					else
						LineResult[pos] = Three_sleep;
				}
				else{
					if(LeftEdge-3 >= LeftRange)
						if(line[LeftEdge-3]== posType){
							LineResult[LeftEdge-3] = Analyzed;
							LineResult[pos] = Three_sleep;
						}
						else
							LineResult[pos] = Two_live;
					else
						LineResult[pos] = Two_sleep;
				}
				return true;
			}
			if(line[LeftEdge-2] != posType && line[RightEdge+2] != posType){
				LineResult[pos] = Two_live;
				return true;
			}
			else if(line[LeftEdge-2] == posType && line[RightEdge+2] != posType){
				if(LeftRange+2<LeftEdge){
					if(line[LeftEdge-3]==posType){
						LineResult[LeftEdge-3] = Analyzed;
						LineResult[pos] = Four_sleep;
					}
					else
						LineResult[pos] = Three_live;
				}
				else
					LineResult[pos] = Three_sleep;
				return true;
			}
			else if(line[LeftEdge-2] != posType && line[RightEdge+2] == posType){
				if(RightRange-2 > RightEdge){
					if(line[RightEdge+3]==posType){
						LineResult[RightEdge+3] = Analyzed;
						LineResult[pos] = Four_sleep;
					}
					else
						LineResult[pos] = Three_live;
				}
				else
					LineResult[pos] = Three_sleep;
				return true;
			}
			else{
				LineResult[pos] = Three_sleep;
				return true;
			}
		}//二连 
		
		if(RightEdge == LeftEdge){//单子
			if(pos == LeftRange){
				if(line[pos+1]==Blankpos && line[pos+3]==Blankpos &&
						line[pos+2]==posType && line[pos+4]==posType){
					LineResult[pos] = Three_sleep;
					LineResult[pos+2] = Analyzed;
					LineResult[pos+4] = Analyzed;
					return true;
				}
			}
			if(pos == RightRange){
				if(line[pos-1]==Blankpos && line[pos-3]==Blankpos &&
						line[pos-2]==posType && line[pos-4]==posType){
					LineResult[pos] = Three_sleep;
					LineResult[pos-2] = Analyzed;
					LineResult[pos-4] = Analyzed;
					return true;
				}
			}
			if(pos > LeftRange+1 && pos < RightRange-1){
				if(line[pos-1]==Blankpos && line[pos+1]==Blankpos &&
						line[pos-2]==posType && line[pos+2]==posType){
					LineResult[pos] = Three_sleep;
					LineResult[pos-2] = Analyzed;
					LineResult[pos+2] = Analyzed;
					return true;
				}
			}
		}//单子*/		
		return false;
	}
}