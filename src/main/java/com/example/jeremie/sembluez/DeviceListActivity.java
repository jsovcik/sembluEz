package com.example.jeremie.sembluez;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;


/*Cette Actiité pemet la gestion des device Bluettoth détectée, selon le contexte elle affichera soit 
   la liste des appreil découvert lors d'un scan, soit les appareil déà appareillés*/

public class DeviceListActivity extends Activity {
    /* Ces trois objets vous permettre de faire les liens entre la vue et les données recue du module BT du téléphone*/
    private ListView mListView;
    private DeviceListAdapter mAdapter;
    private ArrayList<BluetoothDevice> mDeviceList;
  
    private Button mSendBtn; //Un bouton qui nous permettra déclencher l'activité SendDAta

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paired_devices);

        //Lien entre les objets de la vue (layout) et les objets Java
        mDeviceList		= getIntent().getExtras().getParcelableArrayList("device.list");

        mListView		= (ListView) findViewById(R.id.lv_paired);
        mSendBtn 		= (Button) findViewById(R.id.btn_content);
        mAdapter		= new DeviceListAdapter(this);

        mAdapter.setData(mDeviceList);


        mListView.setAdapter(mAdapter);
        /*On s'abonne auprès du BrpdCastReceiver mPairReceiver avec pour être au courant du change d'état d'un appareil (APpeuré/Désappairé)*/
        registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        /*On créé un Listener pour notre objet DeviceListeAdapter pour la gestion des click button*/
        mAdapter.setListener(new DeviceListAdapter.OnPairButtonClickListener() {
            @Override
            public void onPairButtonClick(int position) {
                BluetoothDevice device = mDeviceList.get(position);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                   //Si Appeiré, on "Désappair"
                    unpairDevice(device);
                } else {
                    //Sinon, on Appaire ! 
                    showToast("Pairing...");

                    pairDevice(device);
                }
            }
        });
        //Ebregistrement auprès du BroadCastReceiver mSender
        registerReceiver(mSendReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        //On déclaire un Listerner sur l'objet mAdapter pour le click sur le bouton mSender
        mAdapter.setSendListener(new DeviceListAdapter.OnSendButtonClickListener() {
            @Override
            public void onSendButtonClick(int position) {
                //Si Click sur le button, on stock les données concernant l'objet Bluetooth courant et on les transfert à l'activité SendDataActivity et on la lnace
                BluetoothDevice bluetoothDevice = mDeviceList.get(position);
                Intent intent = new Intent(DeviceListActivity.this, SendDataActivity.class);
                intent.putExtra("device", bluetoothDevice);
                DeviceListActivity.this.startActivity(intent);

            }
        });

    }



    @Override
    public void onDestroy() {
        unregisterReceiver(mPairReceiver);

        super.onDestroy();
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state 		= intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                /*Message Toast pour indéiquer que l'appareil selectionné a été Appereiré ou supprimé des appreil connus*/
                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    showToast("Paired");
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    showToast("Unpaired");
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private final BroadcastReceiver mSendReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();


        }
    };



}
