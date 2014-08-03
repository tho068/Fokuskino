package com.fason.kino;

import com.androidquery.AQuery;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieAdapter extends BaseAdapter {
	String[] label;
	String[] image;
	String[] subtitle;
	private Context context;
	
	public MovieAdapter(Context context,String[] label,String[] image, String[] subtitle)
	{
	    this.context=context;
	    this.image = image;
	    this.label = label;
	    this.subtitle = subtitle;
	
	}
	
	private class ViewHolder{
	    ImageView img;
	    TextView label;
		TextView subtitle;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    ViewHolder holder = null;
	
	    LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	    if (convertView == null) {
	        convertView = mInflater.inflate(R.layout.viewliste, null);
	        holder = new ViewHolder();
	        holder.label=(TextView) convertView.findViewById(R.id.list_title);
	        holder.subtitle=(TextView) convertView.findViewById(R.id.list_subtitle);
	        holder.img = (ImageView) convertView.findViewById(R.id.image);
	        convertView.setTag(holder);
	    }
	    else {
	        holder = (ViewHolder) convertView.getTag();
	    }
	
	     AQuery aq = new AQuery(convertView);
	
	     aq.id(holder.label).text(label[position]);
	     aq.id(holder.subtitle).text(label[position]);
	     aq.id(holder.img).image(image[position], true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, 1.0f);
	
	    return convertView;
	}
	
	
	@Override
	    public int getCount() {
	        // TODO Auto-generated method stub
	        return image.length;
	    }
	
	
	    @Override
	    public long getItemId(int position) {
	        // TODO Auto-generated method stub
	        return position;
	    }
	
	
	    @Override
	    public Object getItem(int position) {
	        // TODO Auto-generated method stub
	        return null;
	    }
}