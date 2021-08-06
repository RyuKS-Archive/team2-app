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

import com.example.testapp.util.HttpUtil;

public class OsRegistActivity extends ActivityHelper implements View.OnTouchListener {
    private String OS_TOKEN = "";
    private ContentValues values = null;

    @Override
    public void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_osregist);
        Intent intent = getIntent();
        values = (ContentValues) intent.getExtras().get("DATA");

        /**********************
         * 뷰 선언
         **********************/
        Button registBtn = findViewById(R.id.registBtn);

        registBtn.setOnTouchListener(this);
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private ContentValues response;

        public NetworkTask() {}

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            HttpUtil httputil = new HttpUtil();

            ContentValues auth_info = new ContentValues();

            auth_info.put("OS_USERNAME", "admin");
            auth_info.put("OS_PASSWORD", "qhrwl4857!");
            auth_info.put("OS_PROJECT_NAME", "admin");
            auth_info.put("OS_USER_DOMAIN_NAME", "Default");
            auth_info.put("OS_PROJECT_DOMAIN_NAME", "Default");

            response = httputil.openstack_getAuthToken(auth_info);

            if (response != null) {
                OS_TOKEN = response.get("authToken").toString();
                ContentValues regist_info = new ContentValues();

                if (OS_TOKEN != null) {
                    regist_info.put("OS_TOKEN", OS_TOKEN);
                } else {
                    regist_info.put("OS_TOKEN", "");
                }

                regist_info.put("default_project_id", "myproject"); // service
                regist_info.put("domain_id", "default");
                regist_info.put("enabled", true);
                regist_info.put("protocol_id", ""); //
                regist_info.put("unique_id", "");   //
                regist_info.put("idp_id", "");      //
                regist_info.put("name", values.get("user").toString().split("@")[0]);
                regist_info.put("password", values.get("password").toString());
                regist_info.put("description", "Auto generated user by Bono 2 team");
                regist_info.put("email", values.get("user").toString());
                regist_info.put("ignore_password_expiry", true);

                response = httputil.openstack_createUser(values);


            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            switch (view.getId()) {
                case R.id.registBtn:
                    /**********************
                     * 생성 버튼
                     **********************/
                    if (!networkCheck()) {
                        AlertDialog.Builder ab = new AlertDialog.Builder(this);
                        ab.setMessage(R.string.network_enable_alert_msg);
                        ab.setIcon(android.R.drawable.ic_dialog_alert);
                        ab.setCancelable(false);
                        ab.setPositiveButton(R.string.description_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        ab.show();

                    } else {
                        NetworkTask networkTask = new NetworkTask();
                        networkTask.execute();
                    }
                    break;

                default:
                    ;
            }
        }
        return false;
    }
}
