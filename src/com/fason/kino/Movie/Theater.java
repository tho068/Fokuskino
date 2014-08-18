package com.fason.kino.Movie;

public class Theater {
	private String theater;
	private String time;
	
	/*
	 * Constructor for the theather class
	 */
	public Theater(String t, String time){
		this.theater = t;
		this.time = time;
	}
	
	/*
	 * Get theater
	 */
	public String getTheather(){
		return this.theater;
	}
	
	/*
	 * Get time
	 */
	public String getTime(){
		return this.time;
	}
}
