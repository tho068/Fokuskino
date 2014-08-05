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
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SingleView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_view);
		
		// Set actionbar title
		getActionBar().setTitle(getIntent().getStringExtra("title"));
		
		// Up navigation
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Run GetData task
		GetData task = new GetData();
		task.execute(getIntent().getStringExtra("url"));
	}
	
class GetData extends AsyncTask<String, Void, Map>{
	
		protected ProgressBar spinner = (ProgressBar) findViewById(R.id.spinner);
		protected RelativeLayout layout = (RelativeLayout) findViewById(R.id.layuot);
		
		protected TextView title = (TextView) findViewById(R.id.title);
		protected TextView description = (TextView) findViewById(R.id.description);
		protected TextView facts = (TextView) findViewById(R.id.facts);
	
		protected void onPreExecute(){
			/* Hide main layout so that we only show a loader
			 * while the app loads the data from website */
			layout.setVisibility(View.INVISIBLE);
			
			// Set movie title from itent
			title.setText(getIntent().getStringExtra("title"));
		}
		
		@SuppressLint("NewApi")
		@Override
		protected Map doInBackground(String... params) {
			// Get all the movies from aurorakino and return a list of them
			// to the postexecute method.
			SingleView.out("Finner singleview items");
			
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
