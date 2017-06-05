package com.iot;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * Created by G.Karun on 17/12/16.
 */
public class Index extends AppCompatActivity {
    int notificationvalue;
    ImageView liveusers,attendance;
    String level,rflevel;
    String[] levels;
    ArrayList<String> levellist=new ArrayList<String>();
    Button earhquake,RFID;
    String data,rfdata;
    ProgressDialog pd;
    String url;
    Timer timer;
    Runnable run;
    android.os.Handler handler;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        liveusers=(ImageView) findViewById(R.id.liveusers);
        attendance=(ImageView) findViewById(R.id.attendance);
        earhquake=(Button)findViewById(R.id.earhquack);
        RFID=(Button)findViewById(R.id.rfid);
        try {
            Bundle bundle = getIntent().getExtras();
            url = bundle.getString("url");
            timer = new Timer();
            handler = new android.os.Handler();
        }
        catch (Exception e){
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        run=new Runnable() {
            @Override
            public void run() {
                new Earthquake().execute(url+"earthquake.php");
                new Rfid().execute(url+"RFID.php");
                handler.postDelayed(this,20000);
            }
        };
        handler.postDelayed(run,0);
        liveusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Index.this, com.iot.Liveusers.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent=new Intent(Index.this,Attendance.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
        });
        RFID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    class Earthquake extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client=new DefaultHttpClient();
            HttpPost post=new HttpPost(params[0]);
            try{
                HttpResponse response=client.execute(post);
                int status=response.getStatusLine().getStatusCode();
                if(status==200){
                    HttpEntity entity=response.getEntity();
                    data= EntityUtils.toString(entity);

                    try{
                        JSONObject jobject=new JSONObject(data);
                        JSONArray jarray=jobject.getJSONArray("level");
                        for(int i=0;i<jarray.length();i++){
                            JSONObject jobj=jarray.getJSONObject(i);
                            level =jobj.getString("value");
                            levellist.add(level);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (levellist.size() > 0) {      // Earth quake

                levels=levellist.toArray(new String[levellist.size()]);
                int arraysize=levels.length;
                int lastvalue=Integer.parseInt(levels[arraysize-1]);
                if(lastvalue>250) {  // Threshold value for Earthquake
                    earhquake.setBackgroundColor(Color.RED);
                    notificationvalue++;
                    long[] vib = {1000, 1000, 1000, 1000};
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Index.this);
                    builder.setSmallIcon(R.drawable.iot);
                    builder.setContentTitle("Warning!");
                    builder.setVibrate(vib);
                    builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                    builder.setContentText("Earth Quake will be happen");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    synchronized (notificationManager) {
                        notificationManager.notify();
                        notificationManager.notify(notificationvalue, builder.build());
                    }
                }
                else if(lastvalue==0){
                    earhquake.setBackgroundColor(Color.GREEN);
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Cannot connect to server or make sure Earth quake sensor is Working",Toast.LENGTH_LONG).show();
            }

        }
        }
    class Rfid extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(params[0]);
            try {
                HttpResponse response = client.execute(post);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    rfdata = EntityUtils.toString(entity);

                    try {
                        JSONObject jobject = new JSONObject(rfdata);
                        JSONArray jarray = jobject.getJSONArray("rfid");
                        for (int i = 0; i < jarray.length(); i++) {
                            JSONObject jobj = jarray.getJSONObject(i);
                            rflevel = jobj.getString("RFID");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            int rflevelint=Integer.parseInt(rflevel);
            if(rflevelint==1) {
                if(notificationvalue>=10){
                    notificationvalue=0;
                }
                RFID.setBackgroundColor(Color.RED);
                notificationvalue++;
                long[] vib = {1000, 1000, 1000, 1000};
                NotificationCompat.Builder builder = new NotificationCompat.Builder(Index.this);
                builder.setSmallIcon(R.drawable.iot);
                builder.setContentTitle("Warning!");
                builder.setVibrate(vib);
                builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                builder.setContentText("Unauthorized place");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                synchronized (notificationManager) {
                    notificationManager.notify();
                    notificationManager.notify(notificationvalue, builder.build());
                }
            }
            else{
                RFID.setBackgroundColor(Color.GREEN);
            }
        }
    }
    @Override
    public void onBackPressed() {
        handler.removeCallbacks(run);
        super.onBackPressed();
    }
}

