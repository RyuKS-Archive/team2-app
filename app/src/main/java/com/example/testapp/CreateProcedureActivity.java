package com.example.testapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp.adapter.ServerListAdapter;
import com.example.testapp.util.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateProcedureActivity extends ActivityHelper implements View.OnTouchListener {
    private String OS_TOKEN = null;

    @Override
    protected void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_create_procedure);

        Intent intent = getIntent();
        OS_TOKEN = intent.getExtras().getString("OS_TOKEN");

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = (int) (size.x * 0.9);
        int height = (int) (size.y * 0.9);

        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        Button nextBtn = findViewById(R.id.nextBtn);

        nextBtn.setOnTouchListener(this);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        return true;
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private ContentValues values;
        private ContentValues response;
        private ProgressDialog progressBar;

        public NetworkTask(ContentValues values) {
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(CreateProcedureActivity.this);

            progressBar.setCancelable(false);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setMessage(getString(R.string.server_create_msg));

            progressBar.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            HttpUtil httputil = new HttpUtil();

            if (OS_TOKEN != null) {
                values.put("OS_TOKEN", OS_TOKEN);
                response = httputil.openstack_CreateServer(values);
            } else {

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressBar != null && progressBar.isShowing()) {
                progressBar.dismiss();
            }

            if (response.getAsInteger("response_code") >= 400) {
                Toast.makeText(CreateProcedureActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CreateProcedureActivity.this, getString(R.string.created_server), Toast.LENGTH_SHORT).show();
                finish();
            }

            super.onPostExecute(result);
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            switch (view.getId()) {
                case R.id.nextBtn:
                    /**********************
                     * 다음 버튼
                     **********************/
                    EditText instanceName = findViewById(R.id.instanceName);

                    ContentValues values = new ContentValues();
                    values.put("instanceName", instanceName.getText().toString());

                    NetworkTask networkTask = new NetworkTask(values);
                    networkTask.execute();

                    break;
                default:
                    ;
            }
        }
        return false;
    }
}
