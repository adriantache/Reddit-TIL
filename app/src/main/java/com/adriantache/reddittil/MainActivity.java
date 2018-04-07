package com.adriantache.reddittil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;

import com.adriantache.reddittil.adapter.TILAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //private RecyclerView recyclerView;
    private ListView listView;
    private ArrayList<TILPost> TILArray = new ArrayList<>();
    private TILAdapter tilAdapter;
    private static final String REDDIT_TIL_URL = "https://www.reddit.com/r/todayilearned/new.json";
    private String JSONString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //recyclerView = findViewById(R.id.recycler_view);
        listView = findViewById(R.id.list_view);

        //todo implement onItemClickListener

        new TILAsyncTask().execute();
    }

    private void createAdapter() {
        tilAdapter = new TILAdapter(this, TILArray);
        if (!TextUtils.isEmpty(JSONString)) extractJSON(JSONString);
        listView.setAdapter(tilAdapter);
    }

    //todo replace ListView with RecyclerView

    //todo implement drag down to refresh

    //todo add Firebase database integration to store TIL posts

    //OKHTTP implementation
    private String fetchJSON(String url) throws IOException, NullPointerException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    //todo JSON parsing code
    private void extractJSON(String JSONString) {
        try {
            JSONObject root = new JSONObject(JSONString);
            JSONObject data = root.getJSONObject("data");
            JSONArray children = data.getJSONArray("children");

            for (int i = 0; i < children.length(); i++) {
                JSONObject child = children.getJSONObject(i);
                JSONObject data2 = child.getJSONObject("data");
                TILArray.add(new TILPost(data2.getString("id"), data2.getString("title"), data2.getString("url"), data2.getLong("created_utc")));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Cannot parse JSON", e);
        }
    }

    //todo reference this after parsing new JSON
    private void sortArrayList() {
        Collections.sort(TILArray, new Comparator<TILPost>() {
            @Override
            public int compare(TILPost lhs, TILPost rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inverted for descending
                return lhs.getTime() > rhs.getTime() ? -1 : (lhs.getTime() < rhs.getTime()) ? 1 : 0;
            }
        });
    }

    private class TILAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONString = fetchJSON(REDDIT_TIL_URL);
            } catch (IOException e) {
                Log.e(TAG, "Cannot fetch JSON from URL", e);
            } catch (NullPointerException e) {
                Log.e(TAG, "Cannot fetch JSON from URL", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            createAdapter();
        }
    }
}
