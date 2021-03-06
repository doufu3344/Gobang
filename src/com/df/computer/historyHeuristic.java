package com.df.computer;

import java.util.Random;

public class historyHeuristic {
	private final static int maxX = 15;
	private final static int maxY = 15;

	private static final int[][] HistoryTable = new int[maxX][maxY];	
	private stonemove[] TargetBuff= new stonemove[maxX*maxY];
	
	public void resetHistoryTable(){
		for(int i=0; i<maxX; ++i)
			for(int j=0; j<maxY; ++j)
				HistoryTable[i][j] = 0;
	}
	public int getHistoryScore(stonemove move){
		return HistoryTable[move.getX()][move.getY()];
	}
	public void enterHistoryScore(stonemove move, int depth){
		HistoryTable[move.getX()][move.getY()] += 2<<depth;
	}
	
	public void mergeSort(stonemove[] source,int n,boolean type){
		int s = 1;
		while(s < n){
			mergePass(source, TargetBuff, s, n, type);
			s += s;
			mergePass(TargetBuff, source, s, n, type);
			s += s;
		}
		int i;
		for(i=1; i<n; ++i)
			if(source[i].getScore() != source[i-1].getScore())
				break;
		if( i > 1){
			Random ran =new Random(System.currentTimeMillis());
			int tran = ran.nextInt(i);
			stonemove trans = new stonemove();
			trans = source[tran];
			source[tran] = source[0];
			source[0] = trans;
			ran =new Random(System.currentTimeMillis());
		}
		int j;
		for(j=n; j>1; --j)
			if(source[j-1].getScore() != source[j-2].getScore())
				break;
		if( j < n ){
			Random ran =new Random(System.currentTimeMillis());
			int tran = ran.nextInt(n - j + 1);
			stonemove trans = new stonemove();
			trans = source[n-tran-1];
			source[n-tran-1] = source[0];
			source[0] = trans;
			ran =new Random(System.currentTimeMillis());
		}
	}

	private void mergePass(stonemove[] source,stonemove[] target,int s,int n,boolean type){
		int i = 0;
		while(i <= n-2*s){
			if(type)
				merge_A(source,target,i,i+s-1,i+2*s-1);
			else
				merge_B(source,target,i,i+s-1,i+2*s-1);
			i = i+2*s;
		}
		if (i + s < n){
			if (type)
				merge_A(source, target, i, i + s - 1, n - 1);
			else
				merge_B(source, target, i, i + s - 1, n - 1);
		}
		else
			for (int j=i; j<=n-1; j++)
				target[j] = source[j];
	}
	private void merge_A(stonemove[] source, stonemove[] target, int l, int m, int r){//从小到大
		int i = l;
		int j = m+1;
		int k = l;
		while((i<=m) && (j<=r)){
				if (source[i].getScore() <= source[j].getScore())
					target[k++] = source[i++];
				else
					target[k++] = source[j++];
		}
		if(i > m)
			for (int q = j; q <= r; q++)
				target[k++] = source[q];
			else
				for(int q = i; q <= m; q++)
					target[k++] = source[q];
	}
	private void merge_B(stonemove[] source, stonemove[] target, int l, int m, int r){//从大到小
		int i = l;
		int j = m + 1;
		int k = l;
		while((i <= m) && (j <= r))
			if (source[i].getScore() >= source[j].getScore())
				target[k++] = source[i++];
			else
				target[k++] = source[j++];
			
		if(i > m)
			for (int q = j; q <= r; q++)
				target[k++] = source[q];
		else
			for(int q = i; q <= m; q++)
				target[k++] = source[q];
	}
	
}