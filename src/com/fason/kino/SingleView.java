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

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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
		
		// Run GetData task
		GetData task = new GetData();
		task.execute(getIntent().getStringExtra("url"));
	}
	
class GetData extends AsyncTask<String, Void, Map>{
	
		protected ProgressBar spinner = (ProgressBar) findViewById(R.id.spinner);
		protected RelativeLayout layout = (RelativeLayout) findViewById(R.id.layuot);
		
		protected TextView title = (TextView) findViewById(R.id.title);
	
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
			
			MainActivity.out("Bakgrunn");
			
			Hashtable<String, Elements> map = new Hashtable<String, Elements>();
			
			try 
			{
				Document doc = (Document) Jsoup.connect(params[0]).get();
				
				Elements test = doc.getElementsByClass("content");	
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				MainActivity.out("Noe gikk galt");
			}
			
			return null;
		}
		
		protected void onPostExecute(Map map){
			// Hide spinner and set layout visible
			spinner.setVisibility(View.INVISIBLE);
			layout.setVisibility(View.VISIBLE);
		}
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
