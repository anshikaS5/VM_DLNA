package com.vmokshagroup.dlnaplayer.activity;


import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vmokshagroup.dlnaplayer.R;
import com.vmokshagroup.dlnaplayer.adapter.CustomListAdapter;
import com.vmokshagroup.dlnaplayer.modelclass.CustomListItem;
import com.vmokshagroup.dlnaplayer.modelclass.DeviceModel;
import com.vmokshagroup.dlnaplayer.modelclass.ItemModel;
import com.vmokshagroup.dlnaplayer.modelclass.MimeTypeMap;
import com.vmokshagroup.dlnaplayer.modelclass.ModelClass;
import com.vmokshagroup.dlnaplayer.modelclass.VideoGridModel;
import com.vmokshagroup.dlnaplayer.util.ProgressHUD;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.net.URI;
import java.util.ArrayList;
import java.util.Stack;

public class MediaServerActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, View
        .OnClickListener, DialogInterface.OnCancelListener, AdapterView.OnItemClickListener {


    @Override
    public void onCancel(DialogInterface dialog) {

    }

    @Override
    public void onClick(View v) {

    }

    public static interface Callbacks {
        void onDisplayDevices();

        void onDisplayDirectories();

        void onDisplayItems(ArrayList<ItemModel> items);

        void onDisplayItemsError(String error);

        void onDeviceAdded(DeviceModel device);

        void onDeviceRemoved(DeviceModel device);
    }

    private ArrayAdapter<CustomListItem> mDeviceListAdapter;
    private TextView mTxtNoDevices;
    public static ArrayList<VideoGridModel> mVideoGridArray;
    boolean isGone = false;
    private Callbacks mCallbacks;
    private BrowseRegistryListener mListener = new BrowseRegistryListener();
    private AndroidUpnpService mService;
    private Stack<ItemModel> mFolders = new Stack<ItemModel>();
    private Boolean mIsShowingDeviceList = true;
    private DeviceModel mCurrentDevice = null;
    private ListView mListView;
    private Toolbar toolbar;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_server);

        initializeUI();
        setSupportActionBar(toolbar);
        pd = new ProgressDialog(MediaServerActivity.this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mVideoGridArray = new ArrayList<VideoGridModel>();
        mDeviceListAdapter = new CustomListAdapter(this);
       /* if (mDeviceListAdapter.getCount() > 0) {
            mTxtNoDevices.setVisibility(View.INVISIBLE);
        } else {
            mTxtNoDevices.setVisibility(View.VISIBLE);
        }*/
        mListView.setAdapter(mDeviceListAdapter);

        //TODO:onBroadcast receiver we already checking for device and refreshing the device

       /* refreshDevices();
        refreshCurrent();*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        //filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
        bindServiceConnection();
        mCallbacks = new Callbacks() {
            @Override
            public void onDisplayDevices() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                       /* if (mDeviceListAdapter.getCount() > 0) {
                            mTxtNoDevices.setVisibility(View.INVISIBLE);
                        } else {
                            mTxtNoDevices.setVisibility(View.INVISIBLE);
                        }*/
                        mListView.setAdapter(mDeviceListAdapter);
                    }
                });
            }

            @Override
            public void onDisplayDirectories() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mItemListAdapter.clear();
//                        setListAdapter(mItemListAdapter);
                    }
                });
            }

            @Override
            public void onDisplayItems(final ArrayList<ItemModel> items) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).getTitle().equals("Video") ||
                                    (items.get(i).getTitle().equals("All Video"))) {
                                navigateTo(items.get(i));
                            } else if (items.get(i).getTitle().equals("Browse Folders") ||
                                    (items.get(i).getTitle().equals("Music") || (items.get(i).getTitle().equals("Pictures")) || (items.get(i).getTitle().equals("Folders")))) {
                            } else {
                                mVideoGridArray.clear();
                                for (int i1 = 0; i1 < items.size(); i1++) {
                                    mVideoGridArray.add(new VideoGridModel(items.get(i1).getIcon(), items.get(i1).getTitle(), items.get(i1).getDescription(), items.get(i1).getDescription2
                                            (), items.get(i1)
                                            .getIconUrl(), items.get(i1).getHideIcon(), items.get(i1).getItemsValue(), items.get(i1).getFileIcon()));
                                }
                            }
                        }
                        if (!isGone && mVideoGridArray.size() > 0) {
                            isGone = true;
                            Intent intent = new Intent(MediaServerActivity.this, GridVideoActivity.class);
                            startActivity(intent);
                            FragmentManager fm = MediaServerActivity.this.getFragmentManager();
                            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                fm.popBackStack();
                            }
                        }
                    }
                });
            }

            @Override
            public void onDisplayItemsError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mItemListAdapter.clear();
//                        mItemListAdapter.add(new CustomListItem(
//                                R.drawable.ic_warning,
//                                getResources().getString(R.string.info_errorlist_folders),
//                                error));
                    }
                });
            }

            @Override
            public void onDeviceAdded(final DeviceModel device) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int position = mDeviceListAdapter.getPosition(device);

                       /* ProgressDialog pd = new ProgressDialog(MediaServerActivity.this);
                        pd.show();*/
                       /* if (pd == null)
                            pd = ProgressHUD.show(MediaServerActivity.this, "", true, true, MediaServerActivity.this);*/


                        if (position >= 0) {
                            mDeviceListAdapter.remove(device);
                            mDeviceListAdapter.insert(device, position);
                        } else {
                            mDeviceListAdapter.add(device);
                        }

                        //TODO:my <code></code>

                       /* if ((position==0)||(position==-1)||(mDeviceListAdapter.getCount() > 0)) {
                            mTxtNoDevices.setVisibility(View.GONE);
                        } else {
                            mTxtNoDevices.setVisibility(View.VISIBLE);
                        }*/

                        if ((mDeviceListAdapter.getCount() > 0)) {
                            mTxtNoDevices.setVisibility(View.GONE);
                        } else {
                            mTxtNoDevices.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }

            @Override
            public void onDeviceRemoved(final DeviceModel device) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       /* if (mDeviceListAdapter.getCount() <= 0) {
                            mTxtNoDevices.setVisibility(View.VISIBLE);
                        } else {
                            mTxtNoDevices.setVisibility(View.INVISIBLE);
                        }*/
                        mDeviceListAdapter.remove(device);
                    }
                });
            }
        };

    }


    @Override
    protected void onResume() {
        super.onResume();

        mDeviceListAdapter.clear();
        unregisterReceiver(receiver);
        unbindServiceConnection();
        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        registerReceiver(receiver, filter);
        bindServiceConnection();
     /*     refreshDevices()*/
        ;
     /*   refreshCurrent();*/

//        if (mDeviceListAdapter.getCount() > 0) {
//            mTxtNoDevices.setVisibility(View.INVISIBLE);
//        } else {
//            mTxtNoDevices.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
        unbindServiceConnection();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        refreshCurrent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        isGone = false;
        navigateTo(parent.getItemAtPosition(position));

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN);
                /*mProgresser_bar.setVisibility(View.VISIBLE);*/
                TextView wifi_warning = (TextView) findViewById(R.id.wifi_warning);
                switch (state) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        wifi_warning.setVisibility(View.GONE);
                        if (!pd.isShowing()) {
                            pd.setMessage("Please Wait...");
                            pd.show();
                        }
                        refreshDevices();
                        refreshCurrent();
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                      /*  mProgresser_bar.setVisibility(View.GONE);*/
                        wifi_warning.setVisibility(View.VISIBLE);
                        mTxtNoDevices.setVisibility(View.GONE);
                        mDeviceListAdapter.clear();
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        wifi_warning.setVisibility(View.VISIBLE);
                        mTxtNoDevices.setVisibility(View.GONE);
                        break;

                }
            }
        }
    };

    //initialize ui
    private void initializeUI() {
        mTxtNoDevices = (TextView) findViewById(R.id.servers);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setOnItemClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
    }

    public void navigateTo(Object model) {

        if (model instanceof DeviceModel) {
            DeviceModel deviceModel = (DeviceModel) model;
            Device device = deviceModel.getDevice();

            if (device.isFullyHydrated()) {
                Service conDir = deviceModel.getContentDirectory();

                if (conDir != null)
                    mService.getControlPoint().execute(
                            new CustomContentBrowseActionCallback(conDir, "0"));

                if (mCallbacks != null)
                    mCallbacks.onDisplayDirectories();

                mIsShowingDeviceList = false;

                mCurrentDevice = deviceModel;
            } else {
                Toast.makeText(MediaServerActivity.this, R.string.info_still_loading, Toast.LENGTH_SHORT)
                        .show();
            }
        }

        if (model instanceof ItemModel) {

            ItemModel item = (ItemModel) model;

            if (item.isContainer()) {
                if (mFolders.isEmpty())
                    mFolders.push(item);
                else if (mFolders.peek().getId() != item.getId())
                    mFolders.push(item);

                mService.getControlPoint().execute(
                        new CustomContentBrowseActionCallback(item.getService(),
                                item.getId()));

            } else {
              /*  try {
                    Uri uri = Uri.parse(item.getUrl());
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String type = mime.getMimeTypeFromUrl(uri.toString());
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, type);
                    startActivity(intent);
                } catch (NullPointerException ex) {
                    Toast.makeText(MediaServerActivity.this, R.string.info_could_not_start_activity, Toast.LENGTH_SHORT)
                            .show();
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(MediaServerActivity.this, R.string.info_no_handler, Toast.LENGTH_SHORT)
                            .show();
                }*/
            }
        }
    }

    public Boolean goBack() {


        if (!mIsShowingDeviceList) {
            mIsShowingDeviceList = true;
            if (mCallbacks != null)
                mCallbacks.onDisplayDevices();
        } else {
            return true;
        }
        return false;

    }

    public void refreshDevices() {
        if (mService == null)
            return;

        mService.getRegistry().removeAllRemoteDevices();


       /* if (mService.getRegistry().getDevices().size() > 0) {
            mTxtNoDevices.setVisibility(View.INVISIBLE);
        } else {
            mTxtNoDevices.setVisibility(View.VISIBLE);
        }*/

        for (Device device : mService.getRegistry().getDevices())
            mListener.deviceAdded(device);

        if (mService.getRegistry().getDevices().size() > 0) {
            mTxtNoDevices.setVisibility(View.INVISIBLE);
        } else {
            mTxtNoDevices.setVisibility(View.VISIBLE);
        }
        if (pd.isShowing()) {
            pd.dismiss();
        }
        mService.getControlPoint().search();
    }

    public void refreshCurrent() {
        if (mService == null)
            return;

       /* mProgresser_bar.setVisibility(View.GONE);*/

        if (mIsShowingDeviceList != null && mIsShowingDeviceList) {
            if (mCallbacks != null)
                mCallbacks.onDisplayDevices();

            mService.getRegistry().removeAllRemoteDevices();

            for (Device device : mService.getRegistry().getDevices())
                mListener.deviceAdded(device);

            mService.getControlPoint().search();
        } else {
            if (!mFolders.empty()) {
                ItemModel item = mFolders.peek();
                if (item == null)
                    return;

                mService.getControlPoint().execute(
                        new CustomContentBrowseActionCallback(item.getService(),
                                item.getId()));
            } else {
                if (mCurrentDevice != null) {
                    Service service = mCurrentDevice.getContentDirectory();
                    if (service != null)
                        mService.getControlPoint().execute(
                                new CustomContentBrowseActionCallback(service, "0"));
                }
            }
        }
    }


    private Boolean bindServiceConnection() {
        Context context = getApplicationContext();
        if (context == null)
            return false;

        context.bindService(
                new Intent(MediaServerActivity.this, AndroidUpnpServiceImpl.class),
                serviceConnection, Context.BIND_AUTO_CREATE
        );

        return true;
    }

    private Boolean unbindServiceConnection() {
        if (mService != null)
            mService.getRegistry().removeListener(mListener);

        Context context = getApplicationContext();
        if (context == null)
            return false;

        context.unbindService(serviceConnection);
        return true;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = (AndroidUpnpService) service;
            mService.getRegistry().addListener(mListener);
            //Todo


           /* if (mService.getRegistry().getDevices().size() > 0) {
                mTxtNoDevices.setVisibility(View.INVISIBLE);
            } else if(((TextView) findViewById(R.id.wifi_warning)).getVisibility() != View.VISIBLE){
                mTxtNoDevices.setVisibility(View.VISIBLE);
            }*/
            for (Device device : mService.getRegistry().getDevices())
                mListener.deviceAdded(device);
            if (mService.getRegistry().getDevices().size() > 0) {
                mTxtNoDevices.setVisibility(View.INVISIBLE);
            } else if(((TextView) findViewById(R.id.wifi_warning)).getVisibility() != View.VISIBLE){
                mTxtNoDevices.setVisibility(View.VISIBLE);
            }
            if (pd.isShowing()) {
                pd.dismiss();
            }
            mService.getControlPoint().search();

        }


        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    private class BrowseRegistryListener extends DefaultRegistryListener {
        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            System.out.println(
                    "Discovery started: " + device.getDisplayString()
            );
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
            deviceRemoved(device);
        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            deviceRemoved(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            deviceAdded(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            deviceRemoved(device);
        }

        public void deviceAdded(Device device) {
            DeviceModel deviceModel = new DeviceModel(R.drawable.ic_device, device);
            Service conDir = deviceModel.getContentDirectory();
            if (conDir != null) {
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(MediaServerActivity.this);
                if (prefs.getBoolean("settings_validate_devices", false)) {
                    // all of its services, actions, and state variables are available.
                    if (device.isFullyHydrated())
                        mService.getControlPoint().execute(
                                new CustomContentBrowseTestCallback(device, conDir));
                } else {
                    if (mCallbacks != null) {
                        ModelClass.mDeviceModel = new ArrayList<DeviceModel>();

                        ModelClass.mDeviceModel.add(deviceModel);
                        mCallbacks.onDeviceAdded(deviceModel);

                    }
                }
            }
        }

        public void deviceRemoved(Device device) {
            if (mCallbacks != null)
                mCallbacks.onDeviceRemoved(new DeviceModel(R.drawable.ic_device, device));
        }
    }

    private class CustomContentBrowseActionCallback extends Browse {
        private Service service;

        public CustomContentBrowseActionCallback(Service service, String id) {
            super(service, id, BrowseFlag.DIRECT_CHILDREN, "*", 0, null, new SortCriterion(true, "dc:title"));

            this.service = service;

            if (mCallbacks != null)
                mCallbacks.onDisplayDirectories();
        }

        private ItemModel createItemModel(DIDLObject item) {

            ItemModel itemModel = new ItemModel(getResources(), R.drawable.ic_folder, service, item);

           /* ItemModel itemModel = new ItemModel(getResources(),
                    R.drawable.ic_folder, service, item);*/

            URI usableIcon = item.getFirstPropertyValue(DIDLObject.Property.UPNP.ICON.class);
            if (usableIcon != null)
                itemModel.setIconUrl(usableIcon.toString());

            if (item instanceof Item) {
//                itemModel.setIcon(R.drawable.ic_file);
                itemModel.setFileIcon(item.getResources().get(0).getValue());

                SharedPreferences prefs =
                        PreferenceManager.getDefaultSharedPreferences(MediaServerActivity.this);

                if (prefs.getBoolean("settings_hide_file_icons", false))
                    itemModel.setHideIcon(true);

                if (prefs.getBoolean("settings_show_extensions", false))
                    itemModel.setShowExtension(true);

                // item.getResources().get(0).getValue();
            }

            return itemModel;
        }

        @Override
        public void received(final ActionInvocation actionInvocation, final DIDLContent didl) {

            ArrayList<ItemModel> items = new ArrayList<ItemModel>();

            try {

                for (Container childContainer : didl.getContainers())
                    items.add(createItemModel(childContainer));

                for (Item childItem : didl.getItems())
                    items.add(createItemModel(childItem));

                if (mCallbacks != null)
                    mCallbacks.onDisplayItems(items);

            } catch (Exception ex) {
                actionInvocation.setFailure(new ActionException(
                        ErrorCode.ACTION_FAILED,
                        "Can't create list childs: " + ex, ex));
                failure(actionInvocation, null, ex.getMessage());
            }
        }

        @Override
        public void updateStatus(Status status) {

        }

        @Override
        public void failure(ActionInvocation invocation, UpnpResponse response, String s) {
            if (mCallbacks != null)
                mCallbacks.onDisplayItemsError(createDefaultFailureMessage(invocation, response));
        }
    }

    private class CustomContentBrowseTestCallback extends Browse {
        private Device device;
        private Service service;

        public CustomContentBrowseTestCallback(Device device, Service service) {
            super(service, "0", BrowseFlag.DIRECT_CHILDREN, "*", 0, null,
                    new SortCriterion(true, "dc:title"));

            this.device = device;
            this.service = service;
        }

        @Override
        public void received(final ActionInvocation actionInvocation, final DIDLContent didl) {
            if (mCallbacks != null)
                mCallbacks.onDeviceAdded(new DeviceModel(R.mipmap.icon_server_name, device));
        }

        @Override
        public void updateStatus(Status status) {

        }

        @Override
        public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_media_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MediaServerActivity.this, SettingActivity.class));
            return true;
        }
        if (id == R.id.action_refresh) {
           /* if (mDeviceListAdapter.getCount() > 0) {
                mTxtNoDevices.setVisibility(View.INVISIBLE);
            } else {
                mTxtNoDevices.setVisibility(View.VISIBLE);
            }*/
            mDeviceListAdapter.clear();
            mListView.setAdapter(mDeviceListAdapter);
            unregisterReceiver(receiver);
            unbindServiceConnection();

            final IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            registerReceiver(receiver, filter);
            bindServiceConnection();

            //TODO:
           /* refreshDevices();
          refreshCurrent();*/
        }
        return super.onOptionsItemSelected(item);
    }

}
