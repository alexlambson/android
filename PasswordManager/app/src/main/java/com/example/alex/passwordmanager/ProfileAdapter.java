package com.example.alex.passwordmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Alex on 3/18/2015.
 */
public class ProfileAdapter extends ArrayAdapter<Profile> {

    List<Profile> mProfileList = new ArrayList<Profile>();

    public ProfileAdapter(Context context, int resource, List<Profile> profiles) {
        super(context, resource, profiles);
        this.mProfileList = profiles;
    }
    private class ViewHolder {
        public LinearLayout listItemView;
        public TextView usernameView;
        public TextView websiteView;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.profile_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.listItemView = (LinearLayout) convertView.findViewById(R.id.profile_list_relative_layout);
            viewHolder.usernameView = (TextView) convertView.findViewById(R.id.list_username);
            viewHolder.websiteView = (TextView) convertView.findViewById(R.id.list_website);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Profile item = this.mProfileList.get(position);
        if (item != null) {
            viewHolder.usernameView.setText(item.username);
            viewHolder.websiteView.setText(item.url);
        }

        return convertView;

    }
    @Override
    public int getCount(){
        return this.mProfileList.size();
    }
    @Override
    public Profile getItem(int position){
        return this.mProfileList.get(position);
    }
    @Override
    public long getItemId(int position){
        return this.getItem(position).uuid.getLeastSignificantBits();
    }
    public void UpdateList(List<Profile> newProfiles){
        this.mProfileList = newProfiles;
        this.notifyDataSetChanged();
    }
}
/*
public class ProfileAdapter2 extends ArrayAdapter<Profile> {
    List<Profile> list = new ArrayList<>();

    private static class ViewHolder {
        private TextView itemView;
    }

    public ProfileAdapter(Context context, int textViewResourceId, List<Profile> items) {
        super(context, textViewResourceId, items);
        this.list = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.profile_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(R.id.itemView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Profile item = getItem(position);
        if (item != null) {
            viewHolder.itemView.setText(item.title);
        }

        return convertView;

    }
}
*/
