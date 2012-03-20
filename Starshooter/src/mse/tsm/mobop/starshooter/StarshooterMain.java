package mse.tsm.mobop.starshooter;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mse.tsm.mobop.starshooter.game.Playground;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StarshooterMain extends Activity
{
  ListView list;
  MenuAdapter adapter;
  
  public static int comPort = 5432;
  
  final int DIALOG_SLAVE_PROMPT4MASTER_IP = 1;

  private ArrayList<ListObject> listItems;

  private String[] menuItems;
  
  /** `prompt 4 master ip'-dialog is in ready state, prepared for user input **/
  private final short PROMPT4MASTERIP_STATE_READY = 0;
  
  /** `prompt 4 master ip'-dialog is in waiting state, connection to master is in progress **/
  private final short PROMPT4MASTERIP_STATE_WAITING = 1;
  
  /** current state of `prompt 4 master ip'-dialog **/
  private short prompt4masterIp_state = PROMPT4MASTERIP_STATE_READY;
  
  // Connection settings
  /** defines if we are master **/
  private boolean con_master = true;
  
  /** master's ip address **/
  private Integer [] con_masterip;

  //@Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    listItems = new ArrayList<ListObject>();
    menuItems = new String[2];
    // fill up menu
    // connect
    ListObject lo0 = new ListObject(getResources().getString(R.string.main_menu_connect), "");
    listItems.add(lo0);
    // about
    StringBuilder sb = new StringBuilder();
    sb.append(getResources().getString(R.string.app_name));
    sb.append(" ");
    try
    {
      sb.append( getPackageManager().getPackageInfo(getPackageName(), 0).versionName );
    } 
    catch (NameNotFoundException e)
    {
      sb.append("unknw");
    }
    
    listItems.add(new ListObject(getResources().getString(R.string.main_menu_about), sb.toString()));

    list = (ListView) findViewById(R.id.mainmenu);
    adapter = new MenuAdapter(this, menuItems, listItems);
    list.setAdapter(adapter);
    
    list.setOnItemClickListener(new ListView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id)
      {
        switch (position)
        {
          // try to connect
          case 0:
            dialog_selectMasterSlave();
            break;
            
          // About
          case 1:
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.about_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
            
            // set icon
            ImageView image = (ImageView) layout.findViewById(R.id.image);
            image.setImageResource(R.drawable.ic_launcher);
            
            // set title
            TextView title=(TextView) layout.findViewById(R.id.title);
            StringBuilder sb = new StringBuilder();
            sb.append(getResources().getString(R.string.app_name)); sb.append(" ");
            try{
              sb.append( getPackageManager().getPackageInfo(getPackageName(), 0).versionName );
            } catch (NameNotFoundException e){sb.append("?.?.?");}
            title.setText(sb);
            
            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText( getResources().getString(R.string.about_text) );
            
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            break;
        }
      }
    });
  }

  public void dialog_selectMasterSlave()
  {
    final CharSequence[] items = { 
      getResources().getString(R.string.select_masterSlave_master), 
      getResources().getString(R.string.select_masterSlave_slave)
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getResources().getString(R.string.select_masterSlave_title));
    
    builder.setItems(items, new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int selectedItem)
        {
        	switch (selectedItem)
		    {
		    	// master
		    	case 0:
		    		Bundle bundle = new Bundle();
		    		bundle.putBoolean("isMaster", true);
		    	  
		    	  	// find out own ip
		            String ip=getLocalIpAddress();
		            bundle.putString("serverip", ip);
		    	  
		    		Intent i = new Intent(StarshooterMain.this, Playground.class);
		    		i.putExtras(bundle);
		    		StarshooterMain.this.startActivity(i);
		    		break;
		      
		    	// slave
		    	case 1:
		    		showDialog(DIALOG_SLAVE_PROMPT4MASTER_IP);
		    		break;
		    }
        }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }
    
  protected Dialog onCreateDialog(int id) {
    Dialog dialog;
    switch (id)
    {
      case DIALOG_SLAVE_PROMPT4MASTER_IP:
        dialog = new Dialog(this);
  
        dialog.setContentView(R.layout.request_ip_prompt);
        dialog.setTitle(getResources().getString(R.string.prompt4ip_title));
        
        final AutoCompleteTextView input = (AutoCompleteTextView)dialog.findViewById(R.id.ripp_input);
        final Button button = (Button)dialog.findViewById(R.id.ripp_button);
        final ProgressBar pb = (ProgressBar)dialog.findViewById(R.id.ripp_connectionProgress);
        
        // find out own ip
        String ip=getLocalIpAddress();
        String []ipd=ip.split("\\.");
        if (ipd.length == 4)
        {
          String ip2 = ipd[0]+"."+ipd[1]+"."+ipd[2]+".";
          input.setText(ip2);
          input.setSelection(ip2.length());
        }
        
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
          //@Override
          public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
          {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
              if (keyCode == KeyEvent.KEYCODE_BACK)
              {
                pb.setVisibility(View.INVISIBLE);
                input.setEnabled(true);
                input.requestFocus();
                button.setText(R.string.prompt4ip_continue);
                prompt4masterIp_state = PROMPT4MASTERIP_STATE_READY;
                return false;
              }
            }
            return false;
          }
        });
        
        input.setOnKeyListener(new OnKeyListener()
        {
          public boolean onKey(View v, int keyCode, KeyEvent event)
          {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
              if (keyCode == KeyEvent.KEYCODE_ENTER)
              {
                button.performClick();
                return true;
              }
            }
            return false;
          }
        });
        
        button.setOnClickListener(new OnClickListener()
        {
          //@Override
          public void onClick(View arg0)
          {
            switch (prompt4masterIp_state)
            {
              case PROMPT4MASTERIP_STATE_READY:
                IPchecker ipc = new IPchecker(input.getText().toString());
                if (ipc.isValid() && !input.getText().toString().equals(getLocalIpAddress()))
                {
                  pb.setVisibility(View.VISIBLE);
                  input.setEnabled(false);
                  button.setText(R.string.prompt4ip_abort);
                  button.requestFocus();
                  prompt4masterIp_state = PROMPT4MASTERIP_STATE_WAITING;
                  
                  /// TODO: Start trying to connect, then start finish(); and close dialog, make sure dialog is as inited
                  Bundle bundle = new Bundle();
                  bundle.putBoolean("isMaster", false);
                  
                  // find out own ip
                  String ip=input.getText().toString();
                  bundle.putString("serverip", ip);
                  
                  Intent i = new Intent(StarshooterMain.this, Playground.class);
                  i.putExtras(bundle);
                  StarshooterMain.this.startActivity(i);
                  
                  finish();
                }
                else
                {
                  Toast.makeText(getApplicationContext(), getResources().getString(ipc.isValid()?R.string.prompt4ip_errOwn:R.string.prompt4ip_errInvalid).toString(), Toast.LENGTH_SHORT).show();
                }
                break;
              case PROMPT4MASTERIP_STATE_WAITING :
                pb.setVisibility(View.INVISIBLE);
                input.setEnabled(true);
                input.requestFocus();
                button.setText(R.string.prompt4ip_continue);
                prompt4masterIp_state = PROMPT4MASTERIP_STATE_READY;
                /// TODO: aborting `trying-to-connect-to-master'-thread
                break;
            }
          }
        });
        
        showDialog(dialog.getVolumeControlStream());
        break;
      default:
    	  dialog = null;
    }
    return dialog;
  }
  

  public String getLocalIpAddress()
  {
    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    try
    {
      WifiInfo wifiInfo = wifiManager.getConnectionInfo();
      int ip = wifiInfo.getIpAddress();
      String ipString = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
      return ipString;
    }
    catch(Exception e)
    {
      Toast toast = Toast.makeText( getApplicationContext(), getResources().getString(R.string.wifiman_err_rights), Toast.LENGTH_SHORT);
      toast.show();
    }
    return getResources().getString(R.string.wifi_err_ip);
    
    /*get IP from all devices: 
    try
    {
      for (Enumeration<NetworkInterface> en = NetworkInterface
          .getNetworkInterfaces(); en.hasMoreElements();)
      {
        NetworkInterface intf = en.nextElement();
        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
        {
          InetAddress inetAddress = enumIpAddr.nextElement();
          if (!inetAddress.isLoopbackAddress())
          {
            return inetAddress.getHostAddress().toString();
          }
        }
      }
    } catch (SocketException ex)
    {
      Log.e(Log.ERROR, ex.toString());
    }
    return null;*/
  }
  
  //@Override
  public void onDestroy()
  {
    list.setAdapter(null);
    super.onDestroy();
  }
}

class IPchecker
{
  private final short VALID_NOT_EVALUATED = 0;
  private final short VALID_EVALUATED_INVALID = 1;
  private final short VALID_EVALUATED_VALID = 2;
  
  private Integer digits[]=new Integer[4];
  private short valid = VALID_NOT_EVALUATED;
  private String input;
  
  public IPchecker(String ip)
  {
    input = ip;
    digits[0]=127;
    digits[1]=0;
    digits[2]=0;
    digits[3]=1;
  }
  
  private void validate()
  {
    Pattern p = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
    Matcher m = p.matcher(input);
    
    if( m.find() )
    {
      for(int i=1;i<=4;i++)
      {
        int dig=Integer.parseInt(m.group(i).toString());
        if( dig<0 || dig>255 )
        {
          valid=VALID_EVALUATED_INVALID;
          return;
        }
      }

      for(int i=1;i<=4;i++)
        digits[i-1]=Integer.parseInt(m.group(i).toString());
      valid=VALID_EVALUATED_VALID;
    }
  }
  
  public boolean isValid()
  {
    if( valid == VALID_NOT_EVALUATED )
      validate();
    
    if( valid == VALID_EVALUATED_VALID )
      return true;
    return false;
  }
  
  public Integer [] getDigits()
  {
    return digits;
  }
}