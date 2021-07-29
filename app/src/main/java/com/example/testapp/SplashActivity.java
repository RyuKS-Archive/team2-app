package com.example.testapp;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;


public class SplashActivity extends ActivityHelper {

    @Override
    protected void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_splash);

        //loading
        for (int cnt = 0; cnt < 3; cnt++) {
            animationTask loading = new animationTask(cnt);
            loading.execute();
        }
    }

    public class animationTask extends AsyncTask<Void, Void, String> {
        private int count = 0;

        public animationTask(int count) {
            this.count = count;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            TextView nameText3 = findViewById(R.id.nameText3);

            switch (count) {
                case 0:
                    nameText3.setText("● ○ ○");
                    break;
                case 1:
                    nameText3.setText("● ● ○");
                    break;
                case 2:
                    nameText3.setText("● ● ●");

                    gotoNextActivity(LoginFormActivity.class);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;
                default:
                    ;
            }
        }
    }
}
