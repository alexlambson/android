package com.example.alex.passwordmanager;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends ActionBarActivity implements View.OnClickListener {
    private Boolean mPasswordEdited = false;
    private String mPassword01, mPassword02 = "";
    private Button mSaveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.mSaveButton = (Button) findViewById(R.id.masterPasswordSave);
        this.mSaveButton.setEnabled(false);
        this.mSaveButton.setOnClickListener(this);
        EditText editText = (EditText) findViewById(R.id.settingsMasterPassword01);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                mPasswordEdited = true;
                mSaveButton.setEnabled(true);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.masterPasswordSave:
                savePassword();
                break;
        }
    }

    private void savePassword() {
        if(!mPasswordEdited){
            return;
        }
        CharSequence text = "Passwords do not match";
        updatePasswords();
        Log.d("Passwords", mPassword01 + " " + mPassword02);
        if(passwordsMatch()){
            ((Globals) this.getApplication()).setMasterPassword(mPassword01);
            text = "Password set to: " + mPassword01;
            Log.d("Passwords", "Passwords matched " + ((Globals) this.getApplication()).getMasterPassword());
        }
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private boolean passwordsMatch() {
        return mPassword01.equals(mPassword02);
    }

    private void updatePasswords() {
        EditText pass1, pass2;
        pass1 = (EditText) this.findViewById(R.id.settingsMasterPassword01);
        pass2 = (EditText) this.findViewById(R.id.settingsMasterPassword02);
        mPassword01 = pass1.getText().toString();
        mPassword02 = pass2.getText().toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
