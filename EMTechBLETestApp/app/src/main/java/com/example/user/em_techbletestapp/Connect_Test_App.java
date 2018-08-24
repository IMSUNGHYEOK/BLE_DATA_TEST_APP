package com.example.user.em_techbletestapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Connect_Test_App extends Activity implements View.OnClickListener{
    private final static String TAG = EMTech_BLE_Test_App.class.getSimpleName();
    private TextView txt_Device;
    private Button btn_Send;

    private EditText edit_Write1;
    private EditText edit_Write2;
    private EditText edit_Write3;
    private EditText edit_Write4;
    private EditText edit_Write5;
    private EditText edit_Write6;
    private EditText edit_Write7;
    private EditText edit_Write8;
    private EditText edit_Write9;
    private EditText edit_Write10;
    private EditText edit_Write11;
    private EditText edit_Write12;
    private EditText edit_Write13;
    private EditText edit_Write14;
    private EditText edit_Write15;
    private EditText edit_Write16;
    private EditText edit_Write17;



    private String Device_Name;
    private String Device_Address;
    private String Device_UUID;

    private TextView mConnectionState;
    public static TextView mDataField;

    private Spinner mGattServiceList;
    private ExpandableListView mGattServicesList;

    private Spinner mGattWriteList;
    private Spinner mGattReadList;

    public BluetoothGattService serviceCH;
    public BluetoothGattCharacteristic writeCH;
    public BluetoothGattCharacteristic readCH = null;

    public  BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    ArrayList< String> spn_Service;
    private ArrayList<String> spn_Write = null;
    private ArrayList<String> spn_Read = null;
    private ArrayList<String> arySpinner;

    public static BluetoothDevice Device;
    private BluetoothAdapter mBluetoothAdapter;
    public BluetoothGatt mBluetoothGatt = null;
    public BluetoothGattCharacteristic mBluetoothGattWCha = null;
    public BluetoothGattCharacteristic mBluetoothGattRCha = null;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d("CONNECT", "initialize");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            mBluetoothLeService.connect(Device_Address);
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
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                txt_Device.setText(Device_Name + "과 연결됨");
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                txt_Device.setText(Device_Name + "과 연결되지않음");
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);

                displayData(data);
                Log.i("Receive",byteArrayToHex(BluetoothLeService.EXTRA_DATA.getBytes()) + " " + BluetoothLeService.EXTRA_DATA.getBytes());
                data = null;
            }
        }
    };
    String str(byte[] data){
        String _str = new String(data);
        return _str;
    }
    private void clearUI() {
        mGattServiceList.setAdapter((SpinnerAdapter) null);
        mGattReadList.setAdapter((SpinnerAdapter) null);
        mGattWriteList.setAdapter((SpinnerAdapter) null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_layout);
        Intent intent = getIntent();

        Device_Name = intent.getStringExtra("Device_Name");
        Device_Address = intent.getStringExtra("Device_Address");

        txt_Device = (TextView)findViewById(R.id.txt_Device);
        txt_Device.setText(Device_Name);
        btn_Send = (Button)findViewById(R.id.btn_Send);
        btn_Send.setOnClickListener(this);

        edit_Write1 = (EditText) findViewById(R.id.edit_Write1);
        edit_Write1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write1.length()==2)
                    edit_Write2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write2 = (EditText) findViewById(R.id.edit_Write2);
        edit_Write2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write2.length()==2)
                    edit_Write3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write3 = (EditText) findViewById(R.id.edit_Write3);
        edit_Write3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write3.length()==2)
                    edit_Write4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write4 = (EditText) findViewById(R.id.edit_Write4);
        edit_Write4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write4.length()==2)
                    edit_Write5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write5 = (EditText) findViewById(R.id.edit_Write5);
        edit_Write5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write5.length()==2)
                    edit_Write6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write6 = (EditText) findViewById(R.id.edit_Write6);
        edit_Write6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write6.length()==2)
                    edit_Write7.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write7 = (EditText) findViewById(R.id.edit_Write7);
        edit_Write7.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write7.length()==2)
                    edit_Write8.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write8 = (EditText) findViewById(R.id.edit_Write8);
        edit_Write8.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write8.length()==2)
                    edit_Write9.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write9 = (EditText) findViewById(R.id.edit_Write9);
        edit_Write9.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write9.length()==2)
                    edit_Write10.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write10 = (EditText) findViewById(R.id.edit_Write10);
        edit_Write10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write10.length()==2)
                    edit_Write11.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write11 = (EditText) findViewById(R.id.edit_Write11);
        edit_Write11.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write11.length()==2)
                    edit_Write12.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write12 = (EditText) findViewById(R.id.edit_Write12);
        edit_Write12.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write12.length()==2)
                    edit_Write13.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write13 = (EditText) findViewById(R.id.edit_Write13);
        edit_Write13.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write13.length()==2)
                    edit_Write14.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write14 = (EditText) findViewById(R.id.edit_Write14);
        edit_Write14.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write14.length()==2)
                    edit_Write15.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write15 = (EditText) findViewById(R.id.edit_Write15);
        edit_Write15.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write15.length()==2)
                    edit_Write16.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write16 = (EditText) findViewById(R.id.edit_Write16);
        edit_Write16.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edit_Write16.length()==2)
                    edit_Write17.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_Write17 = (EditText) findViewById(R.id.edit_Write17);

        mDataField = (TextView) findViewById(R.id.data_value);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        mGattServiceList = (Spinner)findViewById(R.id.Service_Device);
        mGattWriteList = (Spinner)findViewById(R.id.Write_Device);
        mGattReadList = (Spinner)findViewById(R.id.Read_Device);

        spn_Service = new ArrayList< String>();
        spn_Write = new ArrayList<String>();
        spn_Read = new ArrayList<String>();

        mGattWriteList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spn_Write));
        mGattReadList.setAdapter(new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, spn_Read));


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(Device_Address);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    public static byte[] hexStringToByteArray(String str){
        int len = str.length();
        byte[] data = new byte[len/2];
        for(int i=0; i<len; i+=2)
        {
            data[i/2] = (byte) ((Character.digit(str.charAt(i), 16) << 4)
                    + Character.digit(str.charAt(i+1), 16));
        }
        return data;
    }

    public void plusZero()
    {
        if(edit_Write1.length()<2 && edit_Write1.length()!=0)
            edit_Write1.setText("0"+edit_Write1.getText().toString());
        if(edit_Write2.length()<2&& edit_Write2.length()!=0)
            edit_Write2.setText("0"+edit_Write2.getText().toString());
        if(edit_Write3.length()<2&& edit_Write3.length()!=0)
            edit_Write3.setText("0"+edit_Write3.getText().toString());
        if(edit_Write4.length()<2&& edit_Write4.length()!=0)
            edit_Write4.setText("0"+edit_Write4.getText().toString());
        if(edit_Write5.length()<2&& edit_Write5.length()!=0)
            edit_Write5.setText("0"+edit_Write5.getText().toString());
        if(edit_Write6.length()<2&& edit_Write6.length()!=0)
            edit_Write6.setText("0"+edit_Write6.getText().toString());
        if(edit_Write7.length()<2&& edit_Write7.length()!=0)
            edit_Write7.setText("0"+edit_Write7.getText().toString());
        if(edit_Write8.length()<2&& edit_Write8.length()!=0)
            edit_Write8.setText("0"+edit_Write8.getText().toString());
        if(edit_Write9.length()<2&& edit_Write9.length()!=0)
            edit_Write9.setText("0"+edit_Write9.getText().toString());
        if(edit_Write10.length()<2&& edit_Write10.length()!=0)
            edit_Write10.setText("0"+edit_Write10.getText().toString());
        if(edit_Write11.length()<2&& edit_Write11.length()!=0)
            edit_Write11.setText("0"+edit_Write11.getText().toString());
        if(edit_Write12.length()<2&& edit_Write12.length()!=0)
            edit_Write12.setText("0"+edit_Write12.getText().toString());
        if(edit_Write13.length()<2&& edit_Write13.length()!=0)
            edit_Write13.setText("0"+edit_Write13.getText().toString());
        if(edit_Write14.length()<2&& edit_Write14.length()!=0)
            edit_Write14.setText("0"+edit_Write14.getText().toString());
        if(edit_Write15.length()<2&& edit_Write15.length()!=0)
            edit_Write15.setText("0"+edit_Write15.getText().toString());
        if(edit_Write16.length()<2 && edit_Write16.length()!=0)
            edit_Write16.setText("0"+edit_Write16.getText().toString());
        if(edit_Write17.length()<2 && edit_Write17.length()!=0)
            edit_Write17.setText("0"+edit_Write17.getText().toString());
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_Send:

                plusZero(); // 한자리 값 0x0 + 값
                Log.i("SEND MSG",hexStringToByteArray(edit_Write1.getText().toString()+
                        edit_Write2.getText().toString() + edit_Write3.getText().toString() + edit_Write4.getText().toString()+
                        edit_Write5.getText().toString() + edit_Write6.getText().toString() + edit_Write7.getText().toString()+
                        edit_Write8.getText().toString() + edit_Write9.getText().toString() + edit_Write10.getText().toString()+
                        edit_Write11.getText().toString() + edit_Write12.getText().toString() + edit_Write13.getText().toString()+
                        edit_Write14.getText().toString() + edit_Write15.getText().toString() + edit_Write16.getText().toString()+
                        edit_Write17.getText().toString()).toString());
                //write
                SendData(mGattReadList.getSelectedItem().toString(),
                        hexStringToByteArray(edit_Write1.getText().toString()+
                        edit_Write2.getText().toString() + edit_Write3.getText().toString() + edit_Write4.getText().toString()+
                        edit_Write5.getText().toString() + edit_Write6.getText().toString() + edit_Write7.getText().toString()+
                        edit_Write8.getText().toString() + edit_Write9.getText().toString() + edit_Write10.getText().toString()+
                        edit_Write11.getText().toString() + edit_Write12.getText().toString() + edit_Write13.getText().toString()+
                        edit_Write14.getText().toString() + edit_Write15.getText().toString() + edit_Write16.getText().toString()+
                        edit_Write17.getText().toString()));

                //read
                final BluetoothGattCharacteristic characteristic = readCH;
                final int charaProp = characteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0){
                    if(mNotifyCharacteristic != null){
                        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                    }
                    mBluetoothLeService.readCharacteristic(characteristic);
                    if((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0){
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                    }
                }

                ReceiveData(mGattReadList.getSelectedItem().toString());

                break;
        }
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(byte[] data) {
        String str = byteArrayToHex(data);
        Log.i("smsms","recv data : " + str);

        if (str != null) {
            mDataField.setText("   Data : " + str);
        }
    }

    private void displayGattServices(final List<BluetoothGattService> gattServices) {

        if (gattServices == null) return;
        String uuid = null;

        final HashMap<String,ArrayList<String>> gatt = new HashMap<String, ArrayList<String>>();
        final ArrayList<BluetoothGattService> mGattServices = new ArrayList<BluetoothGattService>();
        final ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        final ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_UUID, uuid);
            spn_Service.add(currentServiceData.get(LIST_UUID));
            gattServiceData.add(currentServiceData);
            mGattServices.add(gattService);

            String currentService = uuid;
            ArrayList<String> gattServCha = new ArrayList<>();


            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();

                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

                gattServCha.add(uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gatt.put(currentService, gattServCha);
            //gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        mGattServiceList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //개별 read write uuid 등록
                spn_Read.clear();
                spn_Write.clear();
                String Serv = mGattServiceList.getSelectedItem().toString();
                serviceCH = mGattServices.get(position);
                spn_Write=gatt.get(Serv);
                spn_Read=gatt.get(Serv);
                refresh2();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        mGattServiceList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spn_Service));
        mGattReadList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spn_Read));
        mGattWriteList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spn_Write));

    }
    private void refresh()
    {
        mGattReadList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spn_Read));
        mGattWriteList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spn_Write));

        mGattReadList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                for(int i =0; i< mGattCharacteristics.size(); i++) {
                    for(int j =0; j<1; j++){
                        if (mGattReadList.getSelectedItem().toString().equals(mGattCharacteristics.get(i).get(j).getUuid().toString())) {
                            readCH = mGattCharacteristics.get(i).get(j);
                            mBluetoothLeService.setReadCH(readCH);
                            BluetoothLeService.readCH = readCH;}
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        mGattWriteList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                for(int i =0; i< mGattCharacteristics.size(); i++) {
                    for(int j =0; j<1; j++){
                        if (mGattWriteList.getSelectedItem().toString().equals(mGattCharacteristics.get(i).get(j).getUuid().toString())) {
                            writeCH = mGattCharacteristics.get(i).get(j);
                            mBluetoothLeService.setWriteCH(writeCH);
                            BluetoothLeService.writeCH = writeCH;}
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { return;}
        });
    }

    private void refresh2()
    {
        mGattReadList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spn_Read));
        mGattWriteList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spn_Write));

        mGattReadList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                for(int i =0; i< mGattCharacteristics.size(); i++) {
                    for(int j =0; j<mGattCharacteristics.get(i).size(); j++){
                        if (mGattReadList.getSelectedItem().toString().equals(mGattCharacteristics.get(i).get(j).getUuid().toString())) {
                            readCH = mGattCharacteristics.get(i).get(j);
                            mBluetoothLeService.setReadCH(readCH);
                            BluetoothLeService.readCH = readCH;
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        mGattWriteList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                for(int i =0; i< mGattCharacteristics.size(); i++) {
                    for(int j =0; j<mGattCharacteristics.get(i).size(); j++){
                        if (mGattWriteList.getSelectedItem().toString().equals(mGattCharacteristics.get(i).get(j).getUuid().toString())) {
                            writeCH = mGattCharacteristics.get(i).get(j);
                            mBluetoothLeService.setWriteCH(writeCH);
                            BluetoothLeService.writeCH = writeCH;
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { return;}
        });
    }

    private void intent(Intent intent)
    {
        intent.putExtra("Service_UUID",mGattServiceList.getSelectedItem().toString());
        intent.putExtra("Write_UUID",mGattReadList.getSelectedItem().toString());
        intent.putExtra("Read_UUID",mGattWriteList.getSelectedItem().toString());
        Log.i("Cha",intent.toString());
        startActivity(intent);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void SendData(String value, byte[] data)
    {
        Log.i("Receive", data.toString());
        mBluetoothGatt = mBluetoothLeService.mBluetoothGatt;
        mBluetoothGattWCha = writeCH;

        mBluetoothLeService.writeCharacteristic(mBluetoothGattWCha, data);
    }

    private void ReceiveData(String value)
    {
        mBluetoothGattRCha = readCH;
        mBluetoothLeService.readCharacteristic(mBluetoothGattRCha);
    }

    String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x ", b&0xff));
        return sb.toString();
    }

}
