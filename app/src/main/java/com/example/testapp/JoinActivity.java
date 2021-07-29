package com.example.testapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.testapp.util.HttpUtil;

public class JoinActivity extends ActivityHelper implements View.OnTouchListener {
    @Override
    public void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_join);
        Intent intent = getIntent();
        ContentValues values = (ContentValues) intent.getExtras().get("DATA");

        Button okBtn = findViewById(R.id.okBtn);

        okBtn.setOnTouchListener(this);

        NetworkTask networkTask = new NetworkTask(values);
        networkTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url = getString(R.string.send_mail);
        private ContentValues values;

        public NetworkTask(ContentValues values) {
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            HttpUtil httputil = new HttpUtil();
            result = httputil.request(url, values);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            switch (view.getId()) {
                case R.id.okBtn:
                    /**********************
                     * 1
                     **********************/
                    Intent intent = getIntent();
                    ContentValues values = (ContentValues) intent.getExtras().get("DATA");

                    gotoNextActivity(LoginActivity.class, values);
                    break;

                default:
                    ;
            }
        }
        return false;
    }
}
