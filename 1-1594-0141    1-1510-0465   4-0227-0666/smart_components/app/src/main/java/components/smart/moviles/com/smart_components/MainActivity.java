package components.smart.moviles.com.smart_components;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    ImageButton camara, llamada;
    ImageView preview;
    TextView latitud,longitud,num;
    Double lat,lng;
    Button send,sendSMS;
    Button play, stop,recorder,stopRecorder, toActivityVideo;
    String pathSave ="";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    final int REQUEST_PERMISSION_CODE = 1000;
    static final int request_Code = 1;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initGPS();
    }

    void init(){
        camara = super.findViewById(R.id.camara);
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCamara();
            }
        });
        preview = super.findViewById(R.id.preview);
        preview.setVisibility(View.INVISIBLE);
        latitud  = super.findViewById(R.id.latitud);
        longitud = super.findViewById(R.id.longitud);
        latitud.setVisibility(View.INVISIBLE);
        longitud.setVisibility(View.INVISIBLE);
        num = super.findViewById(R.id.num);
        send = super.findViewById(R.id.sendMSJ);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMj();
            }
        });
        send.setVisibility(View.INVISIBLE);
        if(!checkPermissionFromDevice())
            requestPermission();
        play = super.findViewById(R.id.idplay);
        stop = super.findViewById(R.id.idStop);
        recorder = super.findViewById(R.id.recorder);
        stopRecorder = super.findViewById(R.id.stoprecorder);
        toActivityVideo = super.findViewById(R.id.toSecondActivity);
        recorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {if( checkPermissionFromDevice()){grabar(); }else{ requestPermission(); }}
            });
        stopRecorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pararDeGrabar();
                }
            });
        play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reproducir();
                }
            });
        stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pararDeReproducir();
                }
            });
        toActivityVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivityForResult(intent,request_Code);
                }
        });
        llamada = super.findViewById(R.id.idLlamada);
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            requestCallPermision();
        llamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!num.getText().toString().equals("")) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+num.getText().toString()));
                        startActivity(i);
                    }else{
                        Toast.makeText(MainActivity.this, "No se pudo realizar la llamada...", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Primero digite el número telefónico...", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        sendSMS = super.findViewById(R.id.sendMSJ3);
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMjSMS();
            }
        });

    }



    /////**********PARA EL USO DE LA CAMARA*****************///////
    static final int MEDIA_TYPE_IMAGE = 1;
    void toCamara(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, MEDIA_TYPE_IMAGE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MEDIA_TYPE_IMAGE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            preview.setVisibility(View.VISIBLE);
            preview.setImageBitmap(imageBitmap);
            obtenerCoordenadas();
        }
    }
    /////**********FIN PARA EL USO DE LA CAMARA*****************///////
    //////////////*****PARA USO DEL GPS**************************//////
    Location location;
    LocationManager locationManager;
    LocationListener locationListener;
    AlertDialog alert = null;
    void initGPS(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if ( !locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
            AlertNoGps();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                MainActivity.this.location = location;
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
    }
    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    void obtenerCoordenadas(){
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
        latitud.setVisibility(View.VISIBLE);
        longitud.setVisibility(View.VISIBLE);
        latitud.setText("Latitud: "+String.valueOf(lat));
        longitud.setText("Longitud: "+String.valueOf(lng));
        send.setVisibility(View.VISIBLE);
    }
    /////////////******FIN DEL USO DEL GPS***********************///////
    ////////////MENSAJES********************************//////
    void enviarMjSMS(){
        String numero = num.getText().toString();
        if(numero.trim().length() != 8){
               mensaje("Verifique el # telefonico");
               return;
        }

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("vnd.android-dir/mms-sms");
        sendIntent.putExtra("address", numero);
        sendIntent.putExtra("sms_body", buildUrl());
        startActivity(sendIntent);
    }

    void enviarMj(){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        //TODO
        i.putExtra(Intent.EXTRA_TEXT, buildUrl());
        i.setType("text/plain");
        i.setPackage("com.whatsapp");
        startActivity(i);
    }

    String buildUrl(){
        return "Hey! En este momento me encuentro aqui: http://maps.google.es/?q=" +
                String.valueOf(this.lat)+"%20"+
                String.valueOf(this.lng);
    }
    ////////////MENSAJES********************************//////
    public void mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    //  GRABACIÓN DE AUDIOS
    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_resut = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_resut == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

            }
            break;
        }
    }

    private void grabar() {
        pathSave = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"
                + UUID.randomUUID().toString()+"_audio_record.3gp";
        setupMediaRecorder();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopRecorder.setEnabled(true);
        play.setEnabled(false);
        stop.setEnabled(false);

        Toast.makeText(MainActivity.this, "Grabando...", Toast.LENGTH_SHORT).show();
    }

    private void pararDeGrabar() {
        mediaRecorder.stop();
        stopRecorder.setEnabled(false);
        play.setEnabled(true);
        recorder.setEnabled(true);
        stop.setEnabled(false);

        Toast.makeText(MainActivity.this, "Se detuvo la grabación...", Toast.LENGTH_SHORT).show();
    }

    private void reproducir() {
        stop.setEnabled(true);
        stopRecorder.setEnabled(false);
        recorder.setEnabled(false);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(pathSave);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        Toast.makeText(MainActivity.this, "Reproduciendo...", Toast.LENGTH_SHORT).show();

    }

    private void pararDeReproducir() {
        stopRecorder.setEnabled(false);
        recorder.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(true);

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            setupMediaRecorder();
        }

        Toast.makeText(MainActivity.this, "Se detuvo la reproducción...", Toast.LENGTH_SHORT).show();
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void requestCallPermision() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CALL_PHONE
        },REQUEST_PERMISSION_CODE);
    }

}
