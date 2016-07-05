package com.tranxuanloc.emobits.main;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.tranxuanloc.emobits.R;
import com.tranxuanloc.emobits.dj.DJFragment;
import com.tranxuanloc.emobits.home.HomeFragment;
import com.tranxuanloc.emobits.listennow.ListenNowFragment;
import com.tranxuanloc.emobits.pref.UserInfoPref;
import com.tranxuanloc.emobits.retrofit.MyRetrofit;
import com.tranxuanloc.emobits.retrofit.NoInternet;
import com.tranxuanloc.emobits.retrofit.RetrofitError;
import com.tranxuanloc.emobits.search.SearchFragment;
import com.tranxuanloc.emobits.search.SessionInfo;
import com.tranxuanloc.emobits.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MainActivity extends AppCompatActivity implements SearchFragment.OnSessionSelectedListener {
    private final String TAG = MainActivity.class.getSimpleName();
    private UserInfoPref userInfoPref = new UserInfoPref();
    private View.OnClickListener action;
    private Utilities util = new Utilities();
    private ProgressBar bar;
    private TextView tvError;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (userInfoPref.getName(getApplicationContext()).equalsIgnoreCase("")) {
            setContentView(R.layout.load_data);
            userInfoPref.putInfo(MainActivity.this, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), "test", "");
            initialUI();

            /*action = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadUserInfo();
                }
            };
            uploadUserInfo();*/
        } else
            initialUI();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initialUI() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(HomeFragment.newInstance(0));
        mSectionsPagerAdapter.addFragment(DJFragment.newInstance(0));
        mSectionsPagerAdapter.addFragment(SearchFragment.newInstance(0));
        mSectionsPagerAdapter.addFragment(ListenNowFragment.newInstance());
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(0);
        }


        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.setSelectedTabIndicatorHeight(0);
            setCustomTab(mTabLayout);
        }


    }

    private void setCustomTab(TabLayout tabLayout) {
        TextView tab1 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tab1.setText(R.string.home);
        tab1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_one, 0, 0);
        TabLayout.Tab tabAt0 = tabLayout.getTabAt(0);
        if (tabAt0 != null)
            tabAt0.setCustomView(tab1);
        tab1.setSelected(true);
        TextView tab2 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tab2.setText(R.string.dj);
        tab2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_two, 0, 0);
        TabLayout.Tab tabAt1 = tabLayout.getTabAt(1);
        if (tabAt1 != null)
            tabAt1.setCustomView(tab2);

        TextView tab3 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tab3.setText(R.string.search);
        tab3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_three, 0, 0);
        TabLayout.Tab tabAt2 = tabLayout.getTabAt(2);
        if (tabAt2 != null)
            tabAt2.setCustomView(tab3);

        TextView tab4 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tab4.setText(R.string.listen_now);
        tab4.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_four, 0, 0);
        tab4.setClickable(false);
        TabLayout.Tab tabAt3 = tabLayout.getTabAt(3);
        if (tabAt3 != null)
            tabAt3.setCustomView(tab4);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void uploadUserInfo() {
        bar = (ProgressBar) findViewById(R.id.progress_bar);
        tvError = (TextView) findViewById(R.id.tv_error);
        showLoad();
        if (!Utilities.isConnected(this))
            updateUIError(getString(R.string.no_internet), new NoInternet());
        else
            MyRetrofit.initRequest(this).user(util.getAndroidID(this), Build.MODEL, "null").enqueue(new Callback<UserInfo>() {
                @Override
                public void onResponse(Response<UserInfo> response, Retrofit retrofit) {
                    Log.d(TAG, "onResponse: " + new Gson().toJson(response.body()));
                    if (response.isSuccess() && response.body() != null) {
                        UserInfo info = response.body();
                        if (info.getStatus() == 1) {
                            UserInfo.ListUserInfo listUserInfo = info.getData().get(0);
                            userInfoPref.putInfo(MainActivity.this, listUserInfo.getDevice(), listUserInfo.getName(), listUserInfo.getParam());
                            initialUI();
                        } else
                            updateUIError(getString(R.string.error_system), new Throwable());
                    } else
                        updateUIError(getString(R.string.error_system), new Throwable());
                }

                @Override
                public void onFailure(Throwable t) {
                    updateUIError(RetrofitError.getErrorMessage(getApplicationContext(), t), t);
                }
            });

    }

    private void updateUIError(String string, Throwable error) {
        assert bar != null;
        bar.setVisibility(View.INVISIBLE);
        assert tvError != null;
        tvError.setText(string);
        tvError.setVisibility(View.VISIBLE);
        RetrofitError.errorWithAction(this, error, TAG, bar, action);
    }

    private void showLoad() {
        assert bar != null;
        bar.setVisibility(View.VISIBLE);
        assert tvError != null;
        tvError.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSessionSelected(SessionInfo.ListSessionInfo info) {
        Log.d(TAG, "OnSessionSelected: " + new Gson().toJson(info));
        ListenNowFragment fragment = (ListenNowFragment) mSectionsPagerAdapter.getItem(3);
        if (fragment != null) {
            fragment.updateUI(info);
            mViewPager.setCurrentItem(3, true);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getIntent() != null)
            if (getIntent().hasExtra("IS_NOTIFY"))
                mViewPager.setCurrentItem(3);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
      /*  client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tranxuanloc.emobits.main/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       /* Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tranxuanloc.emobits.main/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        public List<Fragment> getListFragment() {
            return mFragmentList;
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

    }
}
