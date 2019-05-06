package com.example.instaapiintegration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instaapiintegration.CustomViews.AuthenticationDialogue;
import com.example.instaapiintegration.interfaces.AuthenticationListner;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AuthenticationListner {

    private AuthenticationDialogue auth_dialog;
    private SharedPreferences prefs = null;
    private Button Btn_login = null;
    private String token = null;
    private TextView name=null;
    private ImageView image=null;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.textView);
        image = findViewById(R.id.imageView);

        Btn_login  = findViewById(R.id.instagram_login);
        prefs =  getSharedPreferences(Constants.PREF_NAME,MODE_PRIVATE);
        token = prefs.getString("token",null);

         if(token != null){

             Btn_login.setText("Log out");


             getUserInfoByAccessToken(token);

         }else{
             Btn_login.setText("Log in");
             name.setVisibility(View.INVISIBLE);
             image.setVisibility(View.INVISIBLE);

         }
        

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(token != null){

            Btn_login.setText("Log out");

            getUserInfoByAccessToken(token);

        }else{
            Btn_login.setText("Log in");

            name.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
        }
    }

    private void getUserInfoByAccessToken(String token) {

         new RequestInstagramApi().execute();
    }


    private class RequestInstagramApi extends AsyncTask<Void,String,String>{


        @Override
        protected String doInBackground(Void... voids) {


            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Constants.GET_USER_INFO_URL+token);
            Log.d("httpGet",httpGet.toString());
            try{
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String json = EntityUtils.toString(httpEntity);
                return json;

            }catch (ClientProtocolException e){
                     e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response != null){

                try {
                    JSONObject json = new JSONObject(response);
                    Log.e("response",json.toString() );
                     JSONObject jsonObject = json.getJSONObject("data");
                    if(jsonObject.has("id")){

                        String id = jsonObject.getString("id");
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("user_id",id);
                        editor.apply();
                        String username = jsonObject.getString("username");
                        String profile_pic = jsonObject.getString("profile_picture");
                        Picasso.with(MainActivity.this).load(profile_pic).error(R.drawable.ic_launcher_background)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(image);


                        name.setText(username);
                        name.setVisibility(View.VISIBLE);
                        image.setVisibility(View.VISIBLE);

                    }else{

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }






    @Override
    public void onCodeRecieved(String auth_token) {
     if(auth_token==null)
        return;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token",auth_token);
        editor.apply();
        token = auth_token;
        Btn_login.setText("Log out");
        getUserInfoByAccessToken(token);

    }

    public void onItemClick(View view) {


         if(token != null){

             SharedPreferences.Editor editor = prefs.edit();
             editor.clear();
             editor.apply();
             Btn_login.setText("Log in");
             token=null;
             name.setVisibility(View.INVISIBLE);
             image.setVisibility(View.INVISIBLE);

         }else{
             auth_dialog = new AuthenticationDialogue(this,this);
             auth_dialog.setCancelable(true);
             getUserInfoByAccessToken(token);
             auth_dialog.show();
         }

    }
}
