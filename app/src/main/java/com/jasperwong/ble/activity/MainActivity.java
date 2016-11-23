package com.jasperwong.ble.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasperwong.ble.R;
import com.jasperwong.ble.ble.BLEService;
import com.jasperwong.ble.ble.DeviceAdapter;
import com.jasperwong.ble.ble.GATTUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener ,View.OnClickListener{

    private ProgressDialog progDialog = null;
    private boolean mConnected = false;
    private TextView mDataField;

    private ArrayList<BluetoothDevice> mDeviceList= new ArrayList<BluetoothDevice>();

    private BluetoothDevice BleDevice=null;

    private TextView stateTV;

    private ListView mListView;

    private DeviceAdapter mDeviceAdapter;

    private final static String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private String mDeviceName;
    private String mDeviceAddress;
    private MenuItem mState;

    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext=this;
    private boolean isConnected=false;
    private BLEService mBluetoothLeService=null;
    public static boolean ConnectedState=false;

    private onScanDeviceListener mOnScanDeviceListener;

    BluetoothGattCharacteristic mCharacteristic=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

//        final Intent intent = getIntent();
//        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
//        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        InitBLE();
        startScan();
        mListView=(ListView)findViewById(R.id.list_device);
        mDeviceAdapter=new DeviceAdapter(this);
        mDeviceAdapter.setData(mDeviceList);
        mListView.setAdapter(mDeviceAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BleDevice=mDeviceList.get(position);
                ConnectedState=mBluetoothLeService.connect(BleDevice.getAddress());
            }
        });
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        mState=(MenuItem)drawer.findViewById(R.id.nav_guide);

        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void InitBLE(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mIntent, 1);
        }
    }

    private void showProgressDialog() {
        if (progDialog == null){
            progDialog = new ProgressDialog(this);
        }
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
//        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if(mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopScan();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService.disconnect();
        mBluetoothLeService.close();
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            List<BluetoothGattService> gattServiceList = mBluetoothLeService.getSupportedGattServices();
            mCharacteristic = GATTUtils.lookupGattServices(gattServiceList, GATTUtils.BLE_TX);
            if( null != mCharacteristic )
            {
//                mCharacteristic.setValue('g'+"");
//                mBluetoothLeService.writeCharacteristic(mCharacteristic);
//                mBluetoothLeService.setCharacteristicNotification(mCharacteristic, true);
            }
//            mBluetoothLeService.setCharacteristicNotification(mCharacteristic,true);
            // Automatically connects to the device upon successful start-up initialization.
//            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_LONG).show();
                Intent intent1=new Intent(MainActivity.this,TestActivity.class);
                startActivity(intent1);

            }  else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                List<BluetoothGattService> gattServiceList = mBluetoothLeService.getSupportedGattServices();
                BluetoothGattCharacteristic mCharacteristic = GATTUtils.lookupGattServices(gattServiceList, GATTUtils.BLE_TX);
                mCharacteristic.setValue("connect success");
                mBluetoothLeService.writeCharacteristic(mCharacteristic);
                mBluetoothLeService.setCharacteristicNotification(mCharacteristic,true);

            }   else if(BLEService.ACTION_DATA_WRITE.equals(action)){
                Log.d("test","write");

            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Toast.makeText(MainActivity.this,"断开连接",Toast.LENGTH_LONG).show();
            } else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)) {
//                String Rx=intent.getStringExtra(BLEService.EXTRA_DATA);
//                Log.d("Rx","Rx:"+Rx);
            }else if(BLEService.ACTION_DATA_READ.equals(action)){

            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BLEService.ACTION_DATA_WRITE);
        intentFilter.addAction(BLEService.ACTION_DATA_READ);
        return intentFilter;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(id==R.id.action_scan){
//            mDeviceAdapter.clear();
            startScan();
        }

        if(id==R.id.action_refresh){
            stopScan();
            mBluetoothLeService.disconnect();
            mDeviceList.clear();
            startScan();
        }

        if (id == R.id.action_disconnect) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.nav_guide) {
//            Intent guideIntent= new Intent(this,RouteActivity.class);
//            startActivity(guideIntent);
//        } else if (id == R.id.nav_switch) {
//            Intent switchIntent=new Intent(this,SwitchActivity.class);
//            startActivity(switchIntent);
//        } else if (id == R.id.nav_setting) {
//            Intent settingIntent=new Intent(this,SettingActivity.class);
//            startActivity(settingIntent);
//        }else if(id==R.id.nav_share){
//            Intent intent=new Intent(this,UserActivity.class);
//            startActivity(intent);
//        }
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            default:
                break;
        }
    }



    public boolean startScan() {
        if(null == mBluetoothAdapter)
        {
            return false;
        }
        showProgressDialog();
        return mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    public void setOnScanDeviceListener(onScanDeviceListener l)
    {
        mOnScanDeviceListener = l;
    }

    public interface onScanDeviceListener {
        public void onScanDevice(final BluetoothDevice device, final int rssi, byte[] scanRecord);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDeviceAdapter.addDevice(device);
                    mDeviceAdapter.notifyDataSetChanged();
                    dissmissProgressDialog();
//                    Log.d("test","add");
                }
            });
        }

    };

    public boolean stopScan()
    {
        if(null == mBluetoothAdapter)
        {
            return false;
        }

        mBluetoothAdapter.stopLeScan(mLeScanCallback);

        return true;
    }


}
