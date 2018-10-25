package com.example.jeremie.sembluez;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.TextView;

public class SendDataActivity extends Activity {

    private TextView bdev;

    private BluetoothDevice bluetoothDevice;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paired_devices);

        bluetoothDevice = (BluetoothDevice) getIntent().getExtras().get("device");

        bdev = (TextView)findViewById(R.id.bdev);
        bdev.setText(bluetoothDevice.getName());

        
    }
}
