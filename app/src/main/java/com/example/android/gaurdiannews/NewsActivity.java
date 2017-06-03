package com.example.android.gaurdiannews;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>>, RecyclerAdapter.ItemClickCallback,
        SwipeRefreshLayout.OnRefreshListener {

    private static final int NEWS_LOADER_ID = 1;
    public static final String GUARDIAN_API_URL =
            "http://content.guardianapis.com/search?order-by=newest&section=technology&q=Android&api-key=test";

    private RecyclerView mRecyclerView;
    private RecyclerAdapter adapter;

    private View loadingIndicator;
    private TextView stateTextView;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(false);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        fetchNews();
                                    }
                                }
        );

        loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.VISIBLE);

        stateTextView = (TextView) findViewById(R.id.empty_view);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecyclerAdapter(new ArrayList<News>(), this);
        mRecyclerView.setAdapter(adapter);

        adapter.setItemClickCallback(this);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        fetchNews();
    }

    private void fetchNews() {
        mRecyclerView.setVisibility(View.GONE);
        adapter.clear();

        android.app.LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, GUARDIAN_API_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsData) {
        if (newsData != null && !newsData.isEmpty()) {
            adapter.addAll(newsData);
            mRecyclerView.setVisibility(View.VISIBLE);
            loadingIndicator.setVisibility(View.GONE);
            stateTextView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.GONE);
            stateTextView.setVisibility(View.VISIBLE);
            stateTextView.setText(R.string.error);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        adapter.clear();
        mRecyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
        stateTextView.setVisibility(View.GONE);
    }

    private static class NewsLoader extends AsyncTaskLoader<List<News>> {

        private final String mUrl;

        NewsLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<News> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            return QueryUtils.fetchNewsData(mUrl);
        }
    }

    @Override
    public void onItemClick(int p) {
        News currentNews = adapter.getItem(p);
        Uri newsUri = Uri.parse(currentNews.getUrl());
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
        startActivity(websiteIntent);
    }
}
