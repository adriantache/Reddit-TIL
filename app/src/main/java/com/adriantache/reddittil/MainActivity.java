package com.adriantache.reddittil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "MainActivity";
    //private RecyclerView recyclerView;
    private ListView listView;
    private ArrayList<TILPost> TILArray = new ArrayList<>();
    private TILAdapter tilAdapter;
    private static final String REDDIT_TIL_URL =
            "https://www.reddit.com/r/todayilearned/new.json?limit=100";
    private String JSONString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //recyclerView = findViewById(R.id.recycler_view);
        listView = findViewById(R.id.list_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

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
//        OkHttpClient client = new OkHttpClient().connectTimeout(10, TimeUnit.SECONDS)
//                .writeTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS).build();

        //override timeouts to ensure receiving full JSON
        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.connectTimeout(15, TimeUnit.SECONDS);
        b.readTimeout(15, TimeUnit.SECONDS);
        b.writeTimeout(15, TimeUnit.SECONDS);

        OkHttpClient client = b.build();

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

    @Override
    public void onRefresh() {
        TILArray = new ArrayList<>();
        new TILAsyncTask().execute();
    }

    private class TILAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            swipeRefreshLayout.setRefreshing(true);
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
            swipeRefreshLayout.setRefreshing(false);
            createAdapter();
        }
    }
}
