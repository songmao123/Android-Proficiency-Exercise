package com.sqsong.gankiosample.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sqsong.gankiosample.R;

public class WebViewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String WEB_URL = "web_url";

    private WebView webView;
    private Toolbar toolbar;

    private String mWebUrl;
    private SwipeRefreshLayout swipe_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        getIntentParams();
        initView();
        initEvents();
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            mWebUrl = intent.getStringExtra(WEB_URL);
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipe_layout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        webView = new WebView(getApplicationContext());
        swipe_layout.addView(webView);
    }

    private void initEvents() {
        if (TextUtils.isEmpty(mWebUrl)) return;

        swipe_layout.setOnRefreshListener(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setSupportZoom(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(mWebViewClient);
        webView.loadUrl(mWebUrl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            swipe_layout.setRefreshing(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mWebUrl = url;
            swipe_layout.setRefreshing(false);
            String title = view.getTitle();
            if (!TextUtils.isEmpty(title)) {
                toolbar.setTitle(title);
            }
        }
    };

    @Override
    public void onRefresh() {
        webView.loadUrl(mWebUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
           if (webView.canGoBack()) {
               webView.goBack();
               return true;
           }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        swipe_layout.removeAllViews();
        webView.destroy();
    }
}
