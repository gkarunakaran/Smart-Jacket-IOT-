package com.iot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

public class Login extends AppCompatActivity {
    String url,data,uname,pass,portnumber,filefolder,ip;
    ProgressDialog pd;
    Context context;
    EditText ipaddress,port,folder,username,password;
    Button loginbutton;
    String protocol="http://";
    String col=":";
    String slash="/";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        port=(EditText)findViewById(R.id.porttext);
        folder=(EditText)findViewById(R.id.folder);
        ipaddress=(EditText)findViewById(R.id.iptext);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        loginbutton=(Button)findViewById(R.id.login);
        ipaddress.setText("192.168.43.8");
        port.setText("80");
        folder.setText("test");
        context=this;
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip=ipaddress.getText().toString().trim();
                portnumber=port.getText().toString().trim();
                filefolder=folder.getText().toString().trim();
                uname=username.getText().toString().trim();
                pass=password.getText().toString().trim();
                url=protocol+ip+col+portnumber+slash+filefolder+slash;
                String loginurl=url+"login.php";
                if(url.length()!=0&&uname.length()!=0&&pass.length()!=0&&portnumber.length()!=0&&filefolder.length()!=0) {
                    new Loginpage().execute(loginurl);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please enter all the details",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    class Loginpage extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(context);
            pd.setTitle("Logging in!");
            pd.setMessage("Please Wait");
            pd.setCancelable(true);
            pd.setIndeterminate(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String params[]) {
            try {
                HttpClient client=new DefaultHttpClient();
                HttpPost post=new HttpPost(params[0]);
                ArrayList list=new ArrayList();
                list.add(new BasicNameValuePair("username",uname));
                list.add(new BasicNameValuePair("password",pass));
                post.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse response=client.execute(post);
                int status=response.getStatusLine().getStatusCode();
                if(status==200){
                    HttpEntity entity=response.getEntity();
                    data= EntityUtils.toString(entity);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if(data==null){
                Toast.makeText(getApplicationContext(),"Cannot connect to Server", Toast.LENGTH_LONG).show();
            }
            else if(data.equals("success")){
                Intent intent=new Intent(getApplicationContext(),Index.class);
                intent.putExtra("url",url);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(),"Please check usename & password", Toast.LENGTH_LONG).show();
            }
        }
    }

}
