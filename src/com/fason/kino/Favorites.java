package com.fason.kino;

import java.util.List;
import java.util.Map;

import com.fason.kino.Movie.Movie;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Favorites extends Activity {
	
	/*
	 * Movielist, get from bunble, or sharedprefrences, not decided
	 * yet
	 */
	public List<Map<String, String>> listMovies;
	
	/*
	 * Layout Specific elements
	 */
	
	public ListView mListView = (ListView) findViewById(R.id.overview);
	public TextView mInfo = (TextView) findViewById(R.id.info);
	public ProgressBar mSpinner = (ProgressBar) findViewById(R.id.spinner);
	
	/*
	 * Context, and actionbar
	 */
	private ActionBar mActionbar = getActionBar();
	private Context mContext = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);
		
		/*
		 * Get serialized GSON list of favorite movies.
		 */
		

		
		

		
	}
	
	/*
	 * Append movies to listview.
	 */
	public void appendMovies(ListView lv, List<Map<String, String>> mList){
		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.favorites, menu);
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
