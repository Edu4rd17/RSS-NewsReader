package com.example.assignmentrss;

/**
 * @author Eduard Iacob
 */

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

//This class is used to open the web view when the user clicks on a item from the list
public class WebViewActivity extends AppCompatActivity {

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        // Get the url from the intent
        url = getIntent().getStringExtra("URL");
        // Initialize web view
        WebView myWebView = (WebView) findViewById(R.id.webView);
        // Load the url in the web view
        myWebView.loadUrl(url);
    }
}