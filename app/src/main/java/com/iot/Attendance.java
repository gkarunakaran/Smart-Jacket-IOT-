package com.iot;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by G.Karun on 25/12/16.
 */
public class Attendance extends AppCompatActivity {
    ProgressDialog pd;
    String usernames[];
    String data;
    String username;
    TextView sno,users;
    ArrayList<String> userlist=new ArrayList<String>();
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance);
        sno=(TextView)findViewById(R.id.sno);
        users=(TextView)findViewById(R.id.username);
        Bundle bundle=getIntent().getExtras();
        String url=bundle.getString("url");
        new Attendanceusers().execute(url+"iot_json.php");
        }
    class Attendanceusers extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(Attendance.this);
            pd.setTitle("Loading users");
            pd.setMessage("Please wait");
            pd.setIndeterminate(true);
            pd.setCancelable(true);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(Looper.myLooper()==null){
                Looper.prepare();
            }
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
                        JSONArray jarray=jobject.getJSONArray("Sensordata");
                        for(int i=0;i<jarray.length();i++){
                            JSONObject jobj=jarray.getJSONObject(i);
                            username =jobj.getString("Username");
                            userlist.add(username);
                        }

                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
                    }


                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if(userlist.size()>0){
                usernames=userlist.toArray(new String[userlist.size()]);
                for(int i=1;i<=usernames.length;i++){
                    sno.append(""+i+"."+"\n\n");
                }
                for(int i=0;i<usernames.length;i++){
                    users.append(""+usernames[i]+"\n\n");
                }
                pd.cancel();
            }
            else{
                pd.cancel();
                Toast.makeText(getApplicationContext(),"Cannot connect to server or Users not fount",Toast.LENGTH_LONG).show();
            }
        }
    }
    }
