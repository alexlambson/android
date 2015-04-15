package com.example.alex.passwordmanager;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ProfileList extends ActionBarActivity implements AdapterView.OnItemClickListener{
    ListView mainListView;
    ProfileAdapter mProfileAdapter;
    public static final String UPDATE_INTENT = "update-the-ui-list";
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // update ui
            update();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Globals) this.getApplication()).startup();
        setContentView(R.layout.activity_profile_list);

        mainListView = (ListView) findViewById(R.id.profile_list_view);

        mProfileAdapter = new ProfileAdapter(this, R.layout.profile_list_item, this.getProfileList());
        mainListView.setAdapter(mProfileAdapter);
        mainListView.setOnItemClickListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(UPDATE_INTENT));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        if(id == R.id.add_new_profile_button){
            Profile defaultProfile = new Profile();
            this.addToProfiles(defaultProfile);
            int position = this.getProfileList().size() - 1;
            Intent profileEditorIntent = new Intent(this, ProfilePagerActivity.class);
            profileEditorIntent.putExtra("listPosition", position);
            startActivity(profileEditorIntent);
        }
        if(id == R.id.sync_profiles_button){
            ((Globals) this.getApplication()).syncProfiles();
            //Log.d("html", htmlresponse);
        }
        return super.onOptionsItemSelected(item);
    }
    public void update(){
        this.mProfileAdapter.UpdateList(this.getProfileList());
    }
    @Override
    public void onResume(){
        super.onResume();
        this.update();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent detailIntent = new Intent(this, ProfilePagerActivity.class);

        // pack away the data about the cover
        // into your Intent before you head out
        detailIntent.putExtra("listPosition", position);

        // start the next Activity using your prepared Intent
        startActivity(detailIntent);
    }
    public List<Profile> getProfileList(){
        return ((Globals) this.getApplication()).getProfileList();
    }
    public void addToProfiles(Profile profile){
        ((Globals) this.getApplication()).addProfile(profile);
    }
    public void updateProfile(Profile profile, int position){
        ((Globals) this.getApplication()).updateProfile(profile);
    }
    @Override
    protected void onPause() {
        // Unregister since the activity is about to be closed.
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
