package com.example.testapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class LoginActivity extends ActivityHelper implements View.OnTouchListener {
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
            //String result;
            HttpUtil httputil = new HttpUtil(url, "GET");
            response = httputil.request(values);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            EditText email = findViewById(R.id.email);
            TextView resultText = findViewById(R.id.resultText);

            String chk_flg = "";
            String password = "";
            String name = "";
            String os_use_flg = "";
            String os_project_name = "";
            String os_user_domain_name = "";
            String os_project_domain_name = "";
            boolean isFlg = false;

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(result)));

                NodeList nodelist = document.getElementsByTagName("result");
                Node result_node = nodelist.item(0).getChildNodes().item(0);

                nodelist = document.getElementsByTagName("password");
                Node password_node = nodelist.item(0).getChildNodes().item(0);
                nodelist = document.getElementsByTagName("name");
                Node name_node = nodelist.item(0).getChildNodes().item(0);
                nodelist = document.getElementsByTagName("os_use_flg");
                Node os_use_flg_node = nodelist.item(0).getChildNodes().item(0);
                nodelist = document.getElementsByTagName("os_project_name");
                Node os_project_name_node = nodelist.item(0).getChildNodes().item(0);
                nodelist = document.getElementsByTagName("os_user_domain_name");
                Node os_user_domain_name_node = nodelist.item(0).getChildNodes().item(0);
                nodelist = document.getElementsByTagName("os_project_domain_name");
                Node os_project_domain_name_node = nodelist.item(0).getChildNodes().item(0);

                os_use_flg = os_use_flg_node.getNodeValue();
                password = password_node.getNodeValue();
                name = name_node.getNodeValue();

                if (os_use_flg.equals("1")) {
                    os_project_name = os_project_name_node.getNodeValue();
                    os_user_domain_name = os_user_domain_name_node.getNodeValue();
                    os_project_domain_name = os_project_domain_name_node.getNodeValue();
                }

                chk_flg = result_node.getNodeValue();
                if (chk_flg.equals("1")) {
                    isFlg = true;
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

            if (isFlg) {
                if (os_use_flg.equals("1")) {
                    ContentValues user_info = new ContentValues();
                    user_info.put("email", email.getText().toString());
                    user_info.put("password", password);
                    user_info.put("name", name);
                    user_info.put("os_project_name", os_project_name);
                    user_info.put("os_user_domain_name", os_user_domain_name);
                    user_info.put("os_project_domain_name", os_project_domain_name);

                    gotoNextActivity(MainActivity.class, user_info);
                } else {
                    ContentValues user_info = new ContentValues();
                    user_info.put("email", email.getText().toString());
                    user_info.put("password", password);
                    user_info.put("name", name);
                    user_info.put("os_project_name", os_project_name);
                    user_info.put("os_user_domain_name", os_user_domain_name);
                    user_info.put("os_project_domain_name", os_project_domain_name);

                    gotoNextActivity(OsRegistActivity.class, user_info);
                }
            } else {
                resultText.setText(R.string.deny_login_alert_msg);
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