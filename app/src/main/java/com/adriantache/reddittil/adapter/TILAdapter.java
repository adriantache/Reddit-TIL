package com.adriantache.reddittil.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.adriantache.reddittil.R;
import com.adriantache.reddittil.TILPost;

import java.util.List;

public class TILAdapter extends ArrayAdapter<TILPost> {
    public TILAdapter(@NonNull Context context, @NonNull List<TILPost> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TILPost tilPost = getItem(position);

        if (tilPost != null) {
            holder.textView.setText(tilPost.getTitle());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}