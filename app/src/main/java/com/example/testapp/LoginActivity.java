package com.example.testapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.testapp.util.HttpUtil;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class LoginActivity extends ActivityHelper implements View.OnTouchListener {
    private final int HTTP_OK = 200;
    private final int E0001 = 400;

    @Override
    public void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        ContentValues values = (ContentValues) intent.getExtras().get("DATA");

        /**********************
         * 뷰 선언
         **********************/
        Button loginBtn = findViewById(R.id.loginBtn);
        EditText email = findViewById(R.id.email);
        TextView backText = findViewById(R.id.backText);

        email.setText(values.get("email").toString());
        email.setEnabled(false);

        loginBtn.setOnTouchListener(this);

        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backText.setPaintFlags(backText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                gotoPrevActivity();
            }
        });

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url = getString(R.string.chk_login);
        private ContentValues values;
        private ContentValues response;

        public NetworkTask(ContentValues values) {
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String getUrl = url + "?email=" + values.get("email") + "&password=" + values.get("password");
            HttpUtil httputil = new HttpUtil(getUrl, "GET");
            response = httputil.request(null);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            TextView resultText = findViewById(R.id.resultText);

            String email = "";
            String name = "";
            String password = "";
            int auth_flg = 0;
            int os_use_flg = 0;
            String os_project_name = "";
            String os_user_domain_name = "";
            String os_project_domain_name = "";

            if (response.getAsInteger("code") == HTTP_OK) {
                try {
                    JSONObject resJson = new JSONObject(response.getAsString("result"));
                    email = resJson.getString("email");
                    name = resJson.getString("name");
                    password = values.get("password").toString();
                    auth_flg = resJson.getInt("auth_flg");
                    os_use_flg = resJson.getInt("os_use_flg");
                    os_project_name = resJson.getString("os_project_name");
                    os_user_domain_name = resJson.getString("os_user_domain_name");
                    os_project_domain_name = resJson.getString("os_project_domain_name");
                    //차후 optional

                    if (auth_flg == 1) {
                        ContentValues user_info = new ContentValues();
                        user_info.put("email", email);
                        user_info.put("password", password);
                        user_info.put("name", name);
                        user_info.put("os_project_name", os_project_name);
                        user_info.put("os_user_domain_name", os_user_domain_name);
                        user_info.put("os_project_domain_name", os_project_domain_name);

                        if (os_use_flg == 1) {
                            gotoNextActivity(MainActivity.class, user_info);
                        } else {
                            gotoNextActivity(OsRegistActivity.class, user_info);
                        }
                    } else {
                        resultText.setText(R.string.deny_login_alert_msg);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            } else if (response.getAsInteger("code") == E0001) {
                resultText.setText(R.string.deny_login_alert_msg);
            } else {
                resultText.setText(R.string.internal_server_error);
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
                case R.id.loginBtn:
                    /**********************
                     * 다음 버튼
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

                        //TEST LINE
                        //EditText email = findViewById(R.id.email);
                        //gotoNextActivity(MainActivity.class);
                    } else {
                        /**********************
                         * 로그인 체크
                         **********************/
                        EditText email = findViewById(R.id.email);
                        EditText password = findViewById(R.id.password);
                        if (email.getText().toString().length() == 0 || password.getText().toString().length() == 0 ) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(this);
                            ab.setMessage(R.string.chk_input_pw_msg);
                            ab.setIcon(android.R.drawable.ic_dialog_alert);
                            ab.setCancelable(false);
                            ab.setPositiveButton(R.string.description_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            ab.show();
                        } else {
                            ContentValues values = new ContentValues();
                            values.put("email", email.getText().toString());
                            values.put("password", password.getText().toString());

                            NetworkTask networkTask = new NetworkTask(values);
                            networkTask.execute();
                        }
                    }
                    break;
                default:
                    ;
            }
        }
        return false;
    }
}