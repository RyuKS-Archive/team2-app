package com.example.testapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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

public class LoginFormActivity extends ActivityHelper implements OnTouchListener {
    private String emailChk = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private String tmpEmail = "";
    private final int HTTP_OK = 200;
    private final int E0001 = 400;

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
                    resultText.setText("");
                } else {
                    resultText.setText(R.string.email_format_alert_msg);
                }
            }
        });
        loginBtn.setOnTouchListener(this);

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url = getString(R.string.chk_email);
        private ContentValues values;
        private ContentValues response;

        public NetworkTask(ContentValues values) {
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String getUrl = url + "?email=" + values.get("email");
            HttpUtil httputil = new HttpUtil(getUrl, "GET");
            response = httputil.request(null);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (response.getAsInteger("code") == HTTP_OK) {
                gotoNextActivity(LoginActivity.class, values);
            } else if (response.getAsInteger("code") == E0001) {
                gotoNextActivity(JoinFormActivity.class, values);
            } else {
                //runtime error
            }

            /*
            try {
                //JSONObject resJson = new JSONObject(result);
                //String email = resJson.getString("email");

            } catch(Exception e) {
                e.printStackTrace();
            }
             */
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

                    } else {
                        /**********************
                         * email 등록 여부 체크
                         **********************/
                        EditText email = findViewById(R.id.email);

                        if (email.getText().toString().length() == 0 || !email.getText().toString().matches(emailChk)) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(this);
                            ab.setMessage(R.string.email_format_chk_msg);
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
