package com.fason.kino;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {
	
	public static Boolean today = true;
	public static String date = new String();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar actionbar = getActionBar();
		actionbar.setTitle("Fokus kino");
		
		GetData task = new GetData();
		task.execute();
	}

	class GetData extends AsyncTask<Void, Void, List>{
		
		public ListView listview = (ListView) findViewById(R.id.overview);
		protected ProgressBar spinner = (ProgressBar) findViewById(R.id.spinner);
		
		protected void onPreExecute(){
			/* Set listview hidden while loading from server */
			listview.setVisibility(View.INVISIBLE);
		}
		
		@Override
		protected List doInBackground(Void... params) {
			// Get all the movies from aurorakino and return a list of them
			// to the postexecute method.
			
			// Get current date
			Calendar calendar = Calendar.getInstance();
			Date today = calendar.getTime();
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			Date tomorrow = calendar.getTime();
			
			String date = new SimpleDateFormat("dd-MM-yyyy").format(today);
			
			Hashtable<String, Elements> map = new Hashtable<String, Elements>();
			
			try {
				Document doc = (Document) Jsoup.connect("http://fokus.aurorakino.no/billetter-og-program/?D=" + date).get();
				
				Elements test = doc.getElementsByClass("showing");
				
				/* No movies today - get movies for tomorrow */
				if(test.size() == 0){
					MainActivity.today = false;
					date = new SimpleDateFormat("dd-MM-yyyy").format(tomorrow);
					doc = (Document) Jsoup.connect("http://fokus.aurorakino.no/billetter-og-program/?D=" + date).get();
					test = doc.getElementsByClass("showing");
				}
				
				// Store movieprogram date
				MainActivity.date = date;
				
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
					
					movie.put("time", time);
					
					listofmovies.add(movie);
				}
				
				return listofmovies;
			} 
			catch (IOException e) {
			// TODO Auto-generated catch block
				MainActivity.out("Noe gikk galt");
			}
			
			return null;
		}
		
		protected void onPostExecute(final List movielist){
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			
			List<String> title = new ArrayList<String>();
			List<String> subtitle = new ArrayList<String>();
			List<String> image = new ArrayList<String>();
			List<String> timelist = new ArrayList<String>();
			
			// Set subtitle
			if(MainActivity.today == false){
				getActionBar().setSubtitle("Kinoprogram " + MainActivity.date);
			}
			else {
				getActionBar().setSubtitle("Kinoprogram " + MainActivity.date);
			}
			listview.setClickable(true);
			listview.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Map moviemap = (Map) movielist.get(position);
					String url = (String) moviemap.get("url");
					String title = (String) moviemap.get("title");
					
					Intent intent = new Intent(MainActivity.this, SingleView.class);
					intent.putExtra("url", url);
					intent.putExtra("title", title);
					startActivity(intent);
				}
			});
			
			// Iterate over list of movies
			Iterator movieiter = movielist.iterator();
			while(movieiter.hasNext()){
				Map map = (Hashtable) movieiter.next();
				
				MainActivity.out(map.get("url"));
				
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

			MovieAdapter adapter = new MovieAdapter(getBaseContext(), title, image, subtitle, timelist);
			
			listview.setAdapter(adapter);
			
			spinner.setVisibility(View.INVISIBLE);
			listview.setVisibility(View.VISIBLE);
			
		}
	}
	
	public static void out(Object msg){
		Log.i("info", msg.toString());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
