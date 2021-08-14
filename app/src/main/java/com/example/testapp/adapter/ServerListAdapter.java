package com.example.testapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.testapp.R;

import java.util.List;

public class ServerListAdapter extends BaseAdapter {
    public static abstract class Row {}
    private List<Row> rows;

    public static final class Title extends Row {
        public final String text;

        public Title(String text) {
            this.text = text;
        }
    }

    public static final class Item extends Row {
        public final String serverId;
        public final String serverName;

        public Item(String serverName, String serverId) {
            this.serverName = serverName;
            this.serverId = serverId;
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

/*
        Switch instanceOnOff = view.findViewById(R.id.instanceOnOff);

        instanceOnOff.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Toast.makeText(context, "click switch view", Toast.LENGTH_SHORT).show();
                }
        });
*/
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
