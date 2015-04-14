package com.example.alex.passwordmanager;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;


public class ProfilePagerActivity extends FragmentActivity {

    //make a profile locker
    public ViewPager viewPager;
    public PagerAdapter mPagerAdapter;
    int mListPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.slider);

        //get the position to start on
        Intent mIntent = getIntent();
        this.mListPosition = mIntent.getIntExtra("listPosition", 0);
        //setup the view pager
        viewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        //set view pager position
        viewPager.setCurrentItem(this.mListPosition);
    }
    public int getPosition(){
        return this.mListPosition;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_profile) {
            int currentPosition = viewPager.getCurrentItem();
            ((Globals) this.getApplication()).deleteProfile(getProfiles().get(currentPosition));
            viewPager.setCurrentItem(currentPosition - 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public List<Profile> getProfiles(){
        return ((Globals) this.getApplication()).getProfileList();
    }
    public String getMasterPassword(){
        return ((Globals) this.getApplication()).getMasterPassword();
    }
    public void setScreenPosition(int position){
        viewPager.setCurrentItem(position);
    }
    public int getScreenPosition(){
        return viewPager.getCurrentItem();
    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ProfileEditorFragment pTemp = new ProfileEditorFragment();
            pTemp.selectedProfile = getProfiles().get(position);
            return pTemp;
        }

        @Override
        public int getCount() {
            return getProfiles().size();
        }

    }
    /**
     * A placeholder fragment containing a simple view.
     */
    /*public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Profile profile = new Profile();
            View rootView = inflater.inflate(R.layout.password_editor_display, container, false);
            profile.title = "Awesome";
            profile.url = "google.com";
            profile.username = "alexlambson";
            profile.digits = true;
            profile.length = 23;
            final EditText url = (EditText) rootView.findViewById(R.id.userInputURL);
            final TextView password = (TextView) rootView.findViewById(R.id.passwordShow);
            url.setText(profile.url);
            try {
                String passwordString = (String) profile.generate(profile.title + "2jhdjfbfndksmabaldmcshrir739ssdksjaoasjfnflaksbfk");
                password.setText(passwordString);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }

            return rootView;
        }
    }*/
}
