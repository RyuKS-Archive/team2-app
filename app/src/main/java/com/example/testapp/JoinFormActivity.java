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

public class JoinFormActivity extends ActivityHelper implements View.OnTouchListener {
    private boolean networkEnabled = false;

    @Override
    public void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_join_form);
        Intent intent = getIntent();

        /**********************
         * 뷰 선언
         **********************/
        Button signUpBtn = findViewById(R.id.signUpBtn);
        EditText email = findViewById(R.id.email);
        TextView resultText = findViewById(R.id.resultText);


        String emailStr = intent.getExtras().getString("email");

        email.setText(emailStr);
        email.setEnabled(false);

        signUpBtn.setOnTouchListener(this);

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

        resultText.setText("network : " + networkEnabled);
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url = "http://210.216.61.151:12013/join.jsp";
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
            String tmpResult = "";
            boolean isUdt = false;

            EditText email = findViewById(R.id.email);
            TextView resultText = findViewById(R.id.resultText);
            //resultText.setText(result);

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(result)));

                NodeList nodelist = document.getElementsByTagName("result");
                Node textNode = nodelist.item(0).getChildNodes().item(0);

                //Test Code
                resultText.setText("value : " + textNode.getNodeValue());

                tmpResult = textNode.getNodeValue();
                if (tmpResult.equals("1")) {
                    isUdt = true;
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

            if (isUdt) {
                gotoNextActivity(JoinActivity.class, email.getText().toString());
            } else {
                //resultText.setText("value : " + tmpResult);
                resultText.setText("정상적으로 계정 생성을 완료하지 못습니다. 관리자에게 문의하여 주세요.");
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
                case R.id.signUpBtn:
                    //LogUtil.d("Next button");
                    /**********************
                     * 계정 생성 버튼
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
                        gotoNextActivity(JoinActivity.class);
                    } else {
                        /**********************
                         * 로그인 체크
                         **********************/
                        EditText email = findViewById(R.id.email);
                        EditText password = findViewById(R.id.password);
                        EditText name = findViewById(R.id.name);

                        if (email.getText().toString().length() == 0 || password.getText().toString().length() == 0 || name.getText().toString().length() == 0) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(this);
                            ab.setMessage("필수 입력란을 모두 입력해 주세요.");
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
                            values.put("name", name.getText().toString());

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
