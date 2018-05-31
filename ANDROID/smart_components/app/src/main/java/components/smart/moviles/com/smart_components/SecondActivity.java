package components.smart.moviles.com.smart_components;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import static components.smart.moviles.com.smart_components.MainActivity.MEDIA_TYPE_IMAGE;

public class SecondActivity extends AppCompatActivity {
    VideoView video;
    Button irACamaraBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        init();
    }

    void init(){
        video = super.findViewById(R.id.idVideo);
        irACamaraBtn = super.findViewById(R.id.idCamaraVideo);
        irACamaraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toVideoCamara();
            }
        });
    }

    static final int MEDIA_TYPE_VIDEO = 1;
    private void toVideoCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, MEDIA_TYPE_VIDEO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MEDIA_TYPE_VIDEO && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            video.setVideoURI(videoUri);
        }
    }
}
