package com.jasperwong.ble.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.jasperwong.ble.R;

import java.util.List;

/**
 * Created by JasperWong on 2016/5/3.
 */
public class DeviceAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private List<BluetoothDevice> mData;
    private AdapterView.OnItemClickListener mListener;
    public DeviceAdapter(Context context){ mInflater = LayoutInflater.from(context); }

    public void setData(List<BluetoothDevice> data){
        mData=data;
    }

    @Override
    public int getCount() {
        return (mData == null)?0:mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView==null){
            convertView=mInflater.inflate(R.layout.list_device,null);
            viewHolder=new ViewHolder();
            viewHolder.addressTV=(TextView) convertView.findViewById(R.id.device_address);
            viewHolder.nameTV=(TextView) convertView.findViewById(R.id.device_name);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder) convertView.getTag();
        }

        BluetoothDevice device =mData.get(position);
        viewHolder.addressTV.setText(device.getAddress());
        viewHolder.nameTV.setText(device.getName());
        return convertView;
    }

    static class ViewHolder
    {
        TextView nameTV;
        TextView addressTV;
    }

    public void addDevice(BluetoothDevice device) {
        if(!mData.contains(device)) {
            mData.add(device);
        }
    }


}
