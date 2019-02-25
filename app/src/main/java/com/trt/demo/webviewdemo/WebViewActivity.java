package com.trt.demo.webviewdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.view.KeyEvent.KEYCODE_BACK;

public class WebViewActivity extends AppCompatActivity {

    Context myContext;
    WebView myWebview;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_web_view);
        initView();
        initData();
        initWebView();
    }
    private void initView(){
        myWebview = findViewById(R.id.myWebview);

    }

    private void initData(){
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
    }

    private void initWebView(){
        WebSettings webSettings = myWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebview.setWebViewClient(new WebViewClient(){
            @Override
            //方法二：通过 WebViewClient 的 shouldOverrideUrlLoading() 方法回调拦截 url
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("url", url);
                Uri uri=Uri.parse(url);
                if (uri.getScheme().equals("js")){
                    if (uri.getAuthority().equals("webview")){
                        Log.e("TAG", "JS 调用了 Android 的方法"+uri.getQueryParameter("val1"));
                    }
                }
//                view.loadUrl(url);
                return true;
            }
        });
        myWebview.loadUrl("file:///android_asset/DemoJS.html");
        myWebview.addJavascriptInterface(new JS2NativeInterface(),"callNative");
//        myWebview.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KEYCODE_BACK && myWebview.canGoBack()){
            myWebview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void native2js(View v){
        myWebview.evaluateJavascript("javascript:if(window.callJS){window.callJS();}", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.v("ReceiveValue",value);
            }
        });
    }

//方法一：通过 WebView 的 addJavascriptInterface() 进行对象映射
    public class JS2NativeInterface{
        private final static String TAG="JSInterface";
        @JavascriptInterface
        public void callNative(String str){
            Log.v("callNative",str);
        }
    }

}
