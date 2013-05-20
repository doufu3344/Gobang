package com.df.computer;

public class computer{
	private final static int maxX = 15;
	private final static int maxY = 15;

	private final static int BlankPos = 0;
	private final static int Black = 1;
	private final static int White = 2;
	private int[][] list;
	private int[][] list_change = new int[maxX][maxY];

	private int aa=14;
	private int ab=14;
	private int SearchDepth=1;
	private int MaxDepth;
	private stonemove bestMove;
	
	private void MakeMove(stonemove move, int type)
	{
		int tmp;
		if(type == 0)
			tmp = White;
		else
			tmp = Black;
		list_change[move.getX()][move.getY()] = tmp;
	}
	private void UnMakeMove(stonemove move)
	{
		list_change[move.getX()][move.getY()] = BlankPos;
	}
	
	public void SearchAGoodMove(int Type, int level)
	{
		SearchDepth = level;
		for(int i=0;i<maxX;++i)
			for(int j=0;j<maxY;++j)
				list_change[i][j] = list[i][j];
		MaxDepth = SearchDepth;
		NegaScout(MaxDepth, -20000, 20000);
		aa = bestMove.getX();
		ab = bestMove.getY();
	}
	private int NegaScout(int depth, int alpha, int beta)
	{
		int side = (MaxDepth-depth)%2;	
		if (depth <= 0){
			valueEvaluate value = new valueEvaluate();
			int score = value.Evaluate(list_change, side );
			return score;
		}
		moveGenerator movegen = new moveGenerator();
		int Count = movegen.createPossibleMove(list_change, depth, side);	
		
		stonemove[] sto = new stonemove[225];
		sto = movegen.getMoveListA(depth);
		int a = alpha;
		int b = beta;
	    for ( int  i = 0; i < Count; i++ ){
	    	stonemove tmp = new stonemove();
	    	tmp = sto[i];
			MakeMove(tmp, side);
			int t = -NegaScout(depth-1 , -b, -a );
			if (t > a && t < beta && i > 0){
				a = -NegaScout (depth-1, -beta, -t );
				if(depth == MaxDepth)
					bestMove = tmp;
			}
			UnMakeMove(tmp);
			if (a < t){
				a = t;
				if(depth == MaxDepth)
					bestMove = tmp;
			}
			if ( a >= beta ){
				return a;
			}
			b = a + 1;
		}
		return a;
	}

	public void SetList(int[][] l){
		list = l;
	}
	
	public int Geta(){
		return aa;
	}
	public int Getb(){
		return ab;
	}
}