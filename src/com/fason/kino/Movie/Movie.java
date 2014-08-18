package com.fason.kino.Movie;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Movie {
	private String title;
	private String description;
	private String url;
	private String backdrop;
	private String image;
	
	private Map<String, String> facts;
	
	private List<Theater> mTheater;
	
	/*
	 * Class constructor
	 * Movie title, url, imgurl and description as arguments.
	 */
	public Movie(String title, String desc, String url, String image){
		facts = new Hashtable<String, String>();
		this.title = title;
		this.description = desc;
		this.image = image;
		this.url = url;
		
		// List used to store theater objects.
		this.mTheater = new ArrayList<Theater>();
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
	
	/*
	 * Get url
	 */
	public String getUrl(){
		return this.url;
	}
	
	/*
	 * get image
	 */
	public String getImage(){
		return this.image;
	}
	
	/*
	 * Get backdrop
	 */
	public String getBackdrop(){
		return this.backdrop;
	}
	
	/*
	 * Assign theater object
	 */
	public void setTheater(String theater, String time){
		this.mTheater.add(new Theater(theater, time));
	}
	
	/*
	 * Return list of theather obj
	 */
	public List<Theater> getTheater(){
		return this.mTheater;
	}
}
