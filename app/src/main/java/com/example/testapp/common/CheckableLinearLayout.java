package com.example.testapp.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.example.testapp.R;

public class CheckableLinearLayout extends LinearLayout implements Checkable {
    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isChecked() {
        CheckBox cb = findViewById(R.id.enable) ;

        return cb.isChecked() ;
    }

    @Override
    public void toggle() {
        CheckBox cb = findViewById(R.id.enable) ;

        setChecked(cb.isChecked() ? false : true) ;
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = findViewById(R.id.enable) ;

        if (cb.isChecked() != checked) {
            cb.setChecked(checked) ;
        }
    }
}