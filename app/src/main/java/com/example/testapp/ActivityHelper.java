package com.example.testapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.testapp.bean.MessageBean;
import com.example.testapp.common.CsUncaughtExceptionHandler;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public abstract class ActivityHelper extends FragmentActivity {
    protected Context context = null;
    protected BackPressCloseController backPress = new BackPressCloseController(this);

    static protected ArrayList<WeakReference<Activity>> activityStack = new ArrayList<WeakReference<Activity>>();

    protected abstract void activityStart(android.os.Bundle savedInstanceState) throws Exception;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        context = getApplicationContext();
        context.setTheme(R.style.Theme_TestApp);

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        try {
            if (savedInstanceState != null) {

            } else {
                Intent intent = getIntent();
            }

            Thread.setDefaultUncaughtExceptionHandler(new CsUncaughtExceptionHandler(this));
            activityStart(savedInstanceState);

        } catch (Exception e) {
            Log.e("ActivityHelper", checkStr(e.getMessage()));
        }
    };

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //LogUtil.i(ForeGround");
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPress.onBackPressed();
    }

    @Override
    protected void onRestoreInstanceState(android.os.Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(android.os.Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void gotoPrevActivity(Object ...objs) {
        WeakReference<Activity> latestActivity = activityStack.get(activityStack.size() - 1);
        latestActivity.clear();

        Reference ref = new WeakReference(latestActivity);
        Object refObj = ref.get();
        if(refObj != null) {
            ref.clear();
            ref = null;
            latestActivity = null;
        }

        activityStack.remove(activityStack.size() - 1);

        finish();
        overridePendingTransition(R.anim.default_end_enter, R.anim.default_end_exit);
    }

    public void gotoNextActivity(Class<?> nextClass, Object ...objs) {
        gotoActivity(true, nextClass, objs);
    }

    private void gotoActivity(boolean isNextActivity, Class<?> nextClass, Object ...objs) {
        try {
            Intent intent = new Intent(getApplication(), nextClass);

            for (Object obj : objs) {
                if (obj instanceof ContentValues) {
                    intent.putExtra("DATA", ((ContentValues) obj));
                }
            }

            startActivity(intent);

            if (isNextActivity) {
                overridePendingTransition(R.anim.default_start_enter, R.anim.default_start_exit);
            }

            if (nextClass.equals(MainActivity.class)) {
                for (WeakReference<Activity> activity : activityStack) {
                    activity.get().finish();
                }
                finish();
                activityStack = new ArrayList<WeakReference<Activity>>();
            } else {
                activityStack.add(new WeakReference<Activity>(this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String checkStr(String str) {
        return str == null ? "" : str;
    }

    public boolean networkCheck() {
        boolean isEnableNetwork = false;
        ConnectivityManager connectManager;
        NetworkInfo mobile;
        NetworkInfo wifi;

        connectManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if ((mobile != null && mobile.isConnected()) || (wifi != null && wifi.isConnected())) {
            isEnableNetwork = true;
        }

        return isEnableNetwork;
    }

    private void createConfirmMsg(final MessageBean bean) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage(bean.getMessage());
        //ab.setIcon(android.R.drawable.ic_dialog_info);
        ab.setCancelable(false);
        if (bean.getListener() != null) {
            ab.setPositiveButton("확인", bean.getListener());
            ab.setNegativeButton("취소", bean.getListener());
        }

        ab.show();
    }

    private void createMessageMsg(final MessageBean bean) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage(bean.getMessage());
        ab.setIcon(android.R.drawable.ic_dialog_info);
        ab.setCancelable(false);
        if (bean.getListener() != null) {
            ab.setPositiveButton(bean.getPositiveText(), bean.getListener());

            if (bean.getNeutralText() != null && !bean.getNeutralText().equals("")) {
                ab.setNeutralButton(bean.getNeutralText(), bean.getListener());
            }
            if (bean.getNegativeText() != null && !bean.getNegativeText().equals("")) {
                ab.setNegativeButton(bean.getNegativeText(), bean.getListener());
            }
        }

        ab.show();
    }

    private class BackPressCloseController {
        private long backKeyPressedTime = 0;
        private Toast toast;
        private Activity activity;

        public BackPressCloseController(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                for (WeakReference<Activity> act : activityStack) {
                    act.get().finish();
                }

                activity.finish();
                toast.cancel();
            }
        }

        private void showGuide() {
            toast = Toast.makeText(activity, getString(R.string.back_press_close_message), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
