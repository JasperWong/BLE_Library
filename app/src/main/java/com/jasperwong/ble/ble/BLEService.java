/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jasperwong.ble.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;

//import android.app.Activity;
//import android.bluetooth.BluetoothGattDescriptor;


public class BLEService extends Service
{
    private final static String TAG = BLEService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private String mBluetoothDeviceAddress;
    private int mConnectionState = STATE_DISCONNECTED;

    //private final int REQUEST_ENABLE_BT = 1;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_GATT_RSSI =
            "com.example.bluetooth.le.ACTION_GATT_RSSI";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_DATA_READ =
            "com.example.bluetooth.le.ACTION_DATA_READ";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String ACTION_DATA_WRITE =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    public final static UUID UUID_BLE_TX = UUID
            .fromString(SampleGattAttributes.BLE_TX);
    public final static UUID UUID_BLE_RX = UUID
            .fromString(SampleGattAttributes.BLE_RX);
    public final static UUID UUID_BLE_SERVICE = UUID
            .fromString(SampleGattAttributes.BLE_SERVICE);
    public final static String P_DATA =
            "com.example.bluetooth.le.P_DATA";
    public final static String I_DATA =
            "com.example.bluetooth.le.I_DATA";
    public final static String D_DATA =
            "com.example.bluetooth.le.D_DATA";
    public final static String ANGLE_DATA =
            "com.example.bluetooth.le.ANGLE_DATA";


    public  enum  RecState{
        WAIT_START,
        WAIT_I,
        WAIT_P,
        WAIT_COLON,
        WAIT_COLON_I,
        WAIT_P_DATA,
        WAIT_D,
        WAIT_I_DATA,
        WAIT_A,
        WAIT_D_DATA,
        WAIT_ANGLE_DATA,
        WAIT_NEWLINE,
        PARSE_PENDING;
    };

    private String PValueStr;
    private String IValueStr;
    private String DValueStr;
    private String AngleValueStr;


    public static RecState rec_state=RecState.WAIT_P;

    public static boolean RecIsDone=false;

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
        {
            Log.d(TAG, "onReadRemoteRssi");
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_GATT_RSSI, rssi);
            }
            else
            {
                Log.w(TAG, "onReadRemoteRssi received: " + status);
            }
        };

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }
            else
            {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            Log.d("action","onRec");
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_DATA_READ, characteristic);
            }
            else
            {
                Log.w(TAG, "onCharacteristicRead: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            Log.d("action123","onWrite");
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_DATA_WRITE, characteristic);
            }
            else
            {
                Log.w(TAG, "onCharacteristicWrite: " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            Log.d("action123","onChange");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action)
    {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, int rssi)
    {
        Log.d(TAG, "broadcastUpdate - rssi");
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, String.valueOf(rssi));
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic)
    {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        String PData;
        String IData;
        String DData;
        final StringBuilder P_SB=new StringBuilder();
        final StringBuilder I_SB=new StringBuilder();
        final StringBuilder D_SB=new StringBuilder();
        final StringBuilder A_SB=new StringBuilder();
        if (UUID_BLE_RX.equals(characteristic.getUuid())) {
//        Log.d("usart",data+"");
            if (!RecIsDone && data != null && data.length > 0) {
                for (byte byteChar : data) {
                    switch (rec_state) {
                        case WAIT_P: {
                            if (byteChar == 'P') rec_state = RecState.WAIT_COLON;
                            else if (byteChar == 'I') rec_state = RecState.WAIT_COLON_I;
                            break;
                        }
                        case WAIT_COLON: {
                            if (byteChar == ':') {
                                P_SB.delete(0, P_SB.length());
                                D_SB.delete(0, D_SB.length());
//                                REC_WHAT = REC_FRONT;
                                rec_state = RecState.WAIT_P_DATA;
                            }
                            break;
                        }
                        case WAIT_P_DATA: {
                            if (byteChar == ' ') rec_state = RecState.WAIT_D;
                            else P_SB.append(String.format("%c", byteChar));
                            break;
                        }
                        case WAIT_D: {
                            if (byteChar == 'D') rec_state = RecState.WAIT_D_DATA;
                            else rec_state = RecState.WAIT_D;
                            break;
                        }
                        case WAIT_D_DATA: {
                            if (byteChar =='\n'||byteChar=='\r') {
                                PValueStr=P_SB.toString();
                                DValueStr=D_SB.toString();
                                intent.putExtra(P_DATA,PValueStr);
                                intent.putExtra(D_DATA,DValueStr);
                                rec_state = RecState.PARSE_PENDING;
                            }
                            else D_SB.append(String.format("%c", byteChar));
                            break;
                        }


                        case PARSE_PENDING: {
                            RecIsDone = true;
//                        Log.d("rx_init","front:"+FrontDistance+" "+"left:"+LeftDistance+" "+"right:"+RightDistance);
                            rec_state = RecState.WAIT_P;
                            break;
                        }

                        case WAIT_COLON_I:{
                            I_SB.delete(0,I_SB.length());
                            A_SB.delete(0,A_SB.length());
                            if(byteChar==':') rec_state=RecState.WAIT_I_DATA;
                            else ;
                            break;
                        }
                        case WAIT_I_DATA:{
                            if(byteChar==' ') rec_state=RecState.WAIT_A;
                            else I_SB.append(String.format("%c",byteChar));
                        }
                        case WAIT_A:{
                            if(byteChar=='A') rec_state=RecState.WAIT_ANGLE_DATA;
                            else;
                            break;
                        }
                        case WAIT_ANGLE_DATA:{
                            if(byteChar=='\n'||byteChar=='\r') {
                                IValueStr=I_SB.toString();
                                AngleValueStr=A_SB.toString();
                                intent.putExtra(I_DATA,IValueStr);
                                intent.putExtra(ANGLE_DATA,AngleValueStr);
                                rec_state=RecState.PARSE_PENDING;
                            }
                            else A_SB.append(String.format("%c",byteChar));
                            break;
                        }

                        default: {

                            break;
                        }
                    }


                }

            }
            intent.putExtra(EXTRA_DATA, new String(data));
            Log.d("usart", new String(data));
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder
    {
        public BLEService getService()
        {
            return BLEService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();


    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize()
    {
        if (mBluetoothManager == null)
        {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null)
            {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }


        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
        {
            Log.e(TAG, "Unable to get Bluetooth Adapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address)
    {
        if (mBluetoothAdapter == null || address == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }


        // Previously connected device.  Try to reconnect.
        if (
                mBluetoothDeviceAddress != null
                        && address.equals(mBluetoothDeviceAddress)
                        && mBluetoothGatt != null)
        {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect())
            {
                mConnectionState = STATE_CONNECTING;
                return true;
            }
            else
            {
                final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
                mBluetoothDeviceAddress = address;
                Log.d(TAG, "Connection failed");
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null)
        {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect()
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given util.BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close()
    {
        if (mBluetoothGatt == null)
        {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }


    public void readRssi()
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readRemoteRssi();
    }

    /**
     * Write to a given char
     *
     * @param characteristic The characteristic to write to
     */
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled)
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (UUID_BLE_RX.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
            UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }


    public void getCharacteristicDescriptor(BluetoothGattDescriptor descriptor)
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        mBluetoothGatt.readDescriptor(descriptor);
    }


    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */

    public List<BluetoothGattService> getSupportedGattServices()
    {
        if (mBluetoothGatt == null)
        {
            Log.d(TAG, "getSupportedGattService: mBluetoothGatt == null");
            return null;
        }

        return mBluetoothGatt.getServices();
    }


}