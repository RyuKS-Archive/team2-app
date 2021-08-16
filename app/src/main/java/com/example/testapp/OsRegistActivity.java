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

import java.net.HttpURLConnection;

public class OsRegistActivity extends ActivityHelper implements View.OnTouchListener {
    private final String HTTP_NO_CONTENT = "204";
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
        private String url = getString(R.string.udt_os_use);

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            HttpUtil httputil = new HttpUtil();

            //GET AUTH TOKEN
            ContentValues auth_token = new ContentValues();

            auth_token.put("IS_SCOPE", true);
            auth_token.put("OS_USERNAME", "admin");
            auth_token.put("OS_PASSWORD", "qhrwl4857!");
            auth_token.put("OS_PROJECT_NAME", "admin");
            auth_token.put("OS_USER_DOMAIN_NAME", "Default");
            auth_token.put("OS_PROJECT_DOMAIN_NAME", "Default");

            response = httputil.openstack_getAuthToken(auth_token);

            //CREATE USER
            if (response != null) {
                OS_TOKEN = response.get("authToken").toString();
                ContentValues create_user = new ContentValues();

                if (OS_TOKEN != null) {
                    create_user.put("OS_TOKEN", OS_TOKEN);
                } else {
                    create_user.put("OS_TOKEN", "");
                }

                create_user.put("default_project_id", "testproject");
                create_user.put("domain_id", "default");
                create_user.put("enabled", true);
                create_user.put("protocol_id", "");
                create_user.put("unique_id", "");
                create_user.put("idp_id", "");
                create_user.put("name", values.get("email").toString().split("@")[0]);
                create_user.put("password", values.get("password").toString());
                create_user.put("description", "Auto generated user by Bono 2 team");
                create_user.put("email", values.get("email").toString());
                create_user.put("ignore_password_expiry", true);

                // openstack user create --domain default --password-prompt {user}
                response = httputil.openstack_CreateUser(create_user);

                //ADD ROLE
                if(response.getAsInteger("response_code") == HttpURLConnection.HTTP_CREATED) {
                    ContentValues add_role = new ContentValues();
                    add_role.put("OS_TOKEN", OS_TOKEN);
                    add_role.put("user_id", response.getAsString("user_id"));

                    // openstack role add --project testproject --user {user} testrole
                    result = httputil.openstack_AddRole(add_role);

                    if (result.equals(HTTP_NO_CONTENT)) {
                        ContentValues os_use_udt = new ContentValues();
                        os_use_udt.put("email", values.get("email").toString().split("@")[0]);
                        //os_use_udt.put("password", values.get("password").toString());

                        httputil.setUrl(url);
                        response = httputil.request(os_use_udt);
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals(HTTP_NO_CONTENT)) {
                gotoNextActivity(MainActivity.class, values);
            } else {
                // 에러
            }
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
