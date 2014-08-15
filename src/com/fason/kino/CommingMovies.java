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
import android.content.SharedPreferences;
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
        
        /*
         * Execute asynctask
         */
		GetData task = new GetData();
		task.execute();
		
        return rootView;
    }
	
	/*
	 * Get data from server class
	 */
	class GetData extends AsyncTask<Void, Void, List>{
		
		TextView info = (TextView) mRootView.findViewById(R.id.info);
		String server = new String();
		
		protected void onPreExecute(){
			listview.setVisibility(View.INVISIBLE);
			info.setVisibility(View.INVISIBLE);
			
			/*
			 * Check for internet connectivity
			 */
			if(isOnline() == false){
				cancel(true);
			}
		}
		
		protected void getServer(){
			SharedPreferences mSharedPref = mActivity.getSharedPreferences("STORAGE", Context.MODE_PRIVATE);
			this.server = mSharedPref.getString("preferred_theater", "null");
		}
		
		/*
		 * This method is runned when cancel() is called with a positiv
		 * boolean
		 */
		protected void onCancelled(){
			if(isCancelled() == true){
				/*
				 * Cancel has been set to true, here
				 * we handle that
				 */
				
				spinner.setVisibility(View.INVISIBLE);
				info.setVisibility(View.VISIBLE);
				
				if(isOnline() == false){
					// No internet connection, tell the user
					info.setText("Ingen internettforbindelse");
					return;
				}
				
				/*
				 * If we come here, there is no movies today
				 * tell the user about that
				 */
				
				info.setText("Ingen kontakt med server");
				
				return;
				
			}
		}
		
		@Override
		protected List doInBackground(Void... params) {
			
			/*
			 * Go to cancel method if cancelled.
			 */
			if(isCancelled() == true){
				onCancelled();
			}
			
			getServer();
			
			Hashtable<String, Elements> map = new Hashtable<String, Elements>();
			
			try {
				Document doc = (Document) Jsoup.connect(this.server + "/kommende-filmer").get();
				
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
					movie.put("url", this.server + tag.select(".readmoreMobile").attr("href"));
					movie.put("desc", tag.select(".shortenedDescription").text());
					movie.put("image", this.server + tag.select("img").attr("src"));
					
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
			
			// Return null if no internet connection
			return null;
		}
		
		protected void onPostExecute(final List movielist){
			if(isCancelled()){
				onCancelled();
			}
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
