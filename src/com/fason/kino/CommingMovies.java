package com.fason.kino;

import java.io.IOException;
import java.util.ArrayList;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.Fragment;

public class CommingMovies extends Fragment {
	
	/*
	 * Rootview and activity
	 */
	public Activity mActivity = getActivity();
	public View mRootView;
	
	/*
	 * Listview Progressbar and spinner
	 */
	public ListView listview;
	protected ProgressBar spinner;
	protected Spinner selectMonth;

	
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
         * Find listview, spinner and
         * progressbar
         */
        listview = (ListView) mRootView.findViewById(R.id.overview);
        spinner = (ProgressBar) mRootView.findViewById(R.id.spinner);
        selectMonth = (Spinner) mRootView.findViewById(R.id.selectMonth);
        
        /*
         * Populate selectMonth spinner
         */
        populateSpinner();
        
        /*
         * Execute asynctask
         */
		GetData task = new GetData();
		task.execute();
		
        return rootView;
    }
    
    /*
     * Populate the spinner with data
     */
    protected void populateSpinner(){
    	String[] months = {"Januar", "Februar", "Mars"};
    	ArrayAdapter<String> mSpinnerAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, months);
    	selectMonth.setAdapter(mSpinnerAdapter);
    }
	
	/*
	 * Get data from server class
	 */
	class GetData extends AsyncTask<Void, Void, List>{
		
		TextView info = (TextView) mRootView.findViewById(R.id.info);
		
		protected void onPreExecute(){
			listview.setVisibility(View.INVISIBLE);
			info.setVisibility(View.INVISIBLE);
		}
		
		@Override
		protected List doInBackground(Void... params) {
			// Check if Internet is present
			if(isOnline() == true){
				Hashtable<String, Elements> map = new Hashtable<String, Elements>();
				
				try {
					Document doc = (Document) Jsoup.connect("http://fokus.aurorakino.no/kommende-filmer").get();
					
					Elements test = doc.getElementsByClass("MovieItemWrapper");
					
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
						movie.put("title", tag.select(".upcomingTitle a").text());
						movie.put("url", "http://fokus.aurorakino.no" + tag.select(".readmoreMobile").attr("href"));
						movie.put("desc", tag.select(".shortenedDescription").text());
						movie.put("image", "http://fokus.aurorakino.no" + tag.select("img").attr("src"));
						
						/*
						 * Assign this text if no description is ava
						 */
						if(tag.select(".shortenedDescription").text().equals("")){
							movie.put("desc", "Ingen beskrivelse tilgjengelig");
						}
						
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
				
					timelist.add("Ikke implementert");
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
