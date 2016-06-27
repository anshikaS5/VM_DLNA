package com.vmokshagroup.dlnaplayer.activity;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vmokshagroup.dlnaplayer.R;
import com.vmokshagroup.dlnaplayer.adapter.CustomListAdapter;
import com.vmokshagroup.dlnaplayer.modelclass.CustomListItem;
import com.vmokshagroup.dlnaplayer.modelclass.DeviceModel;
import com.vmokshagroup.dlnaplayer.modelclass.ItemModel;
import com.vmokshagroup.dlnaplayer.modelclass.ModelClass;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity implements View
        .OnClickListener {

    TextView mVersiontext, mAppversiontext;
    TextView mToolBar_Title;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initializeUI();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeUI() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar_Title = (TextView) findViewById(R.id.text);
        mVersiontext = (TextView) findViewById(R.id.version_text);
        mAppversiontext = (TextView) findViewById(R.id.appversion_text);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            mAppversiontext.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        //startActivity(new Intent(this, MediaServerActivity.class));


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


        }

    }

}
