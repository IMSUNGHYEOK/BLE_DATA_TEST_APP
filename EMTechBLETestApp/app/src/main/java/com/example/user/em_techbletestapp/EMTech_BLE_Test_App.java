package com.example.user.em_techbletestapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EMTech_BLE_Test_App extends AppCompatActivity implements View.OnClickListener {

    private int boxnum = 0;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBLEScanner;
    private BluetoothManager mBluetoothManager;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    private Handler mHander;
    private static final long SCAN_PERIOD = 1500;

    private Button btn_Scan;
    private Button btn_Connect;

    boolean scanning =false;

    private ListView ScanList;
    BleList bleList = null;

    private String Connect_Device_Name;
    private String Connect_Device_Address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emtech__ble__test__app);

        ActivityCompat.requestPermissions(this,                                                         // 앱 실행시 블루투스 권한 요청
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        btn_Scan = (Button)findViewById(R.id.btn_Scan);
        btn_Scan.setOnClickListener(this);
        btn_Connect = (Button)findViewById(R.id.btn_Connect);
        btn_Connect.setOnClickListener(this);

        bleList = new BleList();
        ScanList = (ListView)findViewById(R.id.layout_ScanList);
        ScanList.setAdapter(bleList);

        mHander = new Handler();


    }

    @Override
    protected void onStart() {
        super.onStart();

        BluetoothCheck(mBluetoothAdapter);
        Log.d("Scan", "Start scanning");
        scanLeDevice(true);
        Log.d("Scan", "End Scanning");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Scan:                                                                                 // BLE device 스캔 버튼
                BluetoothCheck(mBluetoothAdapter);
                Log.d("Scan", "Start scanning");
                scanLeDevice(true);
                Log.d("Scan", "End Scanning");
                break;
            case R.id.btn_Connect:                                                                              //BLE device 연결 버튼
                Intent intent = new Intent(getApplicationContext(), Connect_Test_App.class);
                if(Connect_Device_Name==null)
                {Toast.makeText(EMTech_BLE_Test_App.this, "Device를 선택하세요", Toast.LENGTH_SHORT).show(); break;}
                intent.putExtra("Device_Name",Connect_Device_Name);
                intent.putExtra("Device_Address", Connect_Device_Address);
                startActivity(intent);
                break;
        }
    }

    private void BluetoothCheck(BluetoothAdapter mBluetoothAdapter){                                            // 블루투스 스캔 및 권한 확인
        if(mBluetoothAdapter == null)
        {
            finish();
        }else{
            if(!mBluetoothAdapter.isEnabled())
            {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(i);
            }
        }
    }


    private void scanLeDevice(final boolean enable)
    {
        if(enable){
            mHander.postDelayed(new Runnable() {
                @Override
                public void run() {                                                                         //SCAN_PERIOD 값 만큼 SCAN 후 SCAN 종료
                    scanning = false;
                    mBluetoothAdapter.stopLeScan(leScanCallback);
                    Log.d("Scan", "stop");
                }
            }, SCAN_PERIOD);

            scanning = true;
            mBluetoothAdapter.startLeScan(leScanCallback);
            Log.d("Scan", "START");
        }else{
            scanning = false;
            mBluetoothAdapter.stopLeScan(leScanCallback);
            Log.d("Scan", "stop");
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {           // BLE device 스캔 후 동작
            Log.d("Scan ",device.getName() + " Rssi : " + rssi + " Record : " + scanRecord);

            runOnUiThread(new Runnable() {
                public void run() {
                    if(device.getName() != null){
                        if(device.getName().toString().length() == 0)
                            bleList.notifyDataSetChanged();
                    else{
                            bleList.addDevice(device, rssi);
                            bleList.notifyDataSetChanged(); }
                    } //List에 디바이스 추가
                }
            });
        }
    };

    private class BleList extends BaseAdapter                                                                  // 리스트 뷰에 기기목록 추가
    {
        private ArrayList<BluetoothDevice> devices;
        private ArrayList<CheckBox> checkboxes;
        private LayoutInflater inflater;
        private int Rssi;

        public BleList(){
            super();
            devices = new ArrayList<BluetoothDevice>();
            checkboxes = new ArrayList<CheckBox>();
            Rssi = 0;
            inflater = ((Activity) EMTech_BLE_Test_App.this).getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device, int rssi){
            if(!devices.contains(device)){
                Connect_Test_App.Device = device;
                devices.add(device);
                Rssi = rssi;
            }
        }

        public void clear(){
            devices.clear();
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.scan_list_layout,null);
                viewHolder.deviceChk = (CheckBox) convertView.findViewById(R.id.ble_Chk);
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.ble_Name);
                viewHolder.deviceRssi = (TextView) convertView.findViewById(R.id.ble_Rssi);
                convertView.setTag(viewHolder);

                if (viewHolder.deviceRssi.getText() != null)
                    viewHolder.deviceRssi.setText("Rssi : " + Rssi);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final String deviceName = devices.get(position).getName();

            if(deviceName != null && deviceName.length()>0) {
                //checkboxes.add(viewHolder.deviceChk);
                if(checkboxes.size()<devices.size()) {
                    for(int i =0; i<devices.size()-checkboxes.size()+1; i++)
                    {checkboxes.add(viewHolder.deviceChk);}
                }
                viewHolder.deviceChk.setClickable(false);
                viewHolder.deviceChk.setFocusable(false);
                viewHolder.deviceName.setText(deviceName);

            }  else{
                viewHolder.deviceName.setText("Unknown");
            }

            ScanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    checkboxes.get(boxnum).setChecked(false);
                    Connect_Device_Name = devices.get(position).getName();
                    Connect_Device_Address = devices.get(position).getAddress();
                    checkboxes.get(position).setChecked(true);
                    boxnum = position;
                }
            });
            return convertView;
        }

    }

    static class ViewHolder {
        TextView deviceName;
        CheckBox deviceChk;
        TextView deviceRssi;
    }

}
