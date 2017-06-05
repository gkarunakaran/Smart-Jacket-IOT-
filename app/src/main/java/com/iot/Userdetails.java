package com.iot;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by G.Karun on 24/12/16.
 */
public class Userdetails extends AppCompatActivity {
    TextView user, temprature, humidity, ch4, co, obstacle, xaxis, yaxis, zaxis;
    ImageView tempratureimage, humidityimage, ch4image,coimage, obstacleimage, motionimage, emergency;
    int notificationvalue=0;
    String username;
    String url;
    String data;
    String usernamedetail, tempraturedetail, humiditydetail, ch4detail, codetail, obstacledetail, motionaxis_x,
            motionaxis_y, motionaxis_z, emergencydetail;
    Timer timer=new Timer();
    Runnable run;
    Handler handler;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userdetails);
        coimage=(ImageView)findViewById(R.id.coimage);
        tempratureimage = (ImageView) findViewById(R.id.tempimage);
        humidityimage = (ImageView) findViewById(R.id.humimage);
        ch4image = (ImageView) findViewById(R.id.ch4image);
        obstacleimage = (ImageView) findViewById(R.id.obstacleimage);
        motionimage = (ImageView) findViewById(R.id.motionimage);
        user = (TextView) findViewById(R.id.username);
        temprature = (TextView) findViewById(R.id.temprature);
        humidity = (TextView) findViewById(R.id.humidity);
        ch4 = (TextView) findViewById(R.id.ch4);
        co = (TextView) findViewById(R.id.co);
        obstacle = (TextView) findViewById(R.id.obstacle);
        xaxis = (TextView) findViewById(R.id.xaxis);
        yaxis = (TextView) findViewById(R.id.yaxis);
        zaxis = (TextView) findViewById(R.id.zaxis);
        emergency = (ImageView) findViewById(R.id.emergency);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("user");
        url = bundle.getString("url");
        handler=new Handler();
        run=new Runnable() {
            @Override
            public void run() {
                new Getuserdetais().execute(url+"userdetails.php");
                handler.postDelayed(this,10000);
            }
        };
        handler.postDelayed(run,0);
    }

    class Getuserdetais extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Refreshing...",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(params[0]);
            ArrayList list = new ArrayList();
            list.add(new BasicNameValuePair("username", username));
            try {
                post.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse response = client.execute(post);
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    data = EntityUtils.toString(entity);
                }
                try {
                    JSONObject jobject = new JSONObject(data);
                    JSONArray jarray = jobject.getJSONArray("Userdetails");
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject jobj = jarray.getJSONObject(i);
                        usernamedetail = jobj.getString("Username");
                        tempraturedetail = jobj.getString("TEMPRATURE");
                        humiditydetail = jobj.getString("HUMIDITY");
                        ch4detail = jobj.getString("CH4ppm");
                        codetail = jobj.getString("COppm");
                        obstacledetail = jobj.getString("OBSTACLEcm");
                        motionaxis_x = jobj.getString("MotionAxisX");
                        motionaxis_y = jobj.getString("MotionAxisY");
                        motionaxis_z = jobj.getString("MotionAxisZ");
                        emergencydetail = jobj.getString("Emergency");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            user.setText(usernamedetail);
            temprature.setText(tempraturedetail+"°c");
            humidity.setText(humiditydetail+"%");
            ch4.setText(ch4detail+"ppm");
            co.setText(codetail+"ppm");
            obstacle.setText(obstacledetail+"cm");
            xaxis.setText(motionaxis_x);
            yaxis.setText(motionaxis_y);
            zaxis.setText(motionaxis_z);
            try {
                int emergencysignal = Integer.parseInt(emergencydetail);
                if (emergencysignal == 1) {
                    emergency.setImageResource(R.drawable.cross);
                } else if(emergencysignal==0) {
                    emergency.setImageResource(R.drawable.tick);
                }
            } catch (Exception e) {
            }
            checkvalues();
        }
    }
    public void checkvalues() {
        try {
            int tempraturenumber = Integer.parseInt(tempraturedetail);
            int humiditynumber = Integer.parseInt(humiditydetail);
            float ch4number = Float.parseFloat(ch4detail);
            float conumber = Float.parseFloat(codetail);
            int obstaclenumber = Integer.parseInt(obstacledetail);
            int emergencynumber=Integer.parseInt(emergencydetail);
            if (tempraturenumber > 30) {
                tempratureimage.setImageResource(R.drawable.cross);
                notifymessage("Temprature is high!");
            }
            else {
                tempratureimage.setImageResource(R.drawable.tick);
            }
            if (humiditynumber > 48) {
                humidityimage.setImageResource(R.drawable.cross);
                notifymessage("Humidity is high!");
            } else {
                humidityimage.setImageResource(R.drawable.tick);
            }
            if (ch4number > 16) {
                ch4image.setImageResource(R.drawable.cross);
                notifymessage("High amount of CH4!");
            } else {
                ch4image.setImageResource(R.drawable.tick);
            }
            if (conumber > 2) {
                coimage.setImageResource(R.drawable.cross);
                notifymessage("high amount of Co!");
            } else {
                coimage.setImageResource(R.drawable.tick);
            }
            if (obstaclenumber < 10) {
                obstacleimage.setImageResource(R.drawable.cross);
                notifymessage("Obstacle is Near by!");
            } else {
                obstacleimage.setImageResource(R.drawable.tick);
            }
            if(emergencynumber==1){
                notifymessage("Emergency service need for "+usernamedetail);
            }
        }
        catch (Exception e){
        }
    }
    public void notifymessage(String message){
        notificationvalue++;
        long[] vib={1000,1000,1000,1000};
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.iot);
        builder.setContentTitle("Warning!For "+usernamedetail);
        builder.setVibrate(vib);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        builder.setContentText(message);
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        synchronized (notificationManager) {
            notificationManager.notify();
            notificationManager.notify(notificationvalue,builder.build());
            if(notificationvalue>10){
                notificationvalue=0;
            }
        }
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(run);
        super.onBackPressed();
    }
}