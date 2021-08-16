package com.example.testapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.example.testapp.R;
import com.example.testapp.common.CallBackListener;

import java.util.List;

public class ServerListAdapter extends BaseAdapter {
    public static abstract class Row {}
    private List<Row> rows;
    private CallBackListener mCallback;
    private Context context;
    private boolean cancelFlg = false;

    public ServerListAdapter(CallBackListener mCallback) {
        this.context = (Context) mCallback;
        this.mCallback = mCallback;
    }

    public static final class Title extends Row {
        public final String text;

        public Title(String text) {
            this.text = text;
        }
    }

    public static final class Item extends Row {
        public final String serverId;
        public final String serverName;
        public final String tenantId;
        public boolean isRunning;

        public Item(String serverName, String serverId, String tenantId, boolean isRunning) {
            this.serverName = serverName;
            this.serverId = serverId;
            this.isRunning = isRunning;
            this.tenantId = tenantId;
        }
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    @Override
    public Row getItem(int position) {
        return rows.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Title) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (getItemViewType(position) == 0) {
            if(view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listview_server_list_item, parent, false);
            }

            Item item = (Item) getItem(position);

            if(item != null) {
                TextView serverName = view.findViewById(R.id.serverName);
                serverName.setText(item.serverName);
                TextView serverId = view.findViewById(R.id.serverId);
                serverId.setText(item.serverId);
                TextView tenantId = view.findViewById(R.id.tenantId);
                tenantId.setText(item.tenantId);
                TextView serverDeleteTxt = view.findViewById(R.id.serverDelete);
                //serverDeleteTxt.setTextColor(Color.parseColor("#000000"));

                Switch instanceOnOff = view.findViewById(R.id.instanceOnOff);

                if (item.isRunning) {
                    instanceOnOff.setChecked(true);
                } else {
                    instanceOnOff.setChecked(false);
                }

                serverName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.showUsageStatistics(serverName.getText().toString(), tenantId.getText().toString());
                    }
                });

                serverId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.showUsageStatistics(serverName.getText().toString(), tenantId.getText().toString());
                    }
                });

                instanceOnOff.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(context);
                            ab.setMessage(serverName.getText() + context.getString(R.string.server_stop_alert_msg));
                            ab.setIcon(android.R.drawable.ic_dialog_alert);
                            ab.setCancelable(false);
                            ab.setPositiveButton(R.string.server_stop_btn_msg, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mCallback.changeServerStatus(isChecked, serverId.getText().toString());
                                }
                            });
                            ab.setNegativeButton(R.string.description_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cancelFlg = true;
                                    instanceOnOff.setChecked(!isChecked);
                                }
                            });
                            ab.show();

                        } else {
                            if (cancelFlg) {
                                cancelFlg = false;
                            } else {
                                mCallback.changeServerStatus(isChecked, serverId.getText().toString());
                            }
                        }
                    }
                });

                serverDeleteTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        serverDeleteTxt.setTextColor(Color.parseColor("#DF7401"));

                        AlertDialog.Builder ab = new AlertDialog.Builder(context);
                        ab.setMessage(serverName.getText() + context.getString(R.string.server_delete_alert_msg));
                        ab.setIcon(android.R.drawable.ic_dialog_alert);
                        ab.setCancelable(false);
                        ab.setPositiveButton(R.string.server_delete_btn_msg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mCallback.deleteServer(serverId.getText().toString());
                            }
                        });
                        ab.setNegativeButton(R.string.description_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                serverDeleteTxt.setTextColor(Color.parseColor("#000000"));
                            }
                        });
                        ab.show();

                    }
                });
            }
        } else {
            if(view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listview_server_list_title, parent, false);
            }

            Title title = (Title) getItem(position);

            if(title != null) {
                TextView resultTitle = view.findViewById(R.id.resultTitle);
                resultTitle.setText(title.text);
            }
        }

        return view;
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
