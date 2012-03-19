package mse.tsm.mobop.starshooter;

import mse.tsm.mobop.starshooter.ListObject;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter
{
  private Activity activity;
  private String[] data;
  private ArrayList<ListObject> list;
  private static LayoutInflater inflater=null;
  
  public MenuAdapter(Activity a, String[] d, ArrayList<ListObject> l)
  {
      activity = a;
      data=d;
      list = l;
      inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  public int getCount()
  {
      return data.length;
  }

  public Object getItem(int position)
  {
      return position;
  }

  public long getItemId(int position)
  {
      return position;
  }
  
  public static class ViewHolder
  {
      public TextView text;
      public TextView subtext;
  }

  public View getView(int position, View convertView, ViewGroup parent)
  {
      View vi=convertView;
      ViewHolder holder;
      if(convertView==null)
      {
          vi = inflater.inflate(R.layout.item, null);
          holder=new ViewHolder();
          holder.text=(TextView)vi.findViewById(R.id.text);
          holder.subtext=(TextView)vi.findViewById(R.id.subtext);
          vi.setTag(holder);
      }
      else
          holder=(ViewHolder)vi.getTag();
      
      holder.text.setText(list.get(position).text);
      holder.subtext.setText( list.get(position).subtext);
      return vi;
  }
}
