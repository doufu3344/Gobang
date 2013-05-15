package com.df.chessboard;

/**
 * This is chess class
 */
public class Chess{
	private int x, y;//chess's coordinate
	private int id;//0--black chess;1--white chess;
	
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