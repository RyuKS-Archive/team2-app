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

public class JoinFormActivity extends ActivityHelper implements View.OnTouchListener {
    @Override
    public void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_join_form);
        Intent intent = getIntent();
        ContentValues values = (ContentValues) intent.getExtras().get("DATA");

        /**********************
         * 뷰 선언
         **********************/
        Button signUpBtn = findViewById(R.id.signUpBtn);
        EditText email = findViewById(R.id.email);
        TextView backText = findViewById(R.id.backText);

        email.setText(values.get("email").toString());
        email.setEnabled(false);

        signUpBtn.setOnTouchListener(this);

        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backText.setPaintFlags(backText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                gotoPrevActivity();
            }
        });

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url = getString(R.string.join);
        private ContentValues values;
        private ContentValues response;

        public NetworkTask(ContentValues values) {
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            //String result;
            HttpUtil httputil = new HttpUtil(url, "POST");
            response = httputil.request(values);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String tmpResult = "";
            boolean isUdt = false;

            EditText email = findViewById(R.id.email);
            TextView resultText = findViewById(R.id.resultText);

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(result)));

                NodeList nodelist = document.getElementsByTagName("result");
                Node textNode = nodelist.item(0).getChildNodes().item(0);

                tmpResult = textNode.getNodeValue();
                if (tmpResult.equals("1")) {
                    isUdt = true;
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

            if (isUdt) {
                ContentValues values = new ContentValues();
                values.put("email", email.getText().toString());

                gotoNextActivity(JoinActivity.class, values);
            } else {
                resultText.setTextColor(500186);
                resultText.setText(R.string.err_join_msg);
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
                    /**********************
                     * 계정 생성 버튼
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
                         * 로그인 체크
                         **********************/
                        EditText email = findViewById(R.id.email);
                        EditText password = findViewById(R.id.password);
                        EditText name = findViewById(R.id.name);

                        if (email.getText().toString().length() == 0 || password.getText().toString().length() == 0 || name.getText().toString().length() == 0) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(this);
                            ab.setMessage(R.string.chk_input_msg);
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
