package com.example.testapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.testapp.util.HttpUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
        private String url = "http://210.216.61.151:12013/send_mail.jsp";
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
                    //LogUtil.d("Next button");
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
