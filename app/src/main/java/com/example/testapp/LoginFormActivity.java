package com.example.testapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.testapp.util.HttpUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class LoginFormActivity extends ActivityHelper implements OnTouchListener {
    private String emailChk = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private String tmpEmail = "";

    @Override
    public void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_login_form);


        /**********************
         * 뷰 선언
         **********************/
        Button loginBtn = findViewById(R.id.nextBtn);
        EditText email = findViewById(R.id.email);
        TextView resultText = findViewById(R.id.resultText);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tmpEmail = email.getText().toString().trim();
                if(tmpEmail.matches(emailChk) && s.length() > 0){
                    resultText.setText("Team 2 Bono");
                } else {
                    resultText.setText("이메일 형식이 아닙니다.");
                }
            }
        });
        loginBtn.setOnTouchListener(this);

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url = "http://210.216.61.151:12013/chk_email.jsp";
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
            //Test Code
            EditText email = findViewById(R.id.email);
            TextView resultText = findViewById(R.id.resultText);
            String tmpEmail = "";
            boolean isEmail = false;

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(result)));

                NodeList nodelist = document.getElementsByTagName("result");
                Node textNode = nodelist.item(0).getChildNodes().item(0);

                //Test Code
                resultText.setText("value : " + textNode.getNodeValue());

                tmpEmail = textNode.getNodeValue();
                if (tmpEmail.equals("1")) {
                    isEmail = true;
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

            if (isEmail) {
                gotoNextActivity(LoginActivity.class, values);
            } else {
                gotoNextActivity(JoinFormActivity.class, values);
                //test code 0720
                //gotoNextActivity(MainActivity.class, email.getText().toString());
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
                case R.id.nextBtn:
                    //LogUtil.d("Next button");
                    /**********************
                     * 다음 버튼
                     **********************/
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
                        /**********************
                         * email 등록 여부 체크
                         **********************/
                        EditText email = findViewById(R.id.email);

                        if (email.getText().toString().length() == 0 || !email.getText().toString().matches(emailChk)) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(this);
                            ab.setMessage("이메일을 다시 입력해 주세요.");
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
