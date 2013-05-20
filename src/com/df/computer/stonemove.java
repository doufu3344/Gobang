package com.df.computer;

public class stonemove {
	private int x;
	private int y;
	private int Score;
	
	public void setCoor(int x,int y){
		this.x = x;
		this.y = y;
	}
	public void setScore(int score){
		Score = score;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getScore(){
		return Score;
	}
}
