package cn.qqwh.zhihuwanbao.activity;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import cn.qqwh.zhihuwanbao.R;
import cn.qqwh.zhihuwanbao.db.CacheDbHelper;
import cn.qqwh.zhihuwanbao.fragment.MainFragment;
import cn.qqwh.zhihuwanbao.fragment.MenuFragment;
import cn.qqwh.zhihuwanbao.fragment.NewsFragment;

public class MainActivity extends AppCompatActivity {
    private FrameLayout fl_content;
    private MenuFragment menu_fragment;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout sr;
    private long firstTime;
    private String curId;
    private Toolbar toolbar;
    private boolean isLight;
    private CacheDbHelper dbHelper;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        dbHelper = new CacheDbHelper(this, 1);
        isLight = sp.getBoolean("isLight", true);
        initView();
        loadLatest();
    }

    public void loadLatest() {
        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left).
                replace(R.id.fl_content,new MainFragment(), "latest").
                commit();
        curId = "latest";
    }

    public void setCurId(String id) {
        curId = id;
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));
        setSupportActionBar(toolbar);
        setStatusBarColor(getResources().getColor(isLight ? R.color.light_toolbar : R.color.dark_toolbar));

        sr = (SwipeRefreshLayout) findViewById(R.id.sr);
        sr.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                replaceFragment();
                sr.setRefreshing(false);
            }
        });
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    public void replaceFragment() {
        if (curId.equals("latest")) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                    .replace(R.id.fl_content, new MainFragment(), "latest").commit();
        } else {

        }

    }

    public void closeMenu() {
        mDrawerLayout.closeDrawers();
    }

    public void setSwipeRefreshEnable(boolean enable) {
        sr.setEnabled(enable);
    }

    public void setToolbarTitle(String text) {
        toolbar.setTitle(text);
    }



    public boolean isLight() {
        return isLight;
    }

    public CacheDbHelper getCacheDbHelper() {
        return dbHelper;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            closeMenu();
        } else {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Snackbar sb = Snackbar.make(fl_content, "再按一次退出", Snackbar.LENGTH_SHORT);
                sb.getView().setBackgroundColor(getResources().getColor(isLight ? android.R.color.holo_blue_dark : android.R.color.black));
                sb.show();
                firstTime = secondTime;
            } else {
                finish();
            }
        }

    }


    @TargetApi(21)
    private void setStatusBarColor(int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // If both system bars are black, we can remove these from our layout,
            // removing or shrinking the SurfaceFlinger overlay required for our views.
            Window window = this.getWindow();
            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }
}
