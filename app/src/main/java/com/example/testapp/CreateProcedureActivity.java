package com.example.testapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp.adapter.FlavorListAdapter;
import com.example.testapp.adapter.ImageListAdapter;
import com.example.testapp.adapter.ServerListAdapter;
import com.example.testapp.util.HttpUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateProcedureActivity extends ActivityHelper implements View.OnTouchListener {
    private String OS_TOKEN = null;
    private final int HTTP_ERROR = 400;
    private ImageListAdapter adapterImg = new ImageListAdapter();
    private FlavorListAdapter adapterFlv = new FlavorListAdapter();

    @Override
    protected void activityStart(Bundle savedInstanceState) throws Exception {
        setContentView(R.layout.activity_create_procedure);

        Intent intent = getIntent();
        OS_TOKEN = intent.getExtras().getString("OS_TOKEN");

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = (int) (size.x * 0.9);
        int height = (int) (size.y * 0.9);

        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        Button nextBtn = findViewById(R.id.nextBtn);
        TextView closePopUp = findViewById(R.id.closePopUp);

        nextBtn.setOnTouchListener(this);

        closePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        InitailizeTask initalizeTask = new InitailizeTask();
        initalizeTask.execute();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        return true;
    }

    public class InitailizeTask extends AsyncTask<Void, Void, String> {
        private HashMap responseImg;
        private HashMap responseFlv;
        private int response_code;
        private ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(CreateProcedureActivity.this);

            progressBar.setCancelable(false);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setMessage(getString(R.string.server_init_msg));

            progressBar.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            HttpUtil httputil = new HttpUtil();

            if (OS_TOKEN != null) {
                responseImg = httputil.openstack_ImageList(OS_TOKEN);
                int response_img_code = (Integer) responseImg.get("response_code");

                responseFlv = httputil.openstack_FlavorList(OS_TOKEN);
                int response_flv_code = (Integer) responseFlv.get("response_code");

                if (response_img_code >= HTTP_ERROR || response_flv_code >= HTTP_ERROR) {
                    response_code = 1000;
                }
            } else {

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressBar != null && progressBar.isShowing()) {
                progressBar.dismiss();
            }

            if (response_code >= HTTP_ERROR) {
                Toast.makeText(CreateProcedureActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                ArrayList<ContentValues> imgResults = (ArrayList<ContentValues>) responseImg.get("image_list");
                ArrayList<ContentValues> flvResults = (ArrayList<ContentValues>) responseFlv.get("flavor_list");

                createImgListView(imgResults);
                createFlvListView(flvResults);
            }

            super.onPostExecute(result);
        }

        private void setImgAdapter(ArrayList<ContentValues> results) {
            List<ImageListAdapter.Row> rows = new ArrayList<>();

            for (ContentValues result : results) {
                rows.add(new ImageListAdapter.Item(result.getAsString("name"), result.getAsString("id")));
            }

            adapterImg.setRows(rows);
        }

        private void setFlvAdapter(ArrayList<ContentValues> results) {
            List<FlavorListAdapter.Row> rows = new ArrayList<>();

            for (ContentValues result : results) {
                rows.add(new FlavorListAdapter.Item(result.getAsString("name"), result.getAsString("id")));
            }

            adapterFlv.setRows(rows);
        }

        private void createImgListView(ArrayList<ContentValues> results) {
            ListView lv = findViewById(R.id.imageList);

            setImgAdapter(results);
            lv.setAdapter(adapterImg);
        }

        private void createFlvListView(ArrayList<ContentValues> results) {
            ListView lv = findViewById(R.id.flavorList);

            setFlvAdapter(results);
            lv.setAdapter(adapterFlv);
        }
    }

    public class CreateTask extends AsyncTask<Void, Void, String> {
        private ContentValues values;
        private ContentValues response;
        private ProgressDialog progressBar;

        public CreateTask(ContentValues values) {
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(CreateProcedureActivity.this);

            progressBar.setCancelable(false);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setMessage(getString(R.string.server_create_msg));

            progressBar.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            HttpUtil httputil = new HttpUtil();

            if (OS_TOKEN != null) {
                values.put("OS_TOKEN", OS_TOKEN);

                response = httputil.openstack_CreateServer(values);

                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressBar != null && progressBar.isShowing()) {
                progressBar.dismiss();
            }

            if (response.getAsInteger("response_code") >= HTTP_ERROR) {
                Toast.makeText(CreateProcedureActivity.this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show();
                //finish();
            } else {
                Toast.makeText(CreateProcedureActivity.this, getString(R.string.created_server), Toast.LENGTH_SHORT).show();
                finish();
            }

            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            switch (view.getId()) {
                case R.id.nextBtn:
                    /**********************
                     * 생성 버튼
                     **********************/
                    EditText instanceName = findViewById(R.id.instanceName);
                    ListView imageList = findViewById(R.id.imageList);
                    ListView flavorList = findViewById(R.id.flavorList);

                    if (instanceName.getText().toString().length() == 0 || imageList.getCheckedItemPosition() == -1 || flavorList.getCheckedItemPosition() == -1) {
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
                            values.put("instanceName", instanceName.getText().toString());

                            ImageListAdapter.Item imgItem = (ImageListAdapter.Item) adapterImg.getItem(imageList.getCheckedItemPosition());
                            FlavorListAdapter.Item flvItem = (FlavorListAdapter.Item) adapterFlv.getItem(flavorList.getCheckedItemPosition());

                            values.put("image_id", imgItem.imageId);
                            values.put("flavor_id", flvItem.flavorId);

                            CreateTask createTask = new CreateTask(values);
                            createTask.execute();
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
