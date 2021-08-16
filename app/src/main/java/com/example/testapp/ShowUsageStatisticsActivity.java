package com.example.testapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp.util.HttpUtil;

import org.w3c.dom.Text;

public class ShowUsageStatisticsActivity extends ActivityHelper {
    private String OS_TOKEN = null;
    private String tenantId = null;
    private final int HTTP_ERROR = 400;

    @Override
    protected void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_show_usage_statistics);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = (int) (size.x * 0.9);
        int height = (int) (size.y * 0.9);

        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        Intent intent = getIntent();
        OS_TOKEN = intent.getExtras().getString("OS_TOKEN");
        tenantId = intent.getExtras().getString("tenantId");

        TextView descriptText = findViewById(R.id.descriptText1);
        TextView closePopUp = findViewById(R.id.closePopUp);

        descriptText.setText(intent.getExtras().getString("serverName"));
        closePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        NetworkTask networkTask = new NetworkTask();
        networkTask.execute();
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private ContentValues values = new ContentValues();
        private ContentValues response;
        private ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(ShowUsageStatisticsActivity.this);

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
                values.put("tenantId", tenantId);

                response = httputil.openstack_UsageStatistics(values);
            } else {

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressBar != null && progressBar.isShowing()) {
                progressBar.dismiss();
            }

            if (response.getAsInteger("response_code") >= HTTP_ERROR) {
                Toast.makeText(ShowUsageStatisticsActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                //finish();
            } else {
                TextView total_hour = findViewById(R.id.total_hour);
                TextView total_vcpus_usage = findViewById(R.id.total_vcpus_usage);
                TextView total_memory_mb_usage = findViewById(R.id.total_memory_mb_usage);

                total_hour.setText(response.getAsString("total_hours"));
                total_vcpus_usage.setText(response.getAsString("total_vcpus_usage"));
                total_memory_mb_usage.setText(response.getAsString("total_memory_mb_usage"));
            }

            super.onPostExecute(result);
        }
    }
}
