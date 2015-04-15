package com.example.alex.passwordmanager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

/**
 * Created by Alex on 3/22/2015.
 */
public class SqlManager extends SQLiteOpenHelper {

    private static final String DB_NAME = "profiles.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_PROFILES = "profiles";

    private static final String UUIDCOL = "uuid";
    private static final String DeletedFlag = "deleted";
    private static final String GenerationCOL = "generation";
    private static final String TitleCol = "title";
    private static final String UrlCol = "url";
    private static final String UsernameCol = "username";
    private static final String LengthCol = "length";
    private static final String LowerCol = "lower";
    private static final String UpperCol = "upper";
    private static final String DigitCol = "digit";
    private static final String PunctuationCol = "punctuation";
    private static final String SpacesCol = "spaces";
    private static final String IncludeCol = "include";
    private static final String ExcludeCol = "exclude";
    private static final String ModifiedAt = "modified_at";

    public SqlManager (Context context){
        super(context, DB_NAME, null, VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table profiles (_id integer primary key autoincrement," +
                "deleted integer,"+
                "uuid varchar(40)," +
                "generation integer,"+
                "title varchar(100),"+
                "url varchar(256),"+
                "username varchar(256),"+
                "length integer,"+
                "modified_at varchar(20)," +
                "lower integer, upper integer, digit integer, punctuation integer, spaces integer,"+
                "include varchar(100), exclude varchar(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private ContentValues addColumns(Profile profile, boolean deleted) {
        ContentValues newContent = new ContentValues();
        newContent.put(UUIDCOL, profile.uuid.toString());
        newContent.put(DeletedFlag, deleted);
        newContent.put(GenerationCOL, profile.generation);
        newContent.put(TitleCol, profile.title);
        newContent.put(UrlCol, profile.url);
        newContent.put(UsernameCol, profile.username);
        newContent.put(LengthCol, profile.length);
        newContent.put(LowerCol, profile.lower);
        newContent.put(UpperCol, profile.upper);
        newContent.put(DigitCol, profile.digits);
        newContent.put(PunctuationCol, profile.punctuation);
        newContent.put(SpacesCol, profile.spaces);
        newContent.put(IncludeCol, profile.include);
        newContent.put(ExcludeCol, profile.exclude);
        newContent.put(ModifiedAt, profile.modified);
        return newContent;
    }
    public long insertProfile(Profile profile) {
        ContentValues newContent = addColumns(profile, false);
        long success = getWritableDatabase().insert(TABLE_PROFILES, null, newContent);
        getWritableDatabase().close();
        return success;
    }
    public long updateProfile(Profile profile) {
        ContentValues newContent = addColumns(profile, false);
        long success = getWritableDatabase().update(TABLE_PROFILES, newContent, UUIDCOL+"='"+profile.uuid.toString()+"'", null);
        getWritableDatabase().close();
        //Log.d("Database generation: ", profile.generation + " " + profile.modified);
        return success;
    }
    public long deleteProfile(Profile profile) {
        System.out.println("....................deleted Profile "+profile.title);
        ContentValues newContent = addColumns(profile, true);
        long success = getWritableDatabase().update(TABLE_PROFILES, newContent, UUIDCOL+"='"+profile.uuid.toString()+"'", null);
        getWritableDatabase().close();
        return success;
    }

    public ProfileCursor getProfile() {
        // Select statement grabs all none flagged deleted profiles
        Cursor wrapper = getReadableDatabase().rawQuery("select * from profiles where deleted = 0", null);
        ProfileCursor profileCursor = new ProfileCursor(wrapper);
        return profileCursor;
    }

    public static class ProfileCursor extends CursorWrapper {

        public ProfileCursor(Cursor c) {
            super(c);
        }

        public Profile getProfile() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            Profile profile = new Profile();
            profile.uuid = UUID.fromString(getString(getColumnIndex(UUIDCOL)));
            profile.generation = getInt(getColumnIndex(GenerationCOL));
            profile.title = getString(getColumnIndex(TitleCol));
            profile.url = getString(getColumnIndex(UrlCol));
            profile.username = getString(getColumnIndex(UsernameCol));
            profile.length = getInt(getColumnIndex(LengthCol));
            profile.modified = getString(getColumnIndex(ModifiedAt));
            profile.lower = getInt(getColumnIndex(LowerCol)) > 0;
            profile.upper = getInt(getColumnIndex(UpperCol)) > 0;
            profile.digits = getInt(getColumnIndex(DigitCol)) > 0;
            profile.punctuation = getInt(getColumnIndex(PunctuationCol)) > 0;
            profile.spaces = getInt(getColumnIndex(SpacesCol)) > 0;
            profile.include = getString(getColumnIndex(IncludeCol));
            profile.exclude = getString(getColumnIndex(ExcludeCol));
            return profile;
        }
    }
}
