package com.example.testapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.testapp.R;

import java.util.List;

public class FlavorListAdapter extends BaseAdapter {
    public static abstract class Row {}
    private List<Row> rows;

    public static final class Item extends Row {
        public final String flavorName;
        public final String flavorId;


        public Item(String flavorName, String flavorId) {
            this.flavorName = flavorName;
            this.flavorId = flavorId;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_flavor_list_item, parent, false);
        }

        Item item = (Item) getItem(position);

        if(item != null) {
            TextView flavorName = view.findViewById(R.id.flavorName);
            flavorName.setText(item.flavorName);
            TextView flavorId = view.findViewById(R.id.flavorId);
            flavorId.setText(item.flavorId);
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
