package com.example.jeremie.sembluez;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class SendDataActivity extends Activity {

    private Button fileButton;
    private Button send;
    private ImageView imageView;

    private InputStream inStream;
    private OutputStream outputStream;
    private ByteArrayOutputStream byteArrayOutputStream;
    private Uri dataUri;
    private InputStream iStream;


    private static final int READ_REQUEST_CODE = 42;


    private BluetoothDevice bluetoothDevice;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_data);

        fileButton = (Button)   findViewById(R.id.btn_content);
        send       = (Button)   findViewById(R.id.send);
        imageView  = (ImageView)findViewById(R.id.imageView);

        // Récupération des informations sur le device passées dans l'intent
        Intent intent = getIntent();
        Bundle extra = intent.getExtras();
        if (extra != null) {
            bluetoothDevice = extra.getParcelable("device");
            Toast.makeText(this,bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }


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




        // Action déclenché par l'action du boutton d'envoi de l'image sélectionné.
        // C'est la fonction déféctueuse.
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Récuperation de l'UUID du device
                ParcelUuid[] parcelUuid = bluetoothDevice.getUuids();
                UUID uuid = parcelUuid[0].getUuid();
                try {
                    // Création d'une socket Rfcomm
                    BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                    bluetoothSocket.connect();

                    outputStream = bluetoothSocket.getOutputStream();
                    inStream = bluetoothSocket.getInputStream();

                    // Récuperation de l'image
                    byte[] inputData = getBytes(iStream);

                    // Tentative d'écriture
                    outputStream.write(inputData);
                    bluetoothSocket.close();

                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        });


    }



    // Action déclenché au retour dans l'activité après avoir sélectionné un fichier
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
                dataUri = uri;

                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    // affiche l'image séléctionné
                    imageView.setImageBitmap(bitmap);
                    iStream = getContentResolver().openInputStream(dataUri);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    // Fonction de transformation entre l'InputStream récupéré par la séléction de la photo et
    // le byte[] à envoyer à la socket bluetooth.
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024*1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}
