package com.fason.kino.Movie;

import java.util.Hashtable;
import java.util.Map;

public class Movie {
	private String title;
	private String description;
	private Map<String, String> facts;
	
	/*
	 * Class constructor
	 * Movie title and description as arguments.
	 */
	public Movie(String title, String desc){
		facts = new Hashtable<String, String>();
		this.title = title;
		this.description = desc;
	}
	
	/*
	 * Put facts
	 */
	public void addFact(String key, String value){
		facts.put(key, value);
	}
	
	/*
	 * Get facts
	 */
	public String getFact(String key){
		return facts.get(key);
	}
	
	/*
	 * Get title
	 */
	public String getTitle(){
		return this.title;
	}
	
	/*
	 * Get description
	 */
	public String getDesc(){
		return this.description;
	}
}
