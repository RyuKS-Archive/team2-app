package com.example.testapp.common;

import java.lang.Thread.UncaughtExceptionHandler;
import com.example.testapp.ActivityHelper;

public class CsUncaughtExceptionHandler implements UncaughtExceptionHandler {
	final UncaughtExceptionHandler savedUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	
	private volatile boolean mCrashing = false;
	
	private ActivityHelper activityHelper;
	public CsUncaughtExceptionHandler(ActivityHelper activityHelper) {
		this.activityHelper = activityHelper;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		try {
			if (!mCrashing) {
				mCrashing = true;
			}
		} finally {
			savedUncaughtExceptionHandler.uncaughtException(thread, exception);
		}
	}
}