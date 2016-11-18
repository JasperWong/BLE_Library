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

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class GATTUtils
{
    private static final String TAG = GATTUtils.class.getSimpleName();
    private static HashMap<String, String> attributes = new HashMap<String, String>();

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String HM_10_CONF = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String BLE_TX = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String BLE_RX = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String BLE_SERVICE = "713d0000-503e-4c75-ba94-3148f18d941e";
    static {
        // Sample Services.
        attributes.put("0000fff0-0000-1000-8000-00805f9b34fb", "基于自定义通信协议的蓝牙服务");
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access Profile Service");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute Profile Service");

        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put("0000ffe1-0000-1000-8000-00805f9b34fb", "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("0000fff1-0000-1000-8000-00805f9b34fb", "第一特征值");
        attributes.put("0000fff2-0000-1000-8000-00805f9b34fb", "第二特征值");
        attributes.put("0000fff3-0000-1000-8000-00805f9b34fb", "第三特征值");
        attributes.put("0000fff4-0000-1000-8000-00805f9b34fb", "第四特征值");
        attributes.put("0000fff5-0000-1000-8000-00805f9b34fb", "第五特征值");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    private static HashMap<Integer, String> serviceTypes = new HashMap();
    static {
        // Sample Services.
    	serviceTypes.put(BluetoothGattService.SERVICE_TYPE_PRIMARY, "PRIMARY");
    	serviceTypes.put(BluetoothGattService.SERVICE_TYPE_SECONDARY, "SECONDARY");
    }
    
    public static String getServiceType(int type){
    	return serviceTypes.get(type);
    }
    

    //-------------------------------------------    
    private static HashMap<Integer, String> charPermissions = new HashMap();
    static {
    	charPermissions.put(0, "UNKNOW");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ, "READ");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED, "READ_ENCRYPTED");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM, "READ_ENCRYPTED_MITM");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE, "WRITE");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED, "WRITE_ENCRYPTED");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM, "WRITE_ENCRYPTED_MITM");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED, "WRITE_SIGNED");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM, "WRITE_SIGNED_MITM");
    }
    
    public static String getCharPermission(int permission){
    	return getHashMapValue(charPermissions,permission);
    }
    //-------------------------------------------    
    private static HashMap<Integer, String> charProperties = new HashMap();
    static {
    	
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_BROADCAST, "BROADCAST");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS, "EXTENDED_PROPS");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_INDICATE, "INDICATE");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_NOTIFY, "NOTIFY");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_READ, "READ");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE, "SIGNED_WRITE");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE, "WRITE");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE, "WRITE_NO_RESPONSE");
    }
    
    public static String getCharPropertie(int property){
    	return getHashMapValue(charProperties,property);
    }
    
    //--------------------------------------------------------------------------
    private static HashMap<Integer, String> descPermissions = new HashMap();
    static {
    	descPermissions.put(0, "UNKNOW");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ, "READ");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED, "READ_ENCRYPTED");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM, "READ_ENCRYPTED_MITM");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE, "WRITE");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED, "WRITE_ENCRYPTED");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM, "WRITE_ENCRYPTED_MITM");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED, "WRITE_SIGNED");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM, "WRITE_SIGNED_MITM");
    }
    
    public static String getDescPermission(int property){
    	return getHashMapValue(descPermissions,property);
    }
    
    
    private static String getHashMapValue(HashMap<Integer, String> hashMap,int number){
    	String result =hashMap.get(number);
    	if(TextUtils.isEmpty(result)){
    		List<Integer> numbers = getElement(number);
    		result="";
    		for(int i=0;i<numbers.size();i++){
    			result+=hashMap.get(numbers.get(i))+"|";
    		}
    	}
    	return result;
    }

    /**
     * 位运算结果的反推函数10 -> 2 | 8;
     */
    static private List<Integer> getElement(int number){
    	List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < 32; i++){
            int b = 1 << i;
            if ((number & b) > 0) 
            	result.add(b);
        }
        
        return result;
    }
    
    
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < src.length; i++) {  
            int v = src[i] & 0xFF;  
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
            stringBuilder.append(hv);  
        }  
        return stringBuilder.toString();  
    }


    public static BluetoothGattCharacteristic lookupGattServices(List<BluetoothGattService> gattServices, String uuid)
    {
        if (gattServices == null) return null;

        for (BluetoothGattService gattService : gattServices)
        {
            //-----Service的字段信息-----//
            int type = gattService.getType();
            Log.e(TAG, "-->service type:" + GATTUtils.getServiceType(type));
            Log.e(TAG, "-->includedServices size:" + gattService.getIncludedServices().size());
            Log.e(TAG, "-->service uuid:" + gattService.getUuid());

            //-----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                Log.e(TAG, "---->char uuid:" + gattCharacteristic.getUuid());

                int permission = gattCharacteristic.getPermissions();
                Log.e(TAG, "---->char permission:" + GATTUtils.getCharPermission(permission));

                int property = gattCharacteristic.getProperties();
                Log.e(TAG, "---->char property:" + GATTUtils.getCharPropertie(property));

                byte[] data = gattCharacteristic.getValue();
                if (data != null && data.length > 0)
                {
                    Log.e(TAG, "---->char value:" + new String(data));
                }

                //UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
                if (gattCharacteristic.getUuid().toString().equals(uuid))
                {
                    return gattCharacteristic;
                }

                //-----Descriptors的字段信息-----//
                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor gattDescriptor : gattDescriptors)
                {
                    Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
                    int descPermission = gattDescriptor.getPermissions();
                    Log.e(TAG, "-------->desc permission:" + GATTUtils.getDescPermission(descPermission));

                    byte[] desData = gattDescriptor.getValue();
                    if (desData != null && desData.length > 0)
                    {
                        Log.e(TAG, "-------->desc value:" + new String(desData));
                    }
                }
            }
        }
        return null;
    }
}
