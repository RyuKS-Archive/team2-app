package com.example.testapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp.adapter.ServerListAdapter;
import com.example.testapp.common.OnItemCheckedChange;
import com.example.testapp.util.HttpUtil;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActivityHelper implements View.OnTouchListener, OnItemCheckedChange {
    private final int HTTP_ERROR = 400;
    private String OS_TOKEN = null;
    private CountDownTimer expireTimer = null;

    @Override
    public void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_main);
        statusbarVisibility(true);
        Intent intent = getIntent();
        ContentValues values = (ContentValues) intent.getExtras().get("DATA");

        /**********************
         * 뷰 선언
         **********************/
        Button renewTokenBtn = findViewById(R.id.renewTokenBtn);
        Button serverUsageBtn = findViewById(R.id.serverUsageBtn);
        Button serverCreateBtn = findViewById(R.id.serverCreateBtn);

        renewTokenBtn.setOnTouchListener(this);
        serverUsageBtn.setOnTouchListener(this);
        serverCreateBtn.setOnTouchListener(this);

        TextView infoText1 = findViewById(R.id.infoText1);
        TextView infoText2 = findViewById(R.id.infoText2);

        infoText1.setText(values.get("name").toString() + "#");
        infoText2.setText(values.get("email").toString()  + "#");

        // GET AUTH TOKEN
        ContentValues tmpValue = new ContentValues();
        tmpValue.put("taskCode", 0);

        NetworkTask networkTask = new NetworkTask(tmpValue);
        networkTask.execute();
    }

    @Override
    public void onItemCheckedChange (boolean status, String id){
        // START AND STOP SERVER
        ContentValues tmpValue = new ContentValues();

        tmpValue.put("taskCode", 3);
        tmpValue.put("id", id);
        tmpValue.put("status", status);

        NetworkTask networkTask = new NetworkTask(tmpValue);
        networkTask.execute();
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private ContentValues values;
        private ContentValues response;
        private HashMap responseMap;
        private ServerListAdapter adapter = new ServerListAdapter(MainActivity.this);
        private ProgressDialog progressBar;

        public NetworkTask(ContentValues values) {
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            switch (values.getAsInteger("taskCode")) {
                case 0: //GET AUTH TOKEN
                    progressBar = new ProgressDialog(MainActivity.this);

                    progressBar.setCancelable(false);
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setMessage(getString(R.string.get_auth_token_msg));

                    progressBar.show();

                    break;
                case 1: // GET SERVER LIST
                    progressBar = new ProgressDialog(MainActivity.this);

                    progressBar.setCancelable(false);
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setMessage(getString(R.string.server_list_msg));

                    progressBar.show();

                    break;
                case 2: // CREATE SERVER
                    progressBar = new ProgressDialog(MainActivity.this);

                    progressBar.setCancelable(false);
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.setMessage(getString(R.string.server_create_msg));

                    progressBar.show();

                    break;
                case 3: // START AND STOP SERVER
                    progressBar = new ProgressDialog(MainActivity.this);

                    progressBar.setCancelable(false);
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    if (values.getAsBoolean("status")) {
                        progressBar.setMessage(getString(R.string.server_start_msg));
                    } else {
                        progressBar.setMessage(getString(R.string.server_stop_msg));
                    }

                    progressBar.show();

                    break;
                case 4: // GET SERVER USAGE
                    break;
                case 5:
                    break;
                default:
                    ;
            }

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            HttpUtil httputil = new HttpUtil();
            ContentValues tmpValue = null;

            switch (values.getAsInteger("taskCode")) {
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
                case 1: // GET SERVER LIST
                    tmpValue = new ContentValues();

                    if (OS_TOKEN != null) {
                        tmpValue.put("OS_TOKEN", OS_TOKEN);
                        responseMap = httputil.openstack_ServerList(OS_TOKEN);
                    } else {

                    }

                    break;
                case 2: // CREATE SERVER
                    tmpValue = new ContentValues();

                    if (OS_TOKEN != null) {
                        tmpValue.put("OS_TOKEN", OS_TOKEN);
                        response = httputil.openstack_CreateServer(tmpValue);
                    } else {

                    }

                    break;
                case 3: // START AND STOP SERVER
                    tmpValue = new ContentValues();

                    if (OS_TOKEN != null) {
                        tmpValue.put("OS_TOKEN", OS_TOKEN);
                        tmpValue.put("id", values.getAsString("id"));

                        if (values.getAsBoolean("status")) {
                            response = httputil.openstack_StartServer(tmpValue);
                        } else {
                            response = httputil.openstack_StopServer(tmpValue);
                        }

                    } else {

                    }

                    break;
                case 4: // GET SERVER USAGE
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
            if (progressBar != null && progressBar.isShowing()) {
                progressBar.dismiss();
            }

            switch (values.getAsInteger("taskCode")) {
                case 0: // GET AUTH TOKEN
                    if (response.getAsInteger("response_code") >= HTTP_ERROR) {
                        Toast.makeText(MainActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                    } else {
                        TextView infoText3 = findViewById(R.id.infoText3);
                        TextView infoText4 = findViewById(R.id.infoText4);

                        infoText3.setText(getString(R.string.expire_dt) + "#");
                        infoText4.setText(response.get("expires_at").toString());

                        OS_TOKEN = response.get("authToken").toString();

                        //
                        ContentValues tmpValues = new ContentValues();
                        tmpValues.put("taskCode", 1);

                        NetworkTask networkTask = new NetworkTask(tmpValues);
                        networkTask.execute();

                        if (expireTimer != null) {
                            expireTimer.cancel();
                        }
                        expireTimer = expireTimer();
                    }

                    break;
                case 1: // GET SERVER LIST
                    int response_code = (Integer) responseMap.get("response_code");

                    if (response_code >= HTTP_ERROR) {
                        Toast.makeText(MainActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                    } else {
                        ArrayList<ContentValues> tmpResults = (ArrayList<ContentValues>) responseMap.get("server_list");

                        if (tmpResults.size() == 0) {
                            Toast.makeText(MainActivity.this, getString(R.string.no_exist_msg), Toast.LENGTH_SHORT).show();
                        } else {
                            createListView(tmpResults);
                        }
                    }

                    break;
                case 2: // CREATE SERVER
                    if (response.getAsInteger("response_code") >= HTTP_ERROR) {
                        Toast.makeText(MainActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.created_server), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 3: // START AND STOP SERVER
                    if (response.getAsInteger("response_code") >= HTTP_ERROR) {
                        Toast.makeText(MainActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(MainActivity.this, getString(R.string.created_server), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 4: // GET SERVER USAGE
                    break;
                case 5:
                    break;
                default:
                    ;
            }

            super.onPostExecute(result);
        }

        private void setAdapter(ArrayList<ContentValues> results) {
            List<ServerListAdapter.Row> rows = new ArrayList<>();
            boolean isRunning = false;

            rows.add(new ServerListAdapter.Title("서버 리스트"));

            for (ContentValues result : results) {
                if (result.getAsString("status").equals("ACTIVE")) {
                    isRunning = true;
                } else {
                    isRunning = false;
                }

                rows.add(new ServerListAdapter.Item(result.getAsString("name"), result.getAsString("id"), isRunning));

            }

            adapter.setRows(rows);
        }

        private void createListView(ArrayList<ContentValues> results) {
            ListView lv = findViewById(R.id.serverList);

            setAdapter(results);
            lv.setAdapter(adapter);

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            switch (view.getId()) {
                case R.id.renewTokenBtn:
                    /**********************
                     * RENEW SERVER TOKEN
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
                        TextView nameText2 = findViewById(R.id.nameText2);
                        String min = nameText2.getText().toString().split(":")[1];

                        if (Integer.parseInt(min, 10) > 10) {
                            Toast.makeText(MainActivity.this, getString(R.string.renew_notice_msg), Toast.LENGTH_SHORT).show();
                        } else {
                            ContentValues values = new ContentValues();
                            values.put("taskCode", 0);

                            NetworkTask networkTask = new NetworkTask(values);
                            networkTask.execute();
                        }
                    }

                    break;
                case R.id.serverCreateBtn:
                    /**********************
                     * CREATE SERVER
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
                        ContentValues values = new ContentValues();
                        values.put("taskCode", 2);

                        Intent intent = new Intent(this, CreateProcedureActivity.class);
                        intent.putExtra("OS_TOKEN", OS_TOKEN);

                        startActivity(intent);

                        //NetworkTask networkTask = new NetworkTask(values);
                        //networkTask.execute();
                    }

                    break;
                case R.id.serverUsageBtn:
                    /**********************
                     * SERVER USAGE
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
                        ContentValues values = new ContentValues();
                        values.put("taskCode", 1);

                        NetworkTask networkTask = new NetworkTask(values);
                        networkTask.execute();
                    }

                    break;
                default:
                    ;
            }
        }
        return false;
    }

    public CountDownTimer expireTimer() {
        int interval = 1000;
        TextView nameText2 = findViewById(R.id.nameText2);

        CountDownTimer timer = new CountDownTimer(interval * 3600, interval) {
            public void onTick(long millisUntilFinished) {
                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000));
                String second = String.valueOf((getMin % (60 * 1000)) / 1000);
                //String millis = String.valueOf((getMin % (60 * 1000)) % 1000);

                if (hour.length() == 1) {
                    hour = "0" + hour;
                }
                if (min.length() == 1) {
                    min = "0" + min;
                }
                if (second.length() == 1) {
                    second = "0" + second;
                }

                nameText2.setText(hour + ":" + min + ":" + second);
            }

            public void onFinish() {
                ContentValues values = new ContentValues();
                values.put("taskCode", 0);

                NetworkTask networkTask = new NetworkTask(values);
                networkTask.execute();
            }
        }.start();

        return timer;
    }
}