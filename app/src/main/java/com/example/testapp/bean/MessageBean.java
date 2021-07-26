package com.example.testapp.bean;

import java.io.Serializable;
import android.content.DialogInterface;

public class MessageBean implements Serializable {

	private static final long serialVersionUID = -8344741104755847817L;
	
	private String message = null;
	private DialogInterface.OnClickListener listener = null;
    private String positiveText = null;
    private String neutralText = null;
    private String negativeText = null;

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
    public String getPositiveText() { return positiveText; }
    public void setPositiveText(String positiveText) { this.positiveText = positiveText; }
    public String getNeutralText() { return neutralText; }
    public void setNeutralText(String neutralText) { this.neutralText = neutralText; }
    public String getNegativeText() { return negativeText; }
    public void setNegativeText(String negativeText) { this.negativeText = negativeText; }

	public DialogInterface.OnClickListener getListener() {
		return listener;
	}
	public void setListener(DialogInterface.OnClickListener listener) {
		this.listener = listener;
	}
}
