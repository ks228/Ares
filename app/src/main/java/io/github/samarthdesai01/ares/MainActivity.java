package io.github.samarthdesai01.ares;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private WebView myWebView;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set custom toolbar as the main action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //Create a webview
        myWebView = (WebView)findViewById(R.id.amazonTest);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int padding = (int)(displayMetrics.widthPixels * .45);
        spinner.setPadding(padding,displayMetrics.heightPixels/2,padding,displayMetrics.heightPixels/2);

        //Enable javascript for the webview
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url){
             super.onPageFinished(myWebView, url);
                //System.out.println("Removed from page finish");

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        myWebView.loadUrl("javascript: var elem = document.getElementById(\"navbar\");\n" +
//                                "elem.parentNode.removeChild(elem);");
//                    }
//                }, 10);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        spinner.setVisibility(View.GONE);
//                        myWebView.setVisibility(View.VISIBLE);
//                    }
//                }, 2500);

/*                myWebView.loadUrl("javascript: var elem = document.getElementById(\"navbar\");\n" +
                        "elem.parentNode.removeChild(elem);");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.GONE);
                        myWebView.setVisibility(View.VISIBLE);
                    }
                }, 1000);*/
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                super.onPageStarted(myWebView, url, favicon);
                myWebView.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(myWebView.getProgress() < 30){
                            handler.postDelayed(this, 100);
                        }else{
//                            System.out.println("Removed from page start");
//                            myWebView.loadUrl("javascript: var elem = document.getElementById(\"navbar\");\n" +
//                                    "elem.parentNode.removeChild(elem);");
                            new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                spinner.setVisibility(View.GONE);
                                myWebView.setVisibility(View.VISIBLE);
                            }
                            }, 750);

                        }
                    }
                }, 100);

//                new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    myWebView.loadUrl("javascript: var elem = document.getElementById(\"navbar\");\n" +
//                            "elem.parentNode.removeChild(elem);");
//                 }
//                 }, 1500);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        spinner.setVisibility(View.GONE);
//                        myWebView.setVisibility(View.VISIBLE);
//                    }
//                }, 2000);

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    myWebView.loadUrl("javascript: var elem = document.getElementById(\"navbar\");\n" +
                            "elem.parentNode.removeChild(elem);");
                 }
                 }, 750);
            }



        });
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.loadUrl("https://www.amazon.com/");

        //Navigation Drawer Layout
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped

                        mDrawerLayout.closeDrawers();

                        if(menuItem.getItemId() == R.id.nav_home){
                            System.out.println("Hello!");
                            myWebView.loadUrl("https://www.amazon.com");
                        }
                        if(menuItem.getItemId() == R.id.nav_orders){
                            myWebView.loadUrl("https://www.amazon.com/orders");
                        }
                        if(menuItem.getItemId() == R.id.nav_lists){
                            myWebView.loadUrl("https://www.amazon.com/lists");
                        }

                        return true;
                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (myWebView.canGoBack()) {
                        myWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }




}
