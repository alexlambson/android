package com.example.alex.passwordmanager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Alex on 3/2/2015.
 */
public class ProfileEditorFragment extends Fragment implements View.OnClickListener {
    public Profile selectedProfile;
    View view;
    EditText username, url, title, include, exclude;
    Switch upper, lower, digit, symbol, spaces;
    SeekBar passwordLength;
    TextView showPassword;
    public ProfileEditorFragment() {
        /*selectedProfile = new Profile();
        selectedProfile.title = "Awesome";
        selectedProfile.url = "google.com";
        selectedProfile.username = "alexlambson";
        selectedProfile.digits = true;
        selectedProfile.length = 23;
        selectedProfile.include = "Jews";*/
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.password_editor_display, container, false);
        Button b = (Button) view.findViewById(R.id.passwordGenerateButton);
        Button b2 = (Button) view.findViewById(R.id.deleteProfile);
        b2.setOnClickListener(this);
        b.setOnClickListener(this);
        this.getViews();
        this.setViewsFromProfile();

        return view;
    }
    private void setViewsFromProfile(){
        url.setText(selectedProfile.url);
        title.setText(selectedProfile.title);
        username.setText(selectedProfile.username);
        include.setText(selectedProfile.include);
        passwordLength.setProgress(selectedProfile.length);
        exclude.setText(selectedProfile.exclude);
        //disable for now because slow

        try {
            showPassword.setText(selectedProfile.generate(getMasterPassword()));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        upper.setChecked(selectedProfile.upper);
        lower.setChecked(selectedProfile.lower);
        digit.setChecked(selectedProfile.digits);
        symbol.setChecked(selectedProfile.punctuation);
        spaces.setChecked(selectedProfile.spaces);
    }
    private void updateProfile(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String currentDate = dateFormat.format(calendar.getTime());
        if(selectedProfile.modified != null) {
            Log.d("Profile old time: ", selectedProfile.modified);
        }
        selectedProfile.url = url.getText().toString();
        selectedProfile.title = title.getText().toString();
        selectedProfile.username = username.getText().toString();
        selectedProfile.include = include.getText().toString();
        selectedProfile.exclude = exclude.getText().toString();
        selectedProfile.length = passwordLength.getProgress();
        selectedProfile.upper = upper.isChecked();
        selectedProfile.lower = lower.isChecked();
        selectedProfile.digits = digit.isChecked();
        selectedProfile.punctuation = symbol.isChecked();
        selectedProfile.spaces = spaces.isChecked();
        selectedProfile.modified = currentDate;
        Log.d("Profile update time: ", currentDate);
        selectedProfile.generation++;
    }
    private void getViews(){
        url = (EditText) view.findViewById(R.id.userInputURL);
        title = (EditText) view.findViewById(R.id.userInputTitle);
        username = (EditText) view.findViewById(R.id.userInputUsername);
        include = (EditText) view.findViewById(R.id.userInputInclude);
        exclude = (EditText) view.findViewById(R.id.userInputExclude);
        passwordLength = (SeekBar) view.findViewById(R.id.passwordLength);
        showPassword = (TextView) view.findViewById(R.id.passwordShow);
        upper = (Switch) view.findViewById(R.id.userInputUpper);
        lower = (Switch) view.findViewById(R.id.userInputLower);
        digit = (Switch) view.findViewById(R.id.userInputDigit);
        symbol = (Switch) view.findViewById(R.id.userInputSymbol);
        spaces = (Switch) view.findViewById(R.id.userInputSpaces);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.passwordGenerateButton:
                handleGenerateButton();
                break;
            case R.id.deleteProfile:
                handleDelete();
                break;
        }
    }
    private String getMasterPassword(){
        ProfilePagerActivity profilePagerActivity = (ProfilePagerActivity) getActivity();
        return profilePagerActivity.getMasterPassword();
    }
    public void handleGenerateButton(){
        this.updateProfile();
        this.setViewsFromProfile();
        ((Globals) this.getActivity().getApplication()).updateProfile(this.selectedProfile);
    }
    public void handleDelete(){
        ProfilePagerActivity profilePagerActivity = (ProfilePagerActivity) getActivity();
        int position = profilePagerActivity.getScreenPosition() -1;
        ((Globals) this.getActivity().getApplication()).deleteProfile(this.selectedProfile);
        profilePagerActivity.mPagerAdapter.notifyDataSetChanged();
        if(position >= 0) {
            profilePagerActivity.setScreenPosition(position);
        } else {
            this.getActivity().onBackPressed();
        }

    }
}