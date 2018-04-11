package com.adriantache.reddittil;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.adriantache.reddittil.adapter.TILAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<List<TILPost>> {

    static final String TAG = "MainActivity";
    private static final String REDDIT_TIL_URL =
            "https://www.reddit.com/r/todayilearned/new.json?limit=100";
    private String subreddit;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private RecyclerView recyclerView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //recyclerView = findViewById(R.id.recycler_view);
        listView = findViewById(R.id.list_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        //open URL on click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TILPost tilPost = (TILPost) adapterView.getItemAtPosition(i);
                Uri webPage = Uri.parse(tilPost.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        //start task to fetch data from Reddit
        swipeRefreshLayout.setRefreshing(true);
        getSupportLoaderManager().initLoader(1, null, this);
    }

    //create the adapter to populate the ListView; if it exists, empty it first
    private void createAdapter(List<TILPost> TILArray) {
        swipeRefreshLayout.setRefreshing(false);

        TILAdapter tilAdapter = new TILAdapter(this, TILArray);
        listView.setAdapter(tilAdapter);
    }

    //todo replace ListView with RecyclerView
    //todo add Firebase database integration to store TIL posts

    //EditText to pick subreddit
    public void fetch(View v) {
        EditText editText = findViewById(R.id.edit_text);
        subreddit = editText.getText().toString();

        swipeRefreshLayout.setRefreshing(true);

        getSupportLoaderManager().restartLoader(1,null,this);
    }


    //OKHTTP implementation
    String fetchJSON(String url) throws IOException, NullPointerException {
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

    //todo reference this after parsing new JSON (after implementing storage);
    //todo after this remove duplicates
    private void sortArrayList(ArrayList<TILPost> TILArray) {
        Collections.sort(TILArray, new Comparator<TILPost>() {
            @Override
            public int compare(TILPost lhs, TILPost rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inverted for descending
                if (Build.VERSION.SDK_INT >= 19)
                    return Long.compare(lhs.getTime(), rhs.getTime()) * -1;
                else
                    return lhs.getTime() > rhs.getTime() ? -1 : (lhs.getTime() < rhs.getTime()) ? 1 : 0;
            }
        });
    }

    //action on refresh of SwipeRefreshLayout
    @Override
    public void onRefresh() {
        getLoaderManager().destroyLoader(1);
        swipeRefreshLayout.setRefreshing(true);
        getSupportLoaderManager().initLoader(1, null, this).forceLoad();
    }

    @Override
    @NonNull
    public Loader<List<TILPost>> onCreateLoader(int id, Bundle args) {
        if (TextUtils.isEmpty(subreddit)) {
            return new TILLoader(this, MainActivity.this, REDDIT_TIL_URL);
        } else {
            return new TILLoader(this, MainActivity.this,
                    "https://www.reddit.com/r/" + subreddit + "/new.json?limit=100");
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<TILPost>> loader, List<TILPost> data) {
        createAdapter(data);
    }


    @Override
    public void onLoaderReset(@NonNull Loader<List<TILPost>> loader) {
        createAdapter(new ArrayList<TILPost>());
    }

}