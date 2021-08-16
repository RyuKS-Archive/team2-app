package com.example.testapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.testapp.R;

import java.util.List;

public class ImageListAdapter extends BaseAdapter {
    public static abstract class Row {}
    private List<Row> rows;

    public static final class Item extends Row {
        public final String imageName;
        public final String imageId;

        public Item(String imageName, String imageId) {
            this.imageName = imageName;
            this.imageId = imageId;
        }
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public List<Row> getRows() {
        return rows;
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
            view = inflater.inflate(R.layout.listview_image_list_item, parent, false);
        }

        Item item = (Item) getItem(position);

        if(item != null) {
            TextView imageName = view.findViewById(R.id.imageName);
            imageName.setText(item.imageName);
            TextView imageId = view.findViewById(R.id.imageId);
            imageId.setText(item.imageId);
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
