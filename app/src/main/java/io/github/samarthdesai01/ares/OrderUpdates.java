package io.github.samarthdesai01.ares;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderUpdates extends Service {
    int numAttempts = 0;
    String login[];
    WebView wv;
    public OrderUpdates() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        //Code for initializing Notification Channels or Oreo and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Order Updates";
            String description = "Get notified regarding active orders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("orderUpdates", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        System.out.println("Started command");

        login = intent.getExtras().getStringArray("loginInfo");
        final String username = login[0];
        final String password = login[1];
        System.out.println(username);
        System.out.println(password);

        final WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams params;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // If the device is version Oreo or greater
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSPARENT);
        } else {
            // If the device is pre-Oreo
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    PixelFormat.TRANSPARENT);
        }

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;
        params.width = 0;
        params.height = 0;

        wv = new WebView(getBaseContext());
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);

        wv.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url){
                super.onPageFinished(view, url);
                if(url.contains("openid")){
                    System.out.println("Hello Login");
                    wv.loadUrl("javascript: document.getElementById('ap_email_login').value='" + username + "';\n" +
                            "var elems = document.getElementsByClassName('a-button-input');\n" +
                            "elems[3].click()");
                }
                if(url.contains("/ap/signin") ){
                    System.out.println("On signin");
                    if(numAttempts < 2){
                        wv.loadUrl("javascript: document.getElementById('ap_password').value='"+ password +"';\n" +
                                "          document.getElementById('signInSubmit').click();");
                        System.out.println("Tried sign in");
                    }
                    numAttempts++;

                }
                if(url.contains("order-history") && !url.contains("ap/signin")){
                    numAttempts = 0;
                    System.out.println("got to orders page HELLO");
                    wv.evaluateJavascript("(function() { return document.getElementById('ordersContainer').innerHTML.toString(); })();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {
                                    System.out.println("got value");
                                    if(html != null){
                                        processPackageInfo(html);
                                    }else{
                                        stopSelf();
                                    }
                                }
                            });
                }

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                  System.out.println(url);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(wv.getProgress() < 80){
                            handler.postDelayed(this, 100);
                            //System.out.println(wv.getProgress());
                        }else{
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("Over 80!");

                                }
                            }, 800);

                        }
                    }
                }, 100);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d("Error", "loading web view: request: " + request + " error: " + error);
                stopSelf();
            }

        });

        wv.loadUrl("https://www.amazon.com/orders");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(true){
                    System.out.println("Cancelled");
                    handler.removeCallbacks(this);
                }else{
                    System.out.println("How did you even get here");
                    windowManager.addView(wv, params); //This needs to be here for the webview to function
                    //Even though this is never called it still runs properly for some reason
                }

            }
        }, 1000);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        wv.destroy();

        String[] array = login;
        Intent in = new Intent(this,OrderUpdates.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray("loginInfo", array);
        in.putExtras(bundle);

        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * 30), //Schedule check every 30 minutes
                PendingIntent.getService(this, 0, in, 0)
        );
        System.out.println("Scheduling Next Check");
        System.out.println("Destroying Process");
    }

    public void processPackageInfo(String orderPageHTML){
        String fixedHTML = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(orderPageHTML);
        //System.out.println(fixedHTML);
        Document orderPage = Jsoup.parse(fixedHTML);

        Elements packageData = orderPage.getElementsByClass("a-row a-grid-vertical-align a-grid-center");
        //iterate elements using enhanced for loop

        ArrayList<PackageInfo> packages = new ArrayList<>();

        for(Element e : packageData) {
            Element packageInfo = Jsoup.parse(e.getElementsByClass("item-view-left-col-inner").html());
            String packageName = packageInfo.select("img").attr("alt");
            String imageLink = packageInfo.select("img").attr("src");
            String packageStatusString = e.getElementsByClass("js-shipment-info aok-hidden").select("span").attr("data-cookiepayload");
            Gson g = new Gson();
            PackageStatus packageStat = g.fromJson(packageStatusString, PackageStatus.class);
            PackageInfo currentPackage = new PackageInfo(packageName, imageLink, packageStat.getPrimaryStatus(), packageStat.getShortStatus());
            packages.add(currentPackage);
            System.out.println(currentPackage.toString());

        }

        //stopSelf();
        checkStatusChanges(packages);
    }

    public void checkStatusChanges(ArrayList<PackageInfo> packageInfo){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEdit = preferences.edit();
        Gson g = new Gson();
        String json;
        ArrayList<PackageInfo> packages = new ArrayList<>(); //ArrayList to hold the most recent instance of packageInfo
        if(preferences.contains("packageData")){ //Get the most recent instance of packageInfo
            System.out.println("Getting Previous Data");
            json = preferences.getString("packageData", "");
            Type type = new TypeToken<ArrayList<PackageInfo>>(){}.getType();
            packages = g.fromJson(json, type);
        }else{ //First run, store data and stop service
            System.out.println("First Storage");
            json = g.toJson(packageInfo);
            prefEdit.putString("packageData",json);
            prefEdit.commit();
            stopSelf();
        }
        if(packages.size() != 0){
            for(PackageInfo p : packageInfo){ //Loop through all active packages
                for(PackageInfo previousp : packages){
                    if(p.packageName.equals(previousp.packageName)){
                        NotificationCompat.Builder mBuilder;
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                        //Check if it's the same package
                        if(p.packagePrimaryStatus!= null && previousp.packagePrimaryStatus != null){
                            if((!p.packagePrimaryStatus.equals(previousp.packagePrimaryStatus))){
                                System.out.println("Status Update!");
                                mBuilder = new NotificationCompat.Builder(this, "orderUpdates")
                                        .setSmallIcon(R.drawable.ic_markunread_mailbox_black_24dp)
                                        .setContentTitle(p.packagePrimaryStatus)
                                        .setContentText(p.packageName)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                                notificationManager.notify(1, mBuilder.build());
                            }
                            else{
                                if(previousp.packageShortStatus == null && p.packageShortStatus != null){
                                    mBuilder = new NotificationCompat.Builder(this, "orderUpdates")
                                            .setSmallIcon(R.drawable.ic_markunread_mailbox_black_24dp)
                                            .setContentTitle("Your package has shipped")
                                            .setContentText(p.packageName)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH);
                                }
                            }
                        }

                    }
                }
            }
        }

        //Now store packageInfo as the new
        json = g.toJson(packageInfo);
        prefEdit.putString("packageData",json);
        prefEdit.commit();
        System.out.println("Stored new data");
        stopSelf();
    }
}
