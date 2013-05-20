package com.df.computer;

public class computer{
	private final static int maxX = 15;
	private final static int maxY = 15;
//	private final static int exact = 0;
//	private final static int lower_bound = 1;
//	private final static int upper_bound = 2;
	private final static int BlankPos = 0;
	private final static int Black = 1;
	private final static int White = 2;
	private int[][] list;
	private int[][] list_change = new int[maxX][maxY];
	private int aa=14;
	private int ab=14;
	private int SearchDepth = 1;
	private int MaxDepth;
	private stonemove bestMove;
	
	public historyHeuristic history = new historyHeuristic();
//	TranpositionTable table = new TranpositionTable();
	
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
		for(int i=0;i<maxX;++i)
			for(int j=0;j<maxY;++j)
				list_change[i][j] = list[i][j];
		SearchDepth = level;
		MaxDepth = SearchDepth;
//		table.calculateInitHashKey(list_change);
		history.resetHistoryTable();
		NegaScout(MaxDepth, -20000, 20000);		
		aa = bestMove.getX();
		ab = bestMove.getY();
	}
	private int NegaScout(int depth, int alpha, int beta)
	{
		int side = (MaxDepth-depth)%2;
/*		score = table.LookUpHashTable(alpha, beta, depth, side); 
		if (score != 66666) 
			return score;
*/
		if (depth <= 0)	//叶子节点取估值
		{
			valueEvaluate value = new valueEvaluate();
			int score = value.Evaluate(list_change, side );
//			table.EnterHashTable(exact, score, depth, side );
			return score;
		}

		moveGenerator movegen = new moveGenerator();
		int Count = movegen.createPossibleMove(list_change, depth, side);
		history.mergeSort(movegen.getMoveListA(depth), Count, false);
//		for (int i=0; i<Count; ++i) 
//			movegen.setMoveList(depth, i, history.getHistoryScore(movegen.getMoveList(depth, i)));		
//		history.mergeSort(movegen.getMoveListA(depth), Count, false);
		
		stonemove[] sto = new stonemove[maxX*maxY];
		sto = movegen.getMoveListA(depth);
		
		boolean bestmove=false;
//	    boolean eval_is_exact = false;
		int a = alpha;
	    int b = beta;
	    for (int i=0; i<Count; ++i) {
	    	stonemove tmp = new stonemove();
	    	tmp = sto[i];
			MakeMove(tmp, side);
//			table.Hash_MakeMove(tmp, list_change);
			
			int t = -NegaScout(depth-1 , -b, -a );	
			if (t > a && t < beta && i > 0) {
				a = -NegaScout (depth-1, -beta, -t );
//				eval_is_exact = true;
				if(depth == MaxDepth)
					bestMove = tmp;
				bestmove = true;
			}
//			table.Hash_UnMakeMove(tmp, list_change); 
			UnMakeMove(tmp);
			
			if (a < t){
//				eval_is_exact = true;
				a = t;
				if(depth == MaxDepth)
					bestMove = tmp;
			}
			if ( a >= beta ) {
//				table.EnterHashTable(lower_bound, alpha1, depth,side);
				history.enterHistoryScore(tmp, depth);
				return a;
			}
			b = a + 1;                      // set new null window /
		}
		if (bestmove)
			history.enterHistoryScore(bestMove, depth);
//		if (eval_is_exact) 
//			table.EnterHashTable(exact, alpha1, depth,side);
//		else
//			table.EnterHashTable(upper_bound, alpha1, depth,side); 
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