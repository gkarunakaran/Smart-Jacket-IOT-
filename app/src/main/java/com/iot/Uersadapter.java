package com.iot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by G.Karun on 24/12/16.
 */
public class Uersadapter extends BaseAdapter {
    Context contex;
    String[] listusers;
    TextView textview;
    ImageView imageview;
    public Uersadapter(Context context,String[] users)
    {
        this.listusers=users;
        contex=context;
    }

    @Override
    public int getCount()

    {
        return listusers.length;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View grid;
        LayoutInflater inflater=(LayoutInflater)contex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            grid=new View(contex);
            grid=inflater.inflate(R.layout.usersgrid,null);
            textview=(TextView)grid.findViewById(R.id.gridtext);
            imageview=(ImageView)grid.findViewById(R.id.gridimage);
            textview.setText(listusers[position]);
            imageview.setImageResource(R.drawable.user);
            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageview.setPadding(15,15,15,15);
        }
        else{
            grid=(View)convertView;
        }
        return grid;
    }
}
