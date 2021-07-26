package com.example.testapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class LoginActivity extends ActivityHelper implements View.OnTouchListener {
    private boolean networkEnabled = false;
    @Override
    public void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();

        /**********************
         * 뷰 선언
         **********************/
        Button loginBtn = findViewById(R.id.loginBtn);
        EditText email = findViewById(R.id.email);

        String emailStr = intent.getExtras().getString("email");

        email.setText(emailStr);
        email.setEnabled(false);

        loginBtn.setOnTouchListener(this);

        /**********************
         * 인터넷 연결 체크
         **********************/
        ConnectivityManager connectManager;
        NetworkInfo mobile;
        NetworkInfo wifi;

        connectManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if ((mobile != null && mobile.isConnected()) || (wifi != null && wifi.isConnected())) {
            networkEnabled = true;
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url = "http://210.216.61.151:12013/chk_login.jsp";
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
            EditText email = findViewById(R.id.email);
            TextView resultText = findViewById(R.id.resultText);
            //resultText.setText(result);

            String chk_flg = "";
            boolean isFlg = false;

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(result)));

                NodeList nodelist = document.getElementsByTagName("result");
                Node textNode = nodelist.item(0).getChildNodes().item(0);

                //Test Code
                //resultText.setText("value : " + textNode.getNodeValue());

                chk_flg = textNode.getNodeValue();
                if (chk_flg.equals("1")) {
                    isFlg = true;
                }

                //resultText.setText("value : " + isFlg);
            } catch(Exception e) {
                e.printStackTrace();
            }

            if (isFlg) {
                gotoNextActivity(MainActivity.class, email.getText().toString());
            } else {
                resultText.setText("미인증 계정이거나 비밀번호가 올바르지 않습니다.");
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
                    //LogUtil.d("Next button");
                    /**********************
                     * 다음 버튼
                     **********************/
                    if (!networkEnabled) {
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
                            ab.setMessage("이메일과 비밀번호를 입력해 주세요.");
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