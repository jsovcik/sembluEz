package com.example.jeremie.sembluez;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class SendDataActivity extends Activity {

    private Button fileButton;
    private Button send;
    private InputStream inStream;
    private OutputStream outputStream;
    private ByteArrayOutputStream byteArrayOutputStream;


    private static final int READ_REQUEST_CODE = 42;


    private BluetoothDevice bluetoothDevice;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_data);

        fileButton = (Button)   findViewById(R.id.btn_content);
        send       = (Button)   findViewById(R.id.send);

        Intent intent = getIntent();
        Bundle extra = intent.getExtras();
        if (extra != null) {
            bluetoothDevice = extra.getParcelable("device");
            Toast.makeText(this,bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }

        if (fileButton != null){
            fileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
                    // browser.
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                    // Filter to only show results that can be "opened", such as a
                    // file (as opposed to a list of contacts or timezones)
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    // Filter to show only images, using the image MIME data type.
                    // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                    // To search for all documents available via installed storage providers,
                    // it would be "*/*".
                    intent.setType("image/*");

                    startActivityForResult(intent, READ_REQUEST_CODE);

                }
            });
        }



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                UUID uuid = bluetoothDevice.getUuids()[0].getUuid();
                try {
                    BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                    bluetoothSocket.connect();
                    outputStream = bluetoothSocket.getOutputStream();
                    inStream = bluetoothSocket.getInputStream();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                showImage(uri);
            }
        }
    }
}
