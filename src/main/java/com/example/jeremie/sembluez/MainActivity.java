package com.example.jeremie.sembluez;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mStatusTv;
    private Button mActivateBtn;
    private Button mPairedBtn;
    private Button mScanBtn;

    private ProgressDialog mProgressDlg;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mStatusTv 			= (TextView) findViewById(R.id.tv_status);
        mActivateBtn 		= (Button) findViewById(R.id.btn_enable);
        mPairedBtn 			= (Button) findViewById(R.id.btn_view_paired);
        mScanBtn 			= (Button) findViewById(R.id.btn_scan);

        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();

        mProgressDlg 		= new ProgressDialog(this);

        mProgressDlg.setMessage("Scanning...");
        mProgressDlg.setCancelable(false);
    }
}
