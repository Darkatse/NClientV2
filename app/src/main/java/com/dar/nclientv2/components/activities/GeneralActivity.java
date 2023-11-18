package com.dar.nclientv2.components.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dar.nclientv2.R;
import com.dar.nclientv2.components.views.CFTokenView;
import com.dar.nclientv2.settings.Global;

import java.lang.ref.WeakReference;

public abstract class GeneralActivity extends AppCompatActivity {
    private boolean isFastScrollerApplied = false;
    private static WeakReference<GeneralActivity> lastActivity;
    private CFTokenView tokenView = null;

    public static @Nullable
    CFTokenView getLastCFView() {
        if (lastActivity == null) return null;
        GeneralActivity activity = lastActivity.get();
        if (activity != null) {
            activity.runOnUiThread(activity::inflateWebView);
            return activity.tokenView;
        }
        return null;
    }

    private void inflateWebView() {
        if (tokenView == null) {
            Toast.makeText(this, R.string.fetching_cloudflare_token, Toast.LENGTH_SHORT).show();
            ViewGroup rootView= (ViewGroup) findViewById(android.R.id.content).getRootView();
            ViewGroup v= (ViewGroup) LayoutInflater.from(this).inflate(R.layout.cftoken_layout,rootView,false);
            tokenView = new CFTokenView(v);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tokenView.setVisibility(View.GONE);
            this.addContentView(v, params);
        }
    }

    @Override
    protected void onPause() {
        if (Global.hideMultitask())
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /***************** Edge-to-Edge Setting Begin **************************/
        // Get the window of the activity
        Window window = getWindow();

        // Request for full screen layout

        // SYSTEM_UI_FLAG_LAYOUT_STABLE
        //*** Tells the system that the window wishes the content to
        //*** be laid out at the most extreme scenario. See the docs for
        //*** more information on the specifics
        //SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        //*** Tells the system that the window wishes the content to
        //*** be laid out as if the navigation bar was hidden
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(Color.TRANSPARENT);

            // Get the height of the status bar
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // Add a padding to the top of the root view
            View rootView = findViewById(android.R.id.content);
            rootView.setPadding(0, statusBarHeight, 0, 0);
        }
        /***************** Edge-to-Edge Setting End **************************/

        Global.initActivity(this);
    }

    @Override
    protected void onResume() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onResume();
        lastActivity = new WeakReference<>(this);
        if (!isFastScrollerApplied) {
            isFastScrollerApplied = true;
            Global.applyFastScroller(findViewById(R.id.recycler));
        }
    }
}
