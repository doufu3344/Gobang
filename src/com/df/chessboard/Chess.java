package com.df.chessboard;

public class Chess{
	private int x;
	private int y;
	private int id;
	
	public Chess(int x, int y,int id){
		this.x = x;
		this.y = y;
		this.id=id;
	}
	public int getId(){
		return id;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}

}