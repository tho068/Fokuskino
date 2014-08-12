package com.fason.kino;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fason.kino.CurrentMovies;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	
	public String today;
	public String tomorrow;
	
	private Fragment FragmentToday;
	private Fragment FragmentTomorrow;
	private Fragment FragmentComing;
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
        
    	/*
    	 * Today and tomorrows date
    	 */
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = calendar.getTime();
		
		Bundle bToday = new Bundle();
		Bundle bTomorrow = new Bundle();
		
		this.today = new SimpleDateFormat("dd-MM-yyyy").format(today);
		this.tomorrow = new SimpleDateFormat("dd-MM-yyyy").format(tomorrow);
		
		bToday.putString("date", this.today);
		bTomorrow.putString("date", this.tomorrow);
		
		FragmentToday = new CurrentMovies();
		FragmentTomorrow = new CurrentMovies();
		FragmentComing = new CommingMovies();
		FragmentToday.setArguments(bToday);
		FragmentTomorrow.setArguments(bTomorrow);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        	case 0:
        		return this.FragmentToday;
            
        	case 1:
        		return this.FragmentTomorrow;
        		
        	case 2:
        		return this.FragmentComing;
            
        }
        
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
 
}