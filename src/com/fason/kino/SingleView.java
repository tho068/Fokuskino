package com.fason.kino;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cyrilmottier.android.translucentactionbar.NotifyingScrollView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
	     mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.actionbar);
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

	
	// Method to get rating and fill ratingbar
	protected void getRating(){
	    new Thread(new Runnable() {
	        public void run() {
	        	String title = getIntent().getStringExtra("title").replace(" ", "+");
	        	SingleView.out("new thread");
	        	HttpClient client = new DefaultHttpClient();
	        	HttpGet get = new HttpGet("http://www.omdbapi.com/?t=" + title);
	        	try {
					HttpResponse response = client.execute(get);
					BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
					String json = reader.readLine();
					JSONTokener tokener = new JSONTokener(json);
					JSONObject finalResult = new JSONObject(tokener);
					
					SingleView.out(finalResult);
					setRating(Float.parseFloat((String)finalResult.get("imdbRating")));
				
	        	} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }).start();
	    
	}
	
	public void setRating(final float value){
		runOnUiThread(new Runnable(){

			RatingBar rating = (RatingBar) findViewById(R.id.rating);
			
			@Override
			public void run() {
				rating.setRating(value);
			}
			
		});
	}
	
class GetData extends AsyncTask<String, Void, Map>{
	
		protected ProgressBar spinner = (ProgressBar) findViewById(R.id.spinner);
		protected RelativeLayout layout = (RelativeLayout) findViewById(R.id.layuot);
		
		protected TextView title = (TextView) findViewById(R.id.title);
		protected TextView description = (TextView) findViewById(R.id.description);
		protected TextView facts = (TextView) findViewById(R.id.facts);
		
		protected RatingBar rating = (RatingBar) findViewById(R.id.rating);
	
		protected void onPreExecute(){
			/* Hide main layout so that we only show a loader
			 * while the app loads the data from website */
			layout.setVisibility(View.INVISIBLE);
			
			// Set movie title from itent
			title.setText(getIntent().getStringExtra("title"));
		}
		
		@Override
		protected Map doInBackground(String... params) {
			// Get all the movies from aurorakino and return a list of them
			// to the postexecute method.
			
			Map<String, Object> map = new Hashtable<String, Object>();
			
			List<String> moviefacts = new ArrayList<String>();
			
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
				
				// Return map to postexecute
				return map;
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
	
			}
			
			return null;
		}
		
		protected void onPostExecute(Map map){
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
		switch(id){
		case R.id.trailer:
			// Get movie title and search youtube for trailer
			String title = getIntent().getStringExtra("title").replace(" ", "+");
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + title)));
			break;
		case R.id.ticket:
			// Get url and open browser to ticket buy
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getStringExtra("url"))));
			break;
		case R.id.imdb:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		case R.id.trailer:
			// Get movie title and search youtube for trailer
			String title = getIntent().getStringExtra("title").replace(" ", "+");
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + title)));
			break;
		case R.id.ticket:
			break;
		case R.id.imdb:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
