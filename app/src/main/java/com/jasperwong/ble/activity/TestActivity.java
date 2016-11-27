package com.jasperwong.ble.activity;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jasperwong.ble.R;
import com.jasperwong.ble.ble.BLEService;
import com.jasperwong.ble.ble.GATTUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG=this.getClass().getSimpleName();
    private BLEService mBluetoothLeService=null;
    BluetoothGattCharacteristic mCharacteristic=null;
    final int HEIGHT = 200;
    final int WIDTH = 320;
    final int X_OFFSET = 0;
    private int cx = X_OFFSET;
    //实际的Y轴的位置
    int centerY = HEIGHT / 2;
    private EditText ed_send;
    private Button btn_send;
    private Button PPlus;
    private Button IPlus;
    private Button DPlus;
    private Button PMinus;
    private Button IMinus;
    private Button DMinus;
    private Button Update;
    private EditText PSet;
    private EditText ISet;
    private EditText DSet;
    private SeekBar PSeek;
    private SeekBar ISeek;
    private SeekBar DSeek;
    private TextView PShow;
    private TextView IShow;
    private TextView DShow;
    private TextView AngleShow;
    private float PsetValue=0;
    private float IsetValue=0;
    private float DsetValue=0;
    private WaverView waverView=null;
    Handler handler = null;
    Timer send_timer = new Timer( );
    ArrayList<InputStream> inputStreamArrayList = new ArrayList<InputStream>();
    TimerTask send_task = new TimerTask( ) {
        public void run ( ){
            Log.d("timer","inTimer");
            waverView.addData(520);
            waverView.postInvalidate();

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        waverView=(WaverView)findViewById(R.id.WaveView);

        btn_send=(Button)findViewById(R.id.btn_send_view);
        btn_send.setOnClickListener(this);
        ed_send=(EditText)findViewById(R.id.edit_send_view);
        PPlus=(Button)findViewById(R.id.PPlus_BTN);
        PMinus=(Button)findViewById(R.id.PMinus_BTN);
        IPlus=(Button)findViewById(R.id.IPlus_BTN);
        IMinus=(Button)findViewById(R.id.IMinus_BTN);
        DPlus=(Button)findViewById(R.id.DPlus_BTN);
        DMinus=(Button)findViewById(R.id.DMinus_BTN);
        PPlus.setOnClickListener(this);
        DPlus.setOnClickListener(this);
        IPlus.setOnClickListener(this);
        PMinus.setOnClickListener(this);
        IMinus.setOnClickListener(this);
        DMinus.setOnClickListener(this);

        PSet=(EditText)findViewById(R.id.PSet_ET);
        ISet=(EditText)findViewById(R.id.ISet_ET);
        DSet=(EditText)findViewById(R.id.DSet_ET);

        PSeek=(SeekBar)findViewById(R.id.PSeekBarView);
        ISeek=(SeekBar)findViewById(R.id.ISeekBarView);
        DSeek=(SeekBar)findViewById(R.id.DSeekBarView);
        PSeek.setMax(20000);
        ISeek.setMax(20000);
        DSeek.setMax(20000);
        //waverView.setTarget(160);

        send_timer.schedule(send_task,1000,50);
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {

                    case 1:
                        int length = msg.arg1;
                        byte[] buffer = (byte[])(msg.obj);

                        for(int i=0;i< length;i++)
                        {
//                            waverView.addData(buffer[i]);
                            waverView.addData(222);
                        }
                        break;
                    case 2:
                        //toastUtil.showToast((String)(msg.obj));
                        break;
                }

                super.handleMessage(msg);
            }
        };
        PSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){
                if(fromUser){
                    PsetValue=progress/1000.0f;
                    BigDecimal bigDecimal   =   new   BigDecimal(PsetValue);
                    PsetValue=bigDecimal.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();
                    PSet.setText(PsetValue+"");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ISeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){
                if(fromUser){
                    IsetValue=progress/1000.0f;
                    BigDecimal bigDecimal   =   new   BigDecimal(IsetValue);
                    IsetValue=bigDecimal.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();
                    ISet.setText(IsetValue+"");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        DSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){
                if(fromUser){
                    DsetValue=progress/1000.0f;
                    BigDecimal bigDecimal   =   new   BigDecimal(DsetValue);
                    DsetValue=bigDecimal.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();
                    DSet.setText(DsetValue+"");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        PShow=(TextView)findViewById(R.id.PShow_TV);
        IShow=(TextView)findViewById(R.id.IShow_TV);
        DShow=(TextView)findViewById(R.id.DShow_TV);
        AngleShow=(TextView)findViewById(R.id.AngleShow_TV);
        Update=(Button)findViewById(R.id.Update_BTN);
        Update.setOnClickListener(this);
        Intent gattServiceIntent=new Intent(TestActivity.this,BLEService.class);
        bindService(gattServiceIntent,mServiceConnection,BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }


    @Override
    protected void onResume() {
        if (send_timer == null)
        {
            send_timer.schedule(send_task,1000,50);
        }
        super.onResume();
    }

    protected void onDestroy ( ) {
        if (send_timer != null)
        {
            send_timer.cancel();
            send_timer = null;
        }
        super.onDestroy( );
    }




    @Override
    public void onClick(View v) {
        int id=v.getId();

        switch (id) {
            case R.id.btn_send_view: {
                String Tx;
                Tx = ed_send.getText().toString();
                mCharacteristic.setValue(Tx + "\n");
                mBluetoothLeService.writeCharacteristic(mCharacteristic);
                break;
            }
            case R.id.Update_BTN: {

//                byte[] PsetValue2Byte=float2byte(PsetValue);
//                byte[] IsetValue2Byte=float2byte(IsetValue);
//                byte[] DsetValue2Byte=float2byte(DsetValue);
//                byte[] data=new byte[19];

//                data[3]=' ';
//                data[4]='I';
//                data[5]=(byte)IsetValue;
//                data[6]=' ';
//                data[7]='D';
//                data[8]=(byte)DsetValue;
//                data[9]='\n';
//                InputStream inputStream=new ByteArrayInputStream(data);
//                inputStreamArrayList.add(inputStream);




                mCharacteristic.setValue("P:"+PsetValue+" I"+IsetValue);
                mBluetoothLeService.writeCharacteristic(mCharacteristic);
                mCharacteristic.setValue(" D"+DsetValue+"\n");
//                mCharacteristic.setValue("P:"+PsetValue2Byte+" I"+IsetValue2Byte+" D"+DsetValue2Byte+'\n');
                mBluetoothLeService.writeCharacteristic(mCharacteristic);

                Log.d("update","update");
                break;
            }
            case R.id.PPlus_BTN:{
                String PSetValueSTR;
                PSetValueSTR=PSet.getText().toString();
                PsetValue=Float.parseFloat(PSetValueSTR);
                PsetValue+=0.001;
                BigDecimal bigDecimal   =   new  BigDecimal(PsetValue);
                PsetValue=bigDecimal.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();
                PSet.setText(PsetValue+"");
                break;
            }
            case R.id.PMinus_BTN:{
                String PSetValueSTR;
                PSetValueSTR=PSet.getText().toString();
                PsetValue=Float.parseFloat(PSetValueSTR);
                PsetValue-=0.001;
                BigDecimal bigDecimal   =   new   BigDecimal(PsetValue);
                PsetValue=bigDecimal.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();
                PSet.setText(PsetValue+"");
                break;
            }
            case R.id.IPlus_BTN:{
                String ISetValueSTR;
                ISetValueSTR=ISet.getText().toString();
                IsetValue=Float.parseFloat(ISetValueSTR);
                IsetValue+=0.001;
                BigDecimal bigDecimal   =   new   BigDecimal(IsetValue);
                IsetValue=bigDecimal.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();
                ISet.setText(IsetValue+"");
                break;
            }
            case R.id.IMinus_BTN:{
                String ISetValueSTR;
                ISetValueSTR=ISet.getText().toString();
                IsetValue=Float.parseFloat(ISetValueSTR);
                IsetValue-=0.001;
                BigDecimal bigDecimal   =   new   BigDecimal(IsetValue);
                IsetValue=bigDecimal.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();
                ISet.setText(IsetValue+"");
                break;
            }
            case R.id.DPlus_BTN:{
                String DSetValueSTR;
                DSetValueSTR=DSet.getText().toString();
                DsetValue=Float.parseFloat(DSetValueSTR);
                DsetValue+=0.001;
                BigDecimal bigDecimal   =   new   BigDecimal(DsetValue);
                DsetValue=bigDecimal.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();
                DSet.setText(DsetValue+"");
                break;
            }
            case R.id.DMinus_BTN:{
                String DSetValueSTR;
                DSetValueSTR=DSet.getText().toString();
                DsetValue=Float.parseFloat(DSetValueSTR);
                DsetValue-=0.001;
                BigDecimal bigDecimal   =   new   BigDecimal(DsetValue);
                DsetValue=bigDecimal.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();
                DSet.setText(DsetValue+"");
                DSet.setText(DsetValue+"");
                break;
            }
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

    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }

        return dest;

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
//                mCharacteristic.setValue("service connect");
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
//                mCharacteristic.setValue("connect success");
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

//                    BLEService.rec_state = BLEService.RecState.WAIT_F;
                    String PData = intent.getStringExtra(BLEService.P_DATA);
                    if(PData!=null) PShow.setText("P: "+PData);
                    String IData = intent.getStringExtra(BLEService.I_DATA);
                    if(IData!=null)  IShow.setText("I: "+IData);
                    String DData = intent.getStringExtra(BLEService.D_DATA);
                    if(DData!=null) DShow.setText("D: "+DData);
                    String AngleData = intent.getStringExtra(BLEService.ANGLE_DATA);
                    if(AngleData!=null) AngleShow.setText("ANGLE: "+AngleData);
                    BLEService.RecIsDone = false;
                }
                Log.d("rec",Rx+" ");

            } else if(BLEService.ACTION_DATA_READ.equals(action)){

            }
        }
    };
}
