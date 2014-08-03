package com.fason.kino;

import java.util.List;

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
	List label;
	List image;
	List subtitle;
	private Context context;
	
	public MovieAdapter(Context context, List<String> label, List<String> image, List<String> subtitle)
	{
	    this.context = context;
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
	
	     aq.id(holder.label).text((String)label.get(position));
	     aq.id(holder.subtitle).text((String)subtitle.get(position));
	     aq.id(holder.img).image((String)image.get(position));
	
	    return convertView;
	}
	
	
	@Override
	    public int getCount() {
	        // TODO Auto-generated method stub
	        return image.size();
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