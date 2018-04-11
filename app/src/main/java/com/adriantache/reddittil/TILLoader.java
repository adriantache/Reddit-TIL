package com.adriantache.reddittil;

import android.content.Context;
import android.database.ContentObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TILLoader extends AsyncTaskLoader<List<TILPost>> {
    private MainActivity mainActivity;
    private String url;

    TILLoader(MainActivity mainActivity, @NonNull Context context, String url) {
        super(context);
        this.mainActivity = mainActivity;
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public List<TILPost> loadInBackground() {
        String JSONString = "";

        Log.i("XXXXXXXXXXX"
                , "loadInBackground: "+url);

        //get JSON String
        try {
            if (!TextUtils.isEmpty(url)) JSONString = mainActivity.fetchJSON(url);
        } catch (IOException e) {
            Log.e(MainActivity.TAG, "Cannot fetch JSON from URL", e);
        } catch (NullPointerException e) {
            Log.e(MainActivity.TAG, "Cannot fetch JSON from URL", e);
        }

        List<TILPost> TILArray = new ArrayList<>();

        //parse JSON String
        try {
            JSONObject root = new JSONObject(JSONString);
            JSONObject data = root.getJSONObject("data");
            JSONArray children = data.getJSONArray("children");

            for (int i = 0; i < children.length(); i++) {
                JSONObject child = children.getJSONObject(i);
                JSONObject data2 = child.getJSONObject("data");
                TILArray.add(new TILPost(data2.getString("id"),
                        data2.getString("title"), data2.getString("url"),
                        data2.getLong("created_utc")));
            }
        } catch (JSONException e) {
            Log.e(MainActivity.TAG, "Cannot parse JSON", e);
        }

        return TILArray;
    }
}
