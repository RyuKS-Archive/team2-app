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
import android.widget.TextView;

import com.example.testapp.util.HttpUtil;

public class MainActivity extends ActivityHelper implements View.OnTouchListener {
    private String OS_TOKEN = null;
    @Override
    public void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        ContentValues values = (ContentValues) intent.getExtras().get("DATA");

        /**********************
         * 뷰 선언
         **********************/
        Button oneBtn = findViewById(R.id.oneBtn);
        Button twoBtn = findViewById(R.id.twoBtn);
        Button threeBtn = findViewById(R.id.threeBtn);

        oneBtn.setOnTouchListener(this);
        twoBtn.setOnTouchListener(this);
        threeBtn.setOnTouchListener(this);

        TextView infoText1 = findViewById(R.id.infoText1);
        TextView infoText2 = findViewById(R.id.infoText2);

        infoText1.setText(values.get("name").toString() + "#");
        infoText2.setText(values.get("email").toString()  + "#");

        // 인텐트 os데이터 가지고와서 계정 토큰 생성

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private ContentValues values;
        private ContentValues response;

        public NetworkTask(ContentValues values) {
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            HttpUtil httputil = new HttpUtil();

            if (values.get("btnNum") == "1") {
                ContentValues values = new ContentValues();

                values.put("OS_USERNAME", "admin");
                values.put("OS_PASSWORD", "qhrwl4857!");
                values.put("OS_PROJECT_NAME", "admin");
                values.put("OS_USER_DOMAIN_NAME", "Default");
                values.put("OS_PROJECT_DOMAIN_NAME", "Default");

                response = httputil.openstack_getAuthToken(values);
            } else if (values.get("btnNum") == "2") {
                response = httputil.openstack_selectUser(OS_TOKEN);
            } else if (values.get("btnNum") == "3") {
                ContentValues values = new ContentValues();

                if (OS_TOKEN != null) {
                    values.put("OS_TOKEN", OS_TOKEN);
                } else {
                    values.put("OS_TOKEN", "");
                }

                values.put("default_project_id", "");
                values.put("domain_id", "");
                values.put("enabled", true);
                values.put("protocol_id", "");
                values.put("unique_id", "");
                values.put("idp_id", "");
                values.put("name", "");
                values.put("password", "");
                values.put("description", "");
                values.put("email", "");
                values.put("ignore_password_expiry", true);

                response = httputil.openstack_createUser(values);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Test Code

            if (values.get("btnNum") == "1") {
                TextView infoText3 = findViewById(R.id.infoText3);
                infoText3.setText("expire_dt# " + response.get("expires_at"));
                //TextView authToken = findViewById(R.id.authToken);
                //authToken.setText("인증 토큰 : " + response.get("authToken"));
                OS_TOKEN = response.get("authToken").toString();
            } else if (values.get("btnNum") == "2") {

            } else if (values.get("btnNum") == "3") {

            }

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            switch (view.getId()) {
                case R.id.oneBtn:
                    /**********************
                     * 1
                     **********************/
                    Button oneBtn = findViewById(R.id.oneBtn);

                    if (!networkCheck()) {
                        AlertDialog.Builder ab = new AlertDialog.Builder(this);
                        ab.setMessage("네트워크 연결 상태를 확인해 주세요.");
                        ab.setIcon(android.R.drawable.ic_dialog_alert);
                        ab.setCancelable(false);
                        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        ab.show();

                    } else {
                        ContentValues values = new ContentValues();
                        values.put("btnNum", "1");

                        //NetworkTask networkTask = new NetworkTask(values);
                        //networkTask.execute();
                    }

                    break;
                case R.id.twoBtn:
                    /**********************
                     * 2
                     **********************/
                    Button twoBtn = findViewById(R.id.twoBtn);

                    if (!networkCheck()) {
                        AlertDialog.Builder ab = new AlertDialog.Builder(this);
                        ab.setMessage("네트워크 연결 상태를 확인해 주세요.");
                        ab.setIcon(android.R.drawable.ic_dialog_alert);
                        ab.setCancelable(false);
                        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        ab.show();

                    } else {
                        Intent intent = getIntent();
                        String emailStr = intent.getExtras().getString("email");

                        ContentValues values = new ContentValues();
                        values.put("btnNum", "2");

                        //NetworkTask networkTask = new NetworkTask(values);
                        //networkTask.execute();
                    }

                    break;
                case R.id.threeBtn:
                    /**********************
                     * 3
                     **********************/
                    Button threeBtn = findViewById(R.id.threeBtn);

                    if (!networkCheck()) {
                        AlertDialog.Builder ab = new AlertDialog.Builder(this);
                        ab.setMessage("네트워크 연결 상태를 확인해 주세요.");
                        ab.setIcon(android.R.drawable.ic_dialog_alert);
                        ab.setCancelable(false);
                        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        ab.show();

                    } else {
                        Intent intent = getIntent();
                        String emailStr = intent.getExtras().getString("email");

                        ContentValues values = new ContentValues();
                        values.put("btnNum", "3");

                        //NetworkTask networkTask = new NetworkTask(values);
                        //networkTask.execute();
                    }

                    break;

                default:
                    ;
            }
        }
        return false;
    }
}