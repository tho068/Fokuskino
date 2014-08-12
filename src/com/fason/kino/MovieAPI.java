package com.fason.kino;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.androidquery.AQuery;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class MovieAPI {
	
	protected static Boolean ready = false;
	
	protected static String title = null;
	protected static String backdrop = null;
	protected static int score = 0;
	
	protected Context context = null;
	
	protected String api_key = "66a5c28ca89538226d05bbbf099d418b";
	protected String TMDB_BASE_URL="http://api.themoviedb.org/2.1/Movie.search/en/json/";
	
	/*
	 * Constructor method:
	 * Set the needed variabels and 
	 * views
	 */
	public MovieAPI(String title, Context context){
		this.title = title;
		this.context = context;
		
		/*
		 * Populate the class variables
		 * with values from the
		 * api call
		 */
		
		populateClass();
	}
	
	protected void populateClass(){
		/*
		 * Set up a new thread and do
		 * an API call to populate the class
		 * with the movie title and so on
		 */
		new Thread(new Runnable(){
			public void run(){
				try {
					String query = title.replace(" ", "+");
					String host = "http://api.themoviedb.org/3/search/movie";
					String URL = host + "?api_key=" + api_key + "&query=" + query;
					
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(URL);
					
					// Get the response
					HttpResponse response = client.execute(get);
					
					SingleView.out(response);
					
					// Parse the data, and create JSONObject
					BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
					String json = reader.readLine();
					JSONTokener tokener = new JSONTokener(json);
					
					JSONObject result = new JSONObject(tokener);
					
					JSONArray movies = result.getJSONArray("results");
					
					// Get movie from index 0, as this is the most relevant movie
					JSONObject movie = movies.getJSONObject(0);
					
					// We have a JSONObject, procede to claim needed data.
					if(movie != null){
						/*
						 * Store data in a Bundle and
						 * send it to thread callback
						 * handler
						 */
						Message msg = handler.obtainMessage();
						Bundle bundle = new Bundle();
						bundle.putString("backdrop", movie.getString("backdrop_path"));
						bundle.putInt("imdb_score", movie.getInt("vote_average"));
						msg.setData(bundle);
						handler.sendMessage(msg);
					}
				}
				catch(Exception e){
					SingleView.out(e);
				}
			}
		}).start();;
	}
	/*
	 * Thread callback handler
	 * Update the class with new data
	 * from API
	 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg){
			Bundle b = msg.getData();
			int score = b.getInt("imdb_score");
			String backdrop = b.getString("backdrop");
			
			// Set ready
			MovieAPI.setReady();
			
			// Set score
			MovieAPI.setScore(score);
			
			// Set backdrop
			MovieAPI.setBackdrop(backdrop);
		}
	};
	
	public static void setReady(){
		MovieAPI.ready = true;
		SingleView.out("Vi kommer til setReady");
	}
	
	public static void setScore(int s){
		MovieAPI.score = s;
	}
	
	public static void setBackdrop(String b){
		MovieAPI.backdrop = b;
	}

	
	
}
