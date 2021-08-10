package com.example.testapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.testapp.util.HttpUtil;

import java.util.HashMap;

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
        Button fourBtn = findViewById(R.id.fourBtn);

        oneBtn.setOnTouchListener(this);
        twoBtn.setOnTouchListener(this);
        threeBtn.setOnTouchListener(this);
        fourBtn.setOnTouchListener(this);

        TextView infoText1 = findViewById(R.id.infoText1);
        TextView infoText2 = findViewById(R.id.infoText2);

        infoText1.setText(values.get("name").toString() + "#");
        infoText2.setText(values.get("email").toString()  + "#");

        // GET AUTH TOKEN
        ContentValues tmpValue = new ContentValues();
        tmpValue.put("btnNum", 0);

        NetworkTask networkTask = new NetworkTask(tmpValue);
        networkTask.execute();

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private ContentValues values;
        private ContentValues response;
        private HashMap responseMap;

        public NetworkTask(ContentValues values) {
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            HttpUtil httputil = new HttpUtil();
            ContentValues tmpValue = null;

            switch (values.getAsInteger("btnNum")) {
                case 0: //GET AUTH TOKEN
                    Intent intent = getIntent();
                    ContentValues user_info = (ContentValues) intent.getExtras().get("DATA");

                    ContentValues auth_token = new ContentValues();

                    auth_token.put("IS_SCOPE", false);
                    auth_token.put("OS_USERNAME", user_info.getAsString("email").split("@")[0]);
                    auth_token.put("OS_PASSWORD", user_info.getAsString("password"));
                    auth_token.put("OS_PROJECT_NAME", "");
                    auth_token.put("OS_USER_DOMAIN_NAME", "Default");
                    auth_token.put("OS_PROJECT_DOMAIN_NAME", "");

                    response = httputil.openstack_getAuthToken(auth_token);
                    response = httputil.openstack_getAuthScopeToken(response.getAsString("authToken"));

                    break;
                case 1:
                    tmpValue = new ContentValues();

                    if (OS_TOKEN != null) {
                        // GET SCOPE TOKEN
                        //OS_TOKEN = httputil.openstack_getAuthScopeToken(OS_TOKEN);
                        tmpValue.put("OS_TOKEN", OS_TOKEN);
                    } else {
                        tmpValue.put("OS_TOKEN", "");
                    }

                    responseMap = httputil.openstack_ServerList(OS_TOKEN);

                    break;
                case 2:
                    tmpValue = new ContentValues();

                    if (OS_TOKEN != null) {
                        // GET SCOPE TOKEN
                        // OS_TOKEN = httputil.openstack_getAuthScopeToken(OS_TOKEN);
                        tmpValue.put("OS_TOKEN", OS_TOKEN);
                    } else {
                        tmpValue.put("OS_TOKEN", "");
                    }

                    //response = httputil.openstack_CreateServer(tmpValue);

                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                default:
                    ;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch (values.getAsInteger("btnNum")) {
                case 0:
                    TextView infoText3 = findViewById(R.id.infoText3);

                    infoText3.setText("expire_dt# " + response.get("expires_at"));
                    OS_TOKEN = response.get("authToken").toString();
                    break;
                case 1:


                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                default:
                    ;
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
                        ContentValues values = new ContentValues();
                        values.put("btnNum", 1);

                        NetworkTask networkTask = new NetworkTask(values);
                        networkTask.execute();
                    }

                    break;
                case R.id.twoBtn:
                    /**********************
                     * 2
                     **********************/
                    Button twoBtn = findViewById(R.id.twoBtn);

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
                        ContentValues values = new ContentValues();
                        values.put("btnNum", 2);

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
                        Intent intent = getIntent();
                        String emailStr = intent.getExtras().getString("email");

                        ContentValues values = new ContentValues();
                        values.put("btnNum", 3);

                        //NetworkTask networkTask = new NetworkTask(values);
                        //networkTask.execute();
                    }

                    break;

                case R.id.fourBtn:
                    /**********************
                     * 3
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
                        Intent intent = getIntent();
                        String emailStr = intent.getExtras().getString("email");

                        ContentValues values = new ContentValues();
                        values.put("btnNum", 4);

                        //NetworkTask networkTask = new NetworkTask(values);
                        //networkTask.execute();
                    }

                    break;

                case R.id.fiveBtn:
                    /**********************
                     * 3
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
                        Intent intent = getIntent();
                        String emailStr = intent.getExtras().getString("email");

                        ContentValues values = new ContentValues();
                        values.put("btnNum", 5);

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