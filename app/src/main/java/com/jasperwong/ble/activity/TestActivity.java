package com.jasperwong.ble.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jasperwong.ble.R;
import com.jasperwong.ble.ble.BLEService;
import com.jasperwong.ble.ble.GATTUtils;

import java.util.List;

public class TestActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG=this.getClass().getSimpleName();
    private BLEService mBluetoothLeService=null;
    BluetoothGattCharacteristic mCharacteristic=null;
    private TextView FrontShow;
    private TextView LeftShow;
    private TextView RightShow;
    private TextView RecShow;
    private EditText ed_send;
    private String show="";
    private Button btn_send;
    private static int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_test);
        setSupportActionBar(toolbar);
        FrontShow=(TextView)findViewById(R.id.FrontShowView);
        LeftShow=(TextView)findViewById(R.id.LeftShowView);
        RightShow=(TextView)findViewById(R.id.RightShowView);
        RecShow=(TextView)findViewById(R.id.RecShow);
        btn_send=(Button)findViewById(R.id.btn_send_view);
        btn_send.setOnClickListener(this);
        ed_send=(EditText)findViewById(R.id.edit_send_view);
        Intent gattServiceIntent=new Intent(TestActivity.this,BLEService.class);
        bindService(gattServiceIntent,mServiceConnection,BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_send_view){
            String Tx;
            Tx=ed_send.getText().toString();
            mCharacteristic.setValue(Tx);
            mBluetoothLeService.writeCharacteristic(mCharacteristic);
        }
    }

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

    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service)
        {
            Log.d(TAG, "start service Connection");

            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();
            //从搜索出来的services里面找出合适的service
            List<BluetoothGattService> gattServiceList = mBluetoothLeService.getSupportedGattServices();
            mCharacteristic = GATTUtils.lookupGattServices(gattServiceList, GATTUtils.BLE_TX);
            if( null != mCharacteristic )
            {
//                mCharacteristic.setValue("G");
                mCharacteristic.setValue("service connect");
                mBluetoothLeService.writeCharacteristic(mCharacteristic);
                mBluetoothLeService.setCharacteristicNotification(mCharacteristic, true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            Log.d(TAG, "end Service Connection");
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
//                mConnected = true;
//                Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_LONG).show();
//                Intent intent1=new Intent(MainActivity.this,TestActivity.class);
//                startActivity(intent1);

            }  else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                List<BluetoothGattService> gattServiceList = mBluetoothLeService.getSupportedGattServices();
                BluetoothGattCharacteristic mCharacteristic = GATTUtils.lookupGattServices(gattServiceList, GATTUtils.BLE_TX);
                mCharacteristic.setValue("connect success");
                mBluetoothLeService.writeCharacteristic(mCharacteristic);
                mBluetoothLeService.setCharacteristicNotification(mCharacteristic,true);

            }   else if(BLEService.ACTION_DATA_WRITE.equals(action)){
                Log.d("test","write");

            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mConnected = false;
//                Toast.makeText(MainActivity.this,"断开连接",Toast.LENGTH_LONG).show();
            } else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)) {



                String Rx=intent.getStringExtra(BLEService.EXTRA_DATA);
                if(BLEService.RecIsDone) {
                    String FrontDistance = intent.getStringExtra(BLEService.FRONT_DATA);
                    String LeftDistance = intent.getStringExtra(BLEService.LEFT_DATA);
                    String RightDistance = intent.getStringExtra(BLEService.RIGHT_DATA);
                    FrontShow.setText(FrontDistance);
                    LeftShow.setText(LeftDistance);
                    RightShow.setText(RightDistance);
                    BLEService.RecIsDone = false;
                    BLEService.rec_state = BLEService.RecState.WAIT_F;
                    Log.d("Rx_test","Front:"+FrontDistance);
                    Log.d("Rx_test","Left:"+LeftDistance);
                    Log.d("Rx_test","Right:"+RightDistance);
                }
                show+=Rx;
                RecShow.setText(show);
                i=RecShow.getLineCount();
                i++;
                if(i>=12){
                    show="";
                    i=0;
                }



            } else if(BLEService.ACTION_DATA_READ.equals(action)){

            }
        }
    };
}
