package com.example.testapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        public final String serverName;
        public final String serverId;

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
                view = (LinearLayout) inflater.inflate(R.layout.listview_server_list_item, parent, false);
            }

            Item item = (Item) getItem(position);

            if(item != null) {
                TextView dtcCode = view.findViewById(R.id.serverName);
                dtcCode.setText(item.serverName);
                TextView dtcMessage = view.findViewById(R.id.serverId);
                dtcMessage.setText(item.serverId);
            }
        } else {
            if(view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (LinearLayout) inflater.inflate(R.layout.listview_server_list_title, parent, false);
            }

            Title title = (Title) getItem(position);

            if(title != null) {
                TextView textView = (TextView) view.findViewById(R.id.resultTitle);
                textView.setText(title.text);
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
