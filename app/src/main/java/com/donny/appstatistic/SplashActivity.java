package com.donny.appstatistic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import static com.donny.appstatistic.CommonFunction.IsUserAlreadyLogin;

/**
 * Created by Donny on 9/11/2016.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Context context = getApplication();
                Intent intent = null;
                if (!IsUserAlreadyLogin(context))
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                else
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 1000);
    }

}
