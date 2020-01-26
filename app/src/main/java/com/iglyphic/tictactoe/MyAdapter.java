package com.iglyphic.tictactoe;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyAdapter extends ArrayAdapter<String>{

	ArrayList <String> word;
	ArrayList <String> parts;
	private Activity context;
	public MyAdapter(Activity context, ArrayList<String> word,ArrayList <String> parts) {
		super(context, R.layout.item, word);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.word=word;	
		this.parts=parts;
	}
	
	
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		
		View view= convertView;
		
		
			 
			 LayoutInflater inflater= context.getLayoutInflater();
			 
			 
			 view= inflater.inflate(R.layout.item, parent,false);
			 
			 
		 TextView wordView = (TextView)view.findViewById(R.id.txtItemName);		
		 TextView partView = (TextView)view.findViewById(R.id.txtItemAddress);
		
		 String name=word.get(position);
		 wordView.setText(name);
		 partView.setText(parts.get(position));
		 
		return view;
	}

}
