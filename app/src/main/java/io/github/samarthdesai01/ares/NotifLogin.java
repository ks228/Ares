package io.github.samarthdesai01.ares;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NotifLogin extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_login);

        Button navToNotif = findViewById(R.id.btn_login);

        navToNotif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(getApplicationContext())) {   //Android M Or Over
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 100);
                } else {
                    Intent navToLogin = new Intent(NotifLogin.this, NotifLogin.class);
                    final EditText username = findViewById(R.id.input_email);
                    final EditText password = findViewById(R.id.input_password);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String[] array = new String[]{username.getText().toString(), password.getText().toString()};
                            final Intent in = new Intent(NotifLogin.this, OrderUpdates.class);
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("loginInfo", array);
                            in.putExtras(bundle);
                            startService(in);
                            Intent navBackHome = new Intent(NotifLogin.this, MainActivity.class);
                            startActivity(navBackHome);
                        }
                    }, 100);
                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final EditText username = findViewById(R.id.input_email);
        final EditText password = findViewById(R.id.input_password);
        // Check which request we're responding to
        if (requestCode == 100) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if (Settings.canDrawOverlays(this)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String[] array = new String[]{username.getText().toString(), password.getText().toString()};
                            final Intent in = new Intent(NotifLogin.this, OrderUpdates.class);
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("loginInfo", array);
                            in.putExtras(bundle);
                            startService(in);
                        }
                    }, 1000);
                }else{
                    Toast.makeText(this, "Make sure the overlay permission is on! This is required for checking delivery status", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
