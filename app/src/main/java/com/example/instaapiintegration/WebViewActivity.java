package com.example.instaapiintegration;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.instaapiintegration.interfaces.AuthenticationListner;

public class WebViewActivity extends AppCompatActivity {
private WebView WebView;
    private AuthenticationListner listner;
    private Context context;

    private final String url ="https://api.instagram.com/oauth/authorize/?client_id="+Constants.CLIENT_ID+"&redirect_uri="+Constants.REDIRECT_URI+"&response_type=token";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView = findViewById(R.id.tabbed1);

        WebView.loadUrl(url);
        WebView.setWebViewClient(new WebViewClient(){


            String acces_token;
            boolean authComplete;
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(url.contains("#access_token=")&& !authComplete){
                    Uri uri = Uri.parse(url);
                    acces_token = uri.getEncodedFragment();
                    acces_token = acces_token.substring(acces_token.lastIndexOf("=")+1);
                    Toast.makeText(context, "access_token"+acces_token, Toast.LENGTH_SHORT).show();
                    authComplete = true;
                    listner.onCodeRecieved(acces_token);
                    Toast.makeText(context, "access token:" +acces_token, Toast.LENGTH_SHORT).show();



                }else if(url.contains("?error")){
                    Toast.makeText(context, "getting erroe", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
