package com.df.computer;

public class moveGenerator {
	private final static int  MAXDEEP = 10;
	private final static int maxX = 15;
	private final static int maxY = 15;
	private final static int BlankPos = 0;
	private stonemove[][] MoveList = new stonemove[MAXDEEP][maxX*maxY];
	private int moveCount = 0;
	
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
	
	public stonemove  getMoveList(int nPly,int x){
		return MoveList[nPly][x];
	}
	public stonemove[]  getMoveListA(int nPly){
		return MoveList[nPly];
	}

	private int addMove(int nToX, int nToY,int nPly)
	{
		MoveList[nPly][moveCount] = new stonemove();
		MoveList[nPly][moveCount].setCoor(nToX, nToY);
		MoveList[nPly][moveCount].setScore(PosValue[nToX][nToY]);
		moveCount++;
		return moveCount;
	}

	public int createPossibleMove(int board[][], int nPly, int nSide)
	{
		moveCount = 0;
		for (int i=0; i<maxX; ++i)
			for (int j=0; j<maxY; ++j)
				if (board[i][j] == BlankPos)
					addMove(i, j, nPly);

		//historyHeuristic history = new historyHeuristic();
		//history.MergeSort(MoveList[nPly], moveCount, false);
		return moveCount;
	}
}