package com.fason.kino;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.androidquery.AQuery;
import com.fason.kino.NotifyingScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SingleView extends Activity {

	private Drawable mActionBarBackgroundDrawable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set overlaymode actionbar
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		
		setContentView(R.layout.activity_single_view);
		
		// Set actionbar title and hide it so that it fades
		getActionBar().setTitle(getIntent().getStringExtra("title")); 
	    getActionBar().setDisplayShowHomeEnabled(true);
	    getActionBar().setDisplayShowTitleEnabled(false);
		
	    // Fading action bar init
	     mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.ab_solid_actionbar_fason);
	     mActionBarBackgroundDrawable.setAlpha(0);
	     getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);
	     ((NotifyingScrollView) findViewById(R.id.scrollview)).setOnScrollChangedListener(mOnScrollChangedListener);
	     
		// Get rating
		getRating();
				
		// Up navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Run GetData task
		GetData task = new GetData();
		task.execute(getIntent().getStringExtra("url"));
	}
	
	// Scroll callback for fading actionbar
	private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = findViewById(R.id.poster).getHeight() - getActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
            
            // Show actionbar title and text
            if (newAlpha != 0){
            	getActionBar().setDisplayShowHomeEnabled(true);
            	getActionBar().setDisplayShowTitleEnabled(true);
            }
            // hide actionbar title and text
            else {
            	getActionBar().setDisplayShowHomeEnabled(true);
            	getActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    };
    
	public boolean isOnline() {
	    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    return (networkInfo != null && networkInfo.isConnected());
	}
	
	// Method to get rating and fill ratingbar
	protected void getRating(){
	    new Thread(new Runnable() {
	        public void run() {
	        	
	        	/*
	        	 * API key for the open movie db
	        	 * Get the movie title, and do a most relevant
	        	 * search
	        	 */
	        	String api_key = "66a5c28ca89538226d05bbbf099d418b";
	        	String title = getIntent().getStringExtra("title").replace(" ", "+");
				String query = title.replace(" ", "+");
				String host = "http://api.themoviedb.org/3/search/movie";
				String URL = host + "?api_key=" + api_key + "&query=" + query;
				
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(URL);
				
				if (isOnline() == true){
		        	try {
						HttpResponse response = client.execute(get);
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
						String json = reader.readLine();
						JSONTokener tokener = new JSONTokener(json);
						JSONObject finalResult = new JSONObject(tokener);
						
						JSONArray array = finalResult.getJSONArray("results");
						
						/*
						 * Get index 0 - the most
						 * relevant result
						 */
						JSONObject movie = array.getJSONObject(0);
						
						/*
						 * Populate rating and backdrop img
						 */
						setRating(Float.parseFloat(movie.getString("vote_average")));
						setBackDrop(movie.getString("backdrop_path"));
						setImdbId(movie.getString("title"));
					
		        	} 
		        	catch (Exception e) {
						
		        	}
	        	}
				else {
					/*
					 * No Internet connection is present
					 */
				}
	        }
	    }).start();
	    
	}
	/*
	 * Set rating on UI thread
	 */
	public void setRating(final float value){
		runOnUiThread(new Runnable(){
			RatingBar rating = (RatingBar) findViewById(R.id.rating);
			
			@Override
			public void run() {
				rating.setRating(value);
			}
			
		});
	}
	/*
	 * Method to set the poster image
	 */
	public void setBackDrop(final String s){
		runOnUiThread(new Runnable(){

			ImageView view = (ImageView) findViewById(R.id.poster);
			String base = "http://image.tmdb.org/t/p/";
			String size = "w300";
			
			@Override
			public void run() {
				/*
				 * Check for empty backdrop. If empty, do not
				 * overwrite the sample img.
				 */
				if(s != "null"){
					AQuery aq = new AQuery(SingleView.this);
					aq.id(view).image(base+size+s);
				}
			}
			
		});
	}
	
	public void setImdbId(final String s){
	

		Button btn = (Button) findViewById(R.id.imdb);

			SingleView.out(s);
			SingleView.out("Btn click");
			
			btn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// Get movie title and search youtube for trailer
					String title = s;
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://imdb.com/title/" + title)));
				}
		});
		
	}
	
class GetData extends AsyncTask<String, Void, Map>{
	
		protected ProgressBar spinner = (ProgressBar) findViewById(R.id.spinner);
		protected RelativeLayout layout = (RelativeLayout) findViewById(R.id.layuot);
		
		protected TextView title = (TextView) findViewById(R.id.title);
		protected TextView description = (TextView) findViewById(R.id.description);
		protected TextView facts = (TextView) findViewById(R.id.facts);
		
		protected TextView info = (TextView) findViewById(R.id.info);
		
		protected RatingBar rating = (RatingBar) findViewById(R.id.rating);
	
		protected void onPreExecute(){
			/* Hide main layout so that we only show a loader
			 * while the app loads the data from website */
			layout.setVisibility(View.INVISIBLE);
			info.setVisibility(View.INVISIBLE);
			
			// Set movie title from itent
			title.setText(getIntent().getStringExtra("title"));
			
			/*
			 * If internet is not present, call
			 * the onCancel method.
			 */
			if(isOnline() == false){
				cancel(true);
			}
		}
		/*
		 * Called when the task is cancelled.
		 */
		protected void onCancelled(){
			/*
			 * Handle the cancel event, and add
			 * the info view
			 * 
			 * Also remove the loading spinner
			 */
			
			layout.setVisibility(View.INVISIBLE);
			spinner.setVisibility(View.INVISIBLE);
			
			if(isOnline() == false){
				info.setText("Ingen internettforbindelse");
				info.setVisibility(View.VISIBLE);
				return;
			}
			
			info.setText("Noe gikk galt");
			info.setVisibility(View.VISIBLE);
			
			return;
			
		}
		
		@Override
		protected Map doInBackground(String... params) {
			// Get all the movies from aurorakino and return a list of them
			// to the postexecute method.
			
			Map<String, Object> map = new Hashtable<String, Object>();
			
			List<String> moviefacts = new ArrayList<String>();
			
			/*
			 * If cancel has been called for some reason,
			 * call the onCancelled method.
			 */
			if(isCancelled()){
				onCancelled();
			}
			
			
			try 
			{
				Document doc = (Document) Jsoup.connect(params[0]).get();
				
				// Get moviefacts
				Elements facts = doc.getElementsByClass("movieFacts");
			
				String s[] = facts.html().split("<....");
				
				for (String text : s){
					moviefacts.add(Jsoup.parse(text).text());
				}
				
				// Get moviedescription
				Element moviedescription = doc.getElementsByClass("content").select("p").first();
				
				// Put into map for later usage
				map.put("facts", moviefacts);
				map.put("desc", moviedescription.text());
				
				/*
				 * Assign this text if no description is ava
				 */
				if(moviedescription.text().equals("")){
					map.put("desc", "Ingen beskrivelse tilgjengelig");
				}
				
				// Return map to postexecute
				return map;
			}
			catch (Exception e) {
				/*
				 * Cancel the operation if something
				 * goes wrong
				 */
				
				cancel(true);
			}
			
			
			return null;
		}
		
		protected void onPostExecute(Map map){
			/*
			 * Check if cancel has been set, so that we avoid doing stuff
			 * to something that dont exist.
			 */
			
			if(isCancelled()){
				onCancelled();
			}
			
			// Assign text to desc view
			description.setText((String)map.get("desc"));
			description.setSingleLine(false);
			
			// Assign text to movie facts
			List<String> factlist = (List) map.get("facts");
			Iterator<String> factiter = factlist.iterator();
			Boolean first = true;
			int count = 0;
			while(factiter.hasNext()){
				// Append text, and clean formatting
				if(first == true){
					String dontusethis = factiter.next();
					String andthis = factiter.next();
					first = false;
					SingleView.out(dontusethis);
				}
				else {
					String text = factiter.next();
					if(count % 2 == 0){
						facts.append(text.trim() + "\n\n");
					}
					else {
						facts.append(text.trim());
					}
				}
				
				count += 1;
			}
			
			// Hide spinner and set layout visible
			spinner.setVisibility(View.INVISIBLE);
			layout.setVisibility(View.VISIBLE);
			
		}
	}

	public static void out(Object msg){
		Log.i("info", msg.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.single_view, menu);
		return true;
	}
	
	// Button click handler
	public void onClick(View v){
		int id = v.getId();
		String title = new String();
		
		switch(id){
		case R.id.trailer:
			// Get movie title and search youtube for trailer
			title = getIntent().getStringExtra("title").replace(" ", "+");
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + title)));
			break;
		case R.id.ticket:
			// Get url and open browser to ticket buy
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getStringExtra("url"))));
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		Intent intent;
		
		int id = item.getItemId();
		
		switch(id){
		case R.id.chose_theater:
			intent = new Intent(this, StartUp.class);
			intent.putExtra("chose", true);
			startActivity(intent);
		}
		
		return super.onOptionsItemSelected(item);
	}
}
