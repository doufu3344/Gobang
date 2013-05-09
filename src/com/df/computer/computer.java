package com.df.computer;

public class computer {
	
	private static int a=14;
	private static int b=14;
	private static int[][] list;
	public static int tmp = 0;
	public static void computePositon(int turnto){
		int min = -90000;
		
		valueEvaluate value = new valueEvaluate();

		for(int i=0;i<15;++i)
			for(int j=0;j<15;++j){
				if(list[i][j] == 0){
					list[i][j] = 2;
					tmp = value.Evaluation(list, 1);
					if(tmp > min){
						a=i;
						b=j;
						min = tmp;
					}	
					list[i][j] = 0;
				}
			}
	}

	public void SetList(int[][] l){
		list = l;
	}
	
	public static int Geta(){
		return a;
	}
	public static int Getb(){
		return b;
	}
}
