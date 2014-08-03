package com.fason.kino;

import java.io.IOException;
import java.util.ArrayList;
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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getActionBar().setTitle("Fokus kino");
		getActionBar().setSubtitle("Filmer i dag");
		
		GetData task = new GetData();
		task.execute();
		this.out("Done task");
	}

	class GetData extends AsyncTask<Void, Void, List>{
		
		@Override
		protected List doInBackground(Void... params) {
			// Get all the movies from aurorakino and return a list of them
			// to the postexecute method.
			
			MainActivity.out("Bakgrunn");
			
			Hashtable<String, Elements> map = new Hashtable<String, Elements>();
			
			try {
				Document doc = (Document) Jsoup.connect("http://fokus.aurorakino.no/billetter-og-program/").get();
				
				Elements test = doc.getElementsByClass("showing");
				
				List<Object> listofmovies = new ArrayList<Object>();
		
				Iterator<Element> iter = test.iterator();
				while(iter.hasNext()){
					Element tag = (Element) iter.next();
					
					Map<String, Object> movie = new Hashtable<String, Object>();
					List<String> time = new ArrayList<String>();
					
					movie.put("title", tag.select(".movieTitle").text());
					movie.put("desc", tag.select(".movieDescription").text());
					movie.put("image", tag.select(".smallPoster"));
					
					Elements times = tag.select(".programTime");
					Iterator<Element> itertimes = times.iterator();
					
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
		
		protected void onPostExecute(List movielist){
			MainActivity.out("Kjøres");
			ListView listview = (ListView) findViewById(R.id.overview);
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			
			// Iterate over list of movies
			Iterator movieiter = movielist.iterator();
			while(movieiter.hasNext()){
				Map map = (Hashtable) movieiter.next();
				Map<String, String> adapterMap = new Hashtable<String, String>();
				
				String title = (String) map.get("title");
				String description = (String) map.get("desc");
				String time = "";
				
				List timelist = (List) map.get("time");
				Iterator timeiter = timelist.iterator();
				while(timeiter.hasNext()){
					Button btn = new Button(MainActivity.this);
					btn.setText((String)timeiter.next());
				}
				
				adapterMap.put("title", title);
				adapterMap.put("subtitle", description);
				adapterMap.put("time", time);
				
				list.add(adapterMap);
			}
			
			SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), list, R.layout.viewliste, new String[] {"title", "subtitle", "time"}, new int[] {R.id.list_title, R.id.list_subtitle, R.id.time});
			
			listview.setAdapter(adapter);
			
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
