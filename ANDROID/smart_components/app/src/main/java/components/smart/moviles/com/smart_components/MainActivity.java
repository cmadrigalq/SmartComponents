package components.smart.moviles.com.smart_components;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageButton camara;
    ImageView preview;
    TextView latitud,longitud,num;
    Double lat,lng;
    Button send;
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
    void enviarMj(){
        String numero = num.getText().toString();
        if(numero.trim().length() != 8){
               mensaje("Verifique el # telefonico");
               return;
        }
        String url = "Hey! En este momento me encuentro aqui: http://maps.google.es/?q=" +
                String.valueOf(this.lat)+"%20"+
                String.valueOf(this.lng);

        /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("vnd.android-dir/mms-sms");
        sendIntent.putExtra("address", numero);
        sendIntent.putExtra("sms_body", url);
        startActivity(sendIntent);*/
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        //TODO
        i.putExtra(Intent.EXTRA_TEXT, url);
        i.setType("text/plain");
        i.setPackage("com.whatsapp");
        startActivity(i);
    }
    ////////////MENSAJES********************************//////
    public void mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
