package com.zyvo.mylife;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView web;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setSupportZoom(false);
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new ThemeBridge(), "NativeTheme");

        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }

    public class ThemeBridge {
        @JavascriptInterface
        public void apply(final String color, final boolean light) {
            runOnUiThread(() -> {
                try {
                    getWindow().setStatusBarColor(Color.parseColor(color));
                } catch (Exception ignored) {
                }
                try {
                    getWindow().setNavigationBarColor(Color.parseColor(color));
                } catch (Exception ignored) {
                }
                int flags = light
                        ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                        : 0;
                getWindow().getDecorView().setSystemUiVisibility(flags);
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
