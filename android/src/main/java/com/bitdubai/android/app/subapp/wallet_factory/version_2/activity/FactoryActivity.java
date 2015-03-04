package com.bitdubai.android.app.subapp.wallet_factory.version_2.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.bitdubai.android.app.RuntimeAppActivity;
import com.bitdubai.fermat_api.layer._12_middleware.app_runtime.AppRuntimeManager;
import com.bitdubai.fermat_api.layer._12_middleware.app_runtime.enums.Activities;
import com.bitdubai.fermat_api.layer._1_definition.enums.Plugins;
import com.bitdubai.fermat_core.CorePlatformContext;
import com.bitdubai.fermat_core.Platform;
import com.bitdubai.smartwallet.R;
import com.bitdubai.android.app.common.version_1.classes.MyApplication;
import com.bitdubai.android.app.common.version_1.classes.PagerSlidingTabStrip;
import com.bitdubai.android.app.subapp.wallet_factory.version_2.fragment.BalanceFragment;
import com.bitdubai.android.app.subapp.wallet_factory.version_2.fragment.DiscountsFragment;
import com.bitdubai.android.app.subapp.wallet_factory.version_2.fragment.HomeFragment;
import com.bitdubai.android.app.subapp.wallet_factory.version_2.fragment.ReceiveFragment;
import com.bitdubai.android.app.subapp.wallet_factory.version_2.fragment.RefillFragment;
import com.bitdubai.android.app.subapp.wallet_factory.version_2.fragment.ResourcesFragment;
import com.bitdubai.android.app.subapp.wallet_factory.version_2.fragment.SendFragment;
import com.bitdubai.android.app.subapp.wallet_factory.version_2.fragment.ShopFragment;

import com.bitdubai.android.app.subapp.wallet_runtime.wallet_framework.version_1.classes.MyLayoutInflaterFactory;
public class FactoryActivity extends FragmentActivity
{

    private final Handler handler = new Handler();

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor = 0xFFff9900;
    private String walletStyle = "";
    private CharSequence mTitle = "Wallet Factory";
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MyApplication.setActivityId("FactoryActivity");
        setContentView(R.layout.wallet_factory_activity);

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView abTitle = (TextView) findViewById(titleId);

        Intent i=getIntent();

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);


        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/CaviarDreams.ttf");
        ((MyApplication) this.getApplication()).setDefaultTypeface(tf);
        tabs.setTypeface(tf, 1);
        //changeColor(currentColor);

        tabs.setDividerColor(0xFFFFFFFF);
        tabs.setIndicatorColor(0xFFFFFFFF);
        tabs.setIndicatorHeight(9);
        tabs.setBackgroundColor(0xFFff9900);
        tabs.setTextColor(0xFFFFFFFF);

        String color = "#ff9900";
        MyApplication.setActionBar(getActionBar());
        MyApplication.setDefaultTypeface(MyApplication.getDefaultTypeface());
        ((MyApplication) this.getApplication()).changeColor(Color.parseColor(color), getResources());


        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setIcon(R.drawable.ic_action_factory);
        abTitle.setTypeface(MyApplication.getDefaultTypeface());

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.wallet_shop_activity_account_detail_menu, menu);

        LayoutInflater inflaterClone = getLayoutInflater().cloneInContext(getLayoutInflater().getContext());
        LayoutInflater.Factory lif = new MyLayoutInflaterFactory();
        inflaterClone.setFactory(lif);

    return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
/*
            case R.id.action_contact:
                TabbedDialogFragment dialog = new TabbedDialogFragment();
                dialog.show(getSupportFragmentManager(), "QuickContactFragment");
                return true;
*/
            case R.id.action_requests_sent:
                Intent intent;
              //  intent = new Intent(this, RequestsSentActivity.class);
               // startActivity(intent);

                Platform platform = MyApplication.getPlatform();
                CorePlatformContext platformContext = platform.getCorePlatformContext();

                AppRuntimeManager appRuntimeMiddleware =  (AppRuntimeManager)platformContext.getPlugin(Plugins.APP_RUNTIME_MIDDLEWARE);
                appRuntimeMiddleware =  (AppRuntimeManager)platformContext.getPlugin(Plugins.APP_RUNTIME_MIDDLEWARE);

                appRuntimeMiddleware.getActivity(Activities.CWP_WALLET_ADULTS_ALL_CHAT_TRX);

                intent = new Intent(this, com.bitdubai.android.app.FragmentActivity.class);

                startActivity(intent);
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    private void changeColor(int newColor) {

        tabs.setIndicatorColor(newColor);

        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(ld);
                }

            } else {

                TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(td);
                }

                td.startTransition(200);

            }

            oldBackground = ld;


            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);

        }

        currentColor = newColor;

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //   currentColor = savedInstanceState.getInt("currentColor");
        //  changeColor(currentColor);
    }
    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };



    public class MyPagerAdapter extends FragmentPagerAdapter {

        private String[] titles;
        private String[] titles_1 = { };
        private String[] titles_2 = {  "Home", "Balance", "Send", "Receive","Shops","Refill","Discounts","Resources"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if (walletStyle.equals("Kids") )
            {titles = titles_1;}
            else
            {titles = titles_2;}

            return titles[position];
        }

        @Override
        public int getCount() {
           titles = titles_2;
            return titles.length;
        }
        @Override
        public Fragment getItem(int position) {
           titles = titles_2;
            Fragment currentFragment;
            switch (position) {
                case 0:
                    currentFragment = HomeFragment.newInstance(position);
                    break;
                case 1:
                    currentFragment = BalanceFragment.newInstance(position);
                    break;
                case 2:
                    currentFragment = SendFragment.newInstance(position);
                    break;
                case 3:
                    currentFragment = ReceiveFragment.newInstance(position);
                    break;
                case 4:
                    currentFragment = ShopFragment.newInstance(position);
                    break;
                case 5:
                    currentFragment = RefillFragment.newInstance(position);
                    break;
                case 7:
                    currentFragment = ResourcesFragment.newInstance(position);
                    break;
                default:
                    currentFragment = DiscountsFragment.newInstance(position);
                    break;
            }
            return currentFragment;
        }
    }
}