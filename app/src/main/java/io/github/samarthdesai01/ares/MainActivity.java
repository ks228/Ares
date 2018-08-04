package io.github.samarthdesai01.ares;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private WebView myWebView;
    private ProgressBar spinner;
    private String mSearchTerm;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeStatusBarColor();
        initializeSpinner();
        initializeWebView();
        initializeToolBar();
        initializeDrawer();
        getWebsite();
        if (android.os.Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {   //Android M Or Over
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 100 );
        }
        if(Settings.canDrawOverlays(this)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String[] array = new String[]{"",""};
                    final Intent in = new Intent(MainActivity.this,OrderUpdates.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("loginInfo", array);
                    in.putExtras(bundle);
                    startService(in);
                }
            }, 5000);


        }
    }

    private void getWebsite() {

        //TODO: Add login screen for notifications
        //TODO: On login screen, first ask for overlay permission on submit, then start service
        //TODO: Show toast saying info went through
        //TODO: Stop service from rescheduling if we couldn't get to order page
        //TODO: Login screen will ask for login details and permissions but the service will triggered in Oncreate
        /**
         * CODE TO SUBMIT USERNAME
         * document.getElementById('ap_email_login').value='samarthdesai@utexas.edu'; //Fill out text field
         * var elems = document.getElementsByClassName('a-button-input'); //Find correct submit button
         * elems[3].click() //Click to move on to password
         * Now wait for redirect
         * document.getElementById('ap_password).value='ur pass'
         * document.getElementById('signInSubmit').click();
         * should be signed in now, now get orders
         *
         * load amazon webpage, on page finished check if url contains openid or signin
         * if true, then run first half of code to fill out form with your info
         * then have code to check if url contains signin only and ap
         * if true then hit signinsubmit
         * then check if url contains orders if so then we can run our usual scraping code.
         */
    }

    public void initializeToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    public void initializeDrawer(){
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
                            myWebView.loadUrl("https://www.amazon.com");
                        }
                        if(menuItem.getItemId() == R.id.nav_cart){
                            myWebView.loadUrl("https://www.amazon.com/gp/aw/c/ref=navm_hdr_cart");
                        }
                        if(menuItem.getItemId() == R.id.nav_orders){
                            myWebView.loadUrl("https://www.amazon.com/orders");
                        }
                        if(menuItem.getItemId() == R.id.nav_lists){
                            myWebView.loadUrl("https://www.amazon.com/gp/aw/ls");
                        }
                        if(menuItem.getItemId() == R.id.nav_account){
                            myWebView.loadUrl("https://www.amazon.com/your-account");
                        }
                        return true;
                    }
                });

    }

    public void initializeWebView(){
        myWebView = (WebView)findViewById(R.id.amazonTest);
        //Enable javascript for the webview
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url){
                super.onPageFinished(myWebView, url);
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
                        if(myWebView.getProgress() < 35){
                            handler.postDelayed(this, 100);
                        }else{
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mShareActionProvider != null)
                                        mShareActionProvider.setShareIntent(updateIntent());
                                    spinner.setVisibility(View.GONE);
                                    myWebView.setVisibility(View.VISIBLE);
                                }
                            }, 800);

                        }
                    }
                }, 100);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                myWebView.evaluateJavascript(
                        "document.getElementById('nav-greeting-name').innerHTML",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                if(!html.equals("null")){
                                    setGreetingText(html);
                                }
                            }
                        }
                );

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Remove the header for every page we visit
                        myWebView.loadUrl("javascript: var elem = document.getElementById(\"navbar\");\n" +
                                "elem.parentNode.removeChild(elem);");
                        //Remove the footer for every page we visit
                        myWebView.loadUrl("javascript: var elem = document.getElementById('gwm-Nav-footer');\n" +
                                "elem.parentNode.removeChild(elem);");
                        myWebView.loadUrl("javascript: var elem = document.getElementById('bottom');\n" +
                                "elem.parentNode.removeChild(elem);");
                        myWebView.loadUrl("javascript: var elem = document.getElementById('nav-ftr');\n" +
                                "elem.parentNode.removeChild(elem);");

                    }
                }, 800);

            }
        });

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        myWebView.loadUrl("https://www.amazon.com/");
    }

    public void initializeSpinner(){
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int padding = (int)(displayMetrics.widthPixels * .45);
        spinner.setPadding(padding,displayMetrics.heightPixels/2,padding,displayMetrics.heightPixels/2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Code for setting SearchView behavior
        getMenuInflater().inflate(R.menu.search_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                System.out.println(queryText);
                //Hide the keyboard on button press
                try{
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    searchView.setIconified(true);
                    MenuItemCompat.collapseActionView(searchItem);
                    myWebView.loadUrl("https://www.amazon.com/gp/aw/s/ref=is_s?k="+queryText.replace(" ", "+"));
                }catch(Exception e){

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
                if (mSearchTerm == null && newFilter == null) {
                    return true;
                }
                if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
                    return true;
                }
                mSearchTerm = newFilter;
                System.out.println(newText); //handle this
                return true;
            }
        });

        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        };
        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);


        //Code for Share Button
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.share_item);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        return super.onCreateOptionsMenu(menu);
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
                        myWebView.clearCache(false);
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    //Method used to update the share link when navigating to a new page
    public Intent updateIntent(){
        String currentURL = myWebView.getUrl();
        //strip the extraneous ref header on the URL for shorter links to send
        if(currentURL.contains("ref="))
            currentURL = currentURL.substring(0, currentURL.indexOf("ref="));

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, currentURL);
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    public void changeStatusBarColor()
    {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FF303F9F"));
    }

    //Method to set the drawer header text
    public void setGreetingText(String html){
        TextView userGreeting;
        userGreeting = findViewById(R.id.greetingText);
        String drawerGreeting = html;
        //Trim out the \n tags at the start and end
        drawerGreeting = drawerGreeting.substring(3,drawerGreeting.length()-16);
        //Trim excess space
        drawerGreeting = drawerGreeting.trim();
        //Remove the <b> tags
        drawerGreeting = drawerGreeting.replace("\\u003Cb>", "");
        drawerGreeting = drawerGreeting.replace("\\u003C/b>", "");

        userGreeting.setText(drawerGreeting);
    }
}
