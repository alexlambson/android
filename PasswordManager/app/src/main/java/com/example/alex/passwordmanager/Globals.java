package com.example.alex.passwordmanager;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Created by Alex on 3/19/2015.
 */
public class Globals extends Application {

    private ArrayList<Profile> mProfileList = new ArrayList<Profile>();
    private String masterPassword = "default";
    private SqlManager sqlManager;
    private SqlManager.ProfileCursor cursor;

    public void addProfile(Profile profile){
        sqlManager.insertProfile(profile);
        sqlManager.close();
    }
    public void startup(){
        mProfileList = new ArrayList<Profile>();
        sqlManager = new SqlManager(this);
        cursor = sqlManager.getProfile();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            mProfileList.add(cursor.getProfile());
            cursor.moveToNext();
        }
        cursor.close();
        sqlManager.close();
    }
    public Profile getProfile(Integer i){
        this.startup();
        return this.mProfileList.get(i);
    }
    public ArrayList<Profile> getProfileList(){
        this.startup();
        return mProfileList;
    }
    /*public void setProfileList(ArrayList<Profile> newList){
        this.mProfileList = newList;
    }*/
    public String getMasterPassword(){
        return masterPassword;
    }
    public void setMasterPassword(String password){
        this.masterPassword = password;
    }
    public void updateProfile(Profile profile){
        sqlManager.updateProfile(profile);
    }
    public void deleteProfile(Profile profile){
        sqlManager.deleteProfile(profile);
    }
    public void syncProfiles(){
        this.startup();
        new SyncProfiles().execute();
    }
    private void finishSync(String string){
        if(!string.equals("")){
            Log.d("html finished", string);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(string);
                setLastSync(jsonObject.getString("previous_sync_at"));
                this.markComplete();
            } catch (JSONException e) {
                e.printStackTrace();
                this.setLastModified("");
                this.setLastSync("");
                return;
            }
        } else {
            Log.d("html", "No response");
        }
    }
    private void markComplete(){
        //set modifies to null
        for(int i = 0; i < this.mProfileList.size(); i++){
            Profile profile = this.mProfileList.get(i);
            if(profile.modified == null){
                continue;
            }
            profile.modified = null;
            this.updateProfile(profile);
        }
    }
    private JSONObject makeJSONRequest(String currentDate) throws Exception{
        Log.d("html", "in json");
        String greatestDate = "";
        JSONObject reqValue = new JSONObject();
        JSONArray profilesjson = new JSONArray();

        reqValue.put("name", "alex@gmail.org");
        reqValue.put("verify", this.verifyPassword());
        for(int i = 0; i < this.mProfileList.size(); i++){
            Profile profile = this.mProfileList.get(i);
            if(profile.modified == null){
                continue;
            }
            JSONObject temp = new JSONObject();
            temp.put("uuid", profile.uuid);
            temp.put("scheme", profile.scheme);
            temp.put("generation", profile.generation);
            temp.put("name", profile.title);
            temp.put("url", profile.url);
            temp.put("username", profile.username);
            temp.put("length", profile.length);
            temp.put("lower", profile.lower);
            temp.put("upper", profile.upper);
            temp.put("digits", profile.digits);
            temp.put("punctuation", profile.punctuation);
            temp.put("spaces", profile.spaces);
            temp.put("include", profile.include);
            temp.put("exclude", profile.exclude);
            temp.put("modified_at", profile.modified);
            //temp.put("name", "alex@gmail.com");
            Log.d("Date: ", profile.modified);
            Log.d("compare", String.valueOf(greatestDate.compareTo(profile.modified)));
            if(greatestDate.equals("") || greatestDate.compareTo(profile.modified) < 0){
                greatestDate = profile.modified;
                Log.d("Greatest date set to: ", profile.modified);
            }
            profilesjson.put(temp);
        }
        reqValue.put("profiles", profilesjson);
        reqValue.put("synced_at", this.getCurrentDateString());
        if(!this.getLastSync().equals("")){
            reqValue.put("previous_sync_at", this.getLastSync());
            Log.d("Previous sync set to: ", this.getLastSync());
        }
        if(this.getLastModified().equals("") || this.getLastModified().compareToIgnoreCase(greatestDate) < 0){
            reqValue.put("modified_at", greatestDate);
            setLastModified(greatestDate);
            Log.d("Last Modified set to: ", greatestDate);
        }
        Log.d("html", "json 2 " + reqValue.toString());
        return reqValue;
    }
    private void setLastSync(String sync){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("alex'snotsosecret", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("last_sync", sync);
        editor.commit();
        Log.d("last sync", "Set - " + sync);
    }
    private String getLastSync(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("alex'snotsosecret", getApplicationContext().MODE_PRIVATE);
        String lastSync = sharedPref.getString("last_sync", "");
        Log.d("last sync", "Get - " + lastSync);
        return lastSync;
    }
    private void setLastModified(String sync){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("alex'snotsosecret", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("last_modified", sync);
        editor.commit();
        Log.d("last modified", "Set - " + sync);
    }
    private String getLastModified(){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("alex'snotsosecret", getApplicationContext().MODE_PRIVATE);
        String lastSync = sharedPref.getString("last_modified", "");
        Log.d("last modified", "Get - " + lastSync);
        return lastSync;
    }
    private String getCurrentDateString(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String currentDate = dateFormat.format(calendar.getTime());
        return currentDate;
    }
    private String verifyPassword(){
        Profile profile = new Profile();
        profile.upper = false;
        profile.punctuation = false;
        profile.digits = false;
        profile.length = 4;
        profile.generation = 0;
        profile.username = "verify";
        try {
            return profile.generate(this.getMasterPassword());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return "failed";
    }
    private class SyncProfiles extends AsyncTask<Void, Void, String>{

        //profiles, last sync,
        @Override
        protected String doInBackground(Void... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost req = new HttpPost("https://letmein-app.appspot.com/api/v1noauth/sync");
            req.addHeader("Accept", "application/json;charset=UTF-8");
            req.addHeader("content-type", "application/json;charset=UTF-8");
            StringEntity se = null;
            String responseText = "";
            try {
                JSONObject jsonObject = makeJSONRequest(getCurrentDateString());
                //Log.d("html", "made it past JSON " + jsonObject.toString());
                se = new StringEntity(jsonObject.toString());
                req.setEntity(se);
                HttpResponse httpResponse = httpClient.execute(req);
                responseText = EntityUtils.toString(httpResponse.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
                //backout so we dont set last modified
            }
            return responseText;
        }
        @Override
        protected void onPostExecute(String jsonstring){
            finishSync(jsonstring);
        }
    }
}
