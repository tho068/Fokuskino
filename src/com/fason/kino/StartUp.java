package com.fason.kino;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class StartUp extends Activity {
	
	/*
	 * Initialize actionbar and context for 
	 * later usage
	 */
	private ActionBar mActionBar;
	private Context mContext;
	
	/*
	 * Server string
	 */
	private String server;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_up);
		
		/*
		 * Get actionbar, context and 
		 * set up actionbar
		 */
		mActionBar = getActionBar();
		mContext = this;
		
		mActionBar.setTitle("Velg kino");
		
		/*
		 * Check if user already has picked a theater, if so, gracefully skip
		 * this activity.
		 */
		
		SharedPreferences mSharedPref = getSharedPreferences("STORAGE", Context.MODE_PRIVATE);
		String testIfSkip = mSharedPref.getString("preferred_theater", "null");
	
		if(!testIfSkip.equals("null") && getIntent().getBooleanExtra("chose", false) == false){
			/*
			 * Skip the selection by calling Intent to MainActivity
			 */
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		else {
			/*
			 * Nothing is saved, allow the continius exection of this
			 * activity
			 */
			SingleView.out("Ingenting er lagret....");
		}
		
	}
	
	/*
	 * Handle click events.
	 */
	public void onClick(View v){
		/*
		 * Switch button Ids
		 */
		int id = v.getId();
		
		switch(id){
		case R.id.narvik:
			handleTheater(id);
			break;
		case R.id.tromso:
			handleTheater(id);
			break;
		case R.id.kirkenes:
			handleTheater(id);
			break;
		case R.id.alta:
			handleTheater(id);
			break;
		}
	}
	
	/*
	 * Method to save user defined theater
	 */
	private void handleTheater(int id){
		
		/*
		 * String value that is used to assign server
		 */
		
		SharedPreferences mSharedPref = getSharedPreferences("STORAGE", Context.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSharedPref.edit();
		
		/*
		 * Swith trough the Ids and assign a http
		 * address to be used for grabbing movies
		 */
		
		switch(id){
		case R.id.narvik:
			server = "http://narvik.aurorakino.no";
			break;
		case R.id.kirkenes:
			server = "http://kirkenes.aurorakino.no";
			break;
		case R.id.alta:
			server = "http://alta.aurorakino.no";
			break;
		case R.id.tromso:
			server = "http://fokus.aurorakino.no";
			break;
		}
		
		/*
		 * Commit the string to storage.
		 */
		mEditor.putString("preferred_theater", server);
		mEditor.commit();
		
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_up, menu);
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
