package com.example.jeremie.sembluez;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SendDataActivity extends Activity {

    private TextView bdev;

    private BluetoothDevice bluetoothDevice;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paired_devices);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            bluetoothDevice = extra.getParcelable("device");

        } else {
            finish();
        }

        bdev = (TextView)findViewById(R.id.bdev);
        bdev.setText(bluetoothDevice.getName());
        Toast.makeText(this,"ca marche", Toast.LENGTH_SHORT).show();


    }
}
