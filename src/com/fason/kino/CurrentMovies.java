package com.fason.kino;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.support.v4.app.Fragment;

public class CurrentMovies extends Fragment {
	
	/*
	 * Rootview and activity
	 */
	public Activity mActivity = getActivity();
	public View mRootView;
	
	public RelativeLayout layout; 
	
	/*
	 * Listview and spinner
	 */
	public ListView listview;
	protected ProgressBar spinner;
	
	/*
	 * Important for getting data from server
	 */
	public String date;	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
 
    	/*
    	 * Get ActionBar and rootview
    	 * for later usage.
    	 */
        View rootView = inflater.inflate(R.layout.fragment_current_movies, container, false);
        mActivity = getActivity();
        mRootView = rootView;
        
        /*
         * Find listview and
         * spinner
         */
        listview = (ListView) mRootView.findViewById(R.id.overview);
        spinner = (ProgressBar) mRootView.findViewById(R.id.spinner);
        
        /*
         * Get date
         */
        this.date = getArguments().getString("date");
        
        /*
         * Execute asynctask
         */
		GetData task = new GetData();
		task.execute(this.date);
		
        return rootView;
    }
	
	/*
	 * Get data from server class
	 */
	class GetData extends AsyncTask<String, Void, List>{
		
		protected void onPreExecute(){
			listview.setVisibility(View.INVISIBLE);
		}
		
		@Override
		protected List doInBackground(String... params) {
			// Check if Internet is present
			if(isOnline() == true){
				Hashtable<String, Elements> map = new Hashtable<String, Elements>();
				
				try {
					Document doc = (Document) Jsoup.connect("http://fokus.aurorakino.no/billetter-og-program/?D=" + params[0]).get();
					
					Elements test = doc.getElementsByClass("showing");
					
					/* No movies today - get movies for tomorrow */
					if(test.size() == 0){
						/*
						 * Here we are going to implement a no movies today
						 * view
						 */
	
						cancel(true);
						
					}
					
					List<Object> listofmovies = new ArrayList<Object>();
			
					// Iterate over HTML tags and fetch needed data
					Iterator<Element> iter = test.iterator();
					while(iter.hasNext()){
						Element tag = (Element) iter.next();
						
						Map<String, Object> movie = new Hashtable<String, Object>();
						List<String> time = new ArrayList<String>();
						
						// Map data for later usage
						movie.put("title", tag.select(".movieTitle").text());
						movie.put("url", "http://fokus.aurorakino.no" + tag.select(".movieTitle").attr("href"));
						movie.put("desc", tag.select(".movieDescription").text());
						movie.put("image", "http://fokus.aurorakino.no" + tag.select(".smallPoster").attr("src"));
						
						Elements times = tag.select(".programTime");
						Iterator<Element> itertimes = times.iterator();
						// Get movietimes
						while(itertimes.hasNext()){
							Element movietime = (Element) itertimes.next();
							time.add(movietime.text());
						}
						
						/*
						 * Assign this text if no description is ava
						 */
						if(tag.select(".movieDescription").text().equals("")){
							movie.put("desc", "Ingen beskrivelse tilgjengelig");
						}
						
						movie.put("time", time);
						
						listofmovies.add(movie);
					}
					
					return listofmovies;
				} 
				catch (IOException e) {
				// TODO Auto-generated catch block
				
				}
			}
			
			// Return null if no internet connection
			return null;
		}
		
		protected void onPostExecute(final List movielist){
			
			// Print error if no network
			if(movielist == null){
				spinner.setVisibility(View.INVISIBLE);	
			}
			else {
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				List<String> title = new ArrayList<String>();
				List<String> subtitle = new ArrayList<String>();
				List<String> image = new ArrayList<String>();
				List<String> timelist = new ArrayList<String>();
				
				listview.setClickable(true);
				listview.setOnItemClickListener(new OnItemClickListener(){
	
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Map moviemap = (Map) movielist.get(position);
						String url = (String) moviemap.get("url");
						String title = (String) moviemap.get("title");
						
						Intent intent = new Intent(getActivity(), SingleView.class);
						intent.putExtra("url", url);
						intent.putExtra("title", title);
						startActivity(intent);
					}
				});
				// Iterate over list of movies
				Iterator movieiter = movielist.iterator();
				while(movieiter.hasNext()){
					Map map = (Hashtable) movieiter.next();
				
					String time = new String();
					
					title.add((String) map.get("title"));
					subtitle.add((String) map.get("desc"));
					image.add((String) map.get("image"));
					
					List timetime = (List) map.get("time");
					Iterator timeiter = timetime.iterator();
					time = (String) timeiter.next();
					while(timeiter.hasNext()){
						time = time + "," + timeiter.next();
					}
					
					// Format the text better
					time = time.replaceAll("(3D)", "");
					time = time.replaceAll("[^\\d:,]", "");
					time = time.replace(",", ", ");
					
					timelist.add(time);
				}
				MovieAdapter adapter = new MovieAdapter(getActivity(), title, image, subtitle, timelist);
				listview.setAdapter(adapter);
				
				spinner.setVisibility(View.INVISIBLE);
				listview.setVisibility(View.VISIBLE);	
			}
		}
		
		public boolean isOnline() {
		    ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		    return (networkInfo != null && networkInfo.isConnected());
		}  
	}
	public static void out(Object msg){
		Log.i("info", msg.toString());
	}
}
