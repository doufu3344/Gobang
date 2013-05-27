package com.df.computer;

import java.util.Hashtable;
import java.util.Random;

public class TranpositionTable {
	private final static int maxX = 15;
	private final static int maxY = 15;
	private final static int exact = 0;
	private final static int lower_bound = 1;
	private final static int upper_bound = 2;	
	private final static int BlankPos = 0;

	private long[][][] HashKeyA = new long[2][maxX][maxY];
	private long HashKey;
		
	private class HashItem{
		private int entry_type;
		private int depth;
		private int eval;
		public HashItem(int entry_type, int depth, int eval){
			this.entry_type = entry_type;
			this.depth = depth;
			this.eval = eval;
		}
		public int getEntryType(){
			return entry_type;
		}
		public int getDepth(){
			return depth;
		}
		public int getValue(){
			return eval;
		}
	}
	private class Table{
		public Hashtable<Long, HashItem> Table;
	}	
	Table[] tableArray=new Table[2];

	public TranpositionTable(){
		InitializeHashKey();
	}
	private void InitializeHashKey(){
		Random ran =new Random(System.currentTimeMillis());
		for(int k=0; k<2; ++k)
			for(int i=0; i<maxX; ++i)
				for(int j=0; j<maxY; ++j)
					HashKeyA[k][i][j] = ran.nextLong();
		tableArray[0] = new Table();
		tableArray[0].Table = new Hashtable<Long, HashItem>();
		tableArray[1] = new Table();
		tableArray[1].Table = new Hashtable<Long, HashItem>();
	}
	
	public void calculateInitHashKey(int[][] board){
		HashKey = 0L;
		for (int i=0; i<maxX; ++i)
			for (int j=0; j<maxY; ++j)
			{
				int nStoneType = board[i][j];
				if (nStoneType != BlankPos)
					HashKey = HashKey ^ HashKeyA[nStoneType-1][i][j]; 
			}
	}

	public void Hash_MakeMove(stonemove move, int board[][])
	{		
 		int type = board[move.getX()][move.getY()]-1;
		HashKey = HashKey ^ HashKeyA[type][move.getX()][move.getY()];
	}

	public void Hash_UnMakeMove(stonemove move, int board[][]){
		int type = board[move.getX()][move.getY()]-1;
		HashKey = HashKey ^ HashKeyA[type][move.getX()][move.getY()];
	}

	public int LookUpHashTable(int alpha, int beta, int depth,int TableNo){
		long x = HashKey;
		HashItem pht = tableArray[TableNo].Table.get( x );
	    if (pht != null && pht.getDepth() >= depth){
			switch (pht.getEntryType()) 
			{
			case exact: 
				return pht.eval;
			case lower_bound:
				if (pht.getValue() >= beta)
					return pht.getValue();
				else 
					break;
			case upper_bound:
				if (pht.getValue() <= alpha)
					return pht.getValue();
				else 
					break;
	        }
		}
		return 66666;
	}
	public void EnterHashTable(int entry_type, int eval, int depth, int TableNo){
		long x = HashKey;
		HashItem pht = new HashItem(entry_type, depth, eval);
		tableArray[TableNo].Table.put(x, pht);
	}
}