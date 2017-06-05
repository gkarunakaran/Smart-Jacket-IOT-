package com.iot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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
 * Created by G.Karun on 24/12/16.
 */
public class Liveusers extends AppCompatActivity {
    ProgressDialog pd;
    String data;
    String[] usernames;
    String username;
    GridView users;
    String url;
    ArrayList<String> userlist=new ArrayList<String>();
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liveusers);
        users=(GridView)findViewById(R.id.liveusers);
        Bundle bundle=getIntent().getExtras();
        url=bundle.getString("url");
        new Getusers().execute(url+"iot_json.php");
    }
    class Getusers extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(Liveusers.this);
            pd.setTitle("Loading users");
            pd.setMessage("Please wait");
            pd.setIndeterminate(true);
            pd.setCancelable(true);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(Looper.myLooper()==null) {
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
                        e.printStackTrace();
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if(userlist.size()!=0){
                usernames=userlist.toArray(new String[userlist.size()]);
                users.setAdapter(new Uersadapter(getApplicationContext(),usernames));
                users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(getApplicationContext(),Userdetails.class);
                        intent.putExtra("user",usernames[position]);
                        intent.putExtra("url",url);
                        startActivity(intent);
                    }
                });
                pd.cancel();
            }
            else{
                pd.cancel();
                Toast.makeText(getApplicationContext(),"Cannot connect to server or Users not fount",Toast.LENGTH_LONG).show();
            }
        }
    }
}
