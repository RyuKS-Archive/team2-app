package com.example.testapp;

import android.os.Bundle;

public class SplashActivity extends ActivityHelper {

    @Override
    protected void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_splash);
/*
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
        gotoNextActivity(LoginFormActivity.class);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
