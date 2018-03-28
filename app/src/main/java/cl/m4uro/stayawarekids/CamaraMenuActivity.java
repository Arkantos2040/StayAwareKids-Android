package cl.m4uro.stayawarekids;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class CamaraMenuActivity extends AppCompatActivity {

    private Button btn_camara1, btn_camara2, btn_camara3;
    private ImageButton imageButton_back;

    //aquí van las url de las camaras.
    String path_cam1 = "http://10.40.3.180/VIDEO.CGI"; //RESPETAR ESTE FORMATO QUE ES ONVIF DE LAS CAMARAS.
    String path_cam2 = "rtsp://unab:unab@algarrobo.dahuaddns.com:554/cam/realmonitor?channel=2&subtype=0";
    String path_cam3 = "rtsp://unab:unab@algarrobo.dahuaddns.com:554/cam/realmonitor?channel=3&subtype=0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara_menu);
        imageButton_back = (ImageButton)findViewById(R.id.imageButton_back);
        imageButton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_camara1 = (Button)findViewById(R.id.button_camara1);
        btn_camara1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToCam = new Intent(CamaraMenuActivity.this, PlayerActivity.class);
                goToCam.putExtra("path",path_cam1);
                goToCam.putExtra("title", "Cámara 1");
                startActivity(goToCam);
            }
        });
        btn_camara2 = (Button)findViewById(R.id.button_camara2);
        btn_camara2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToCam = new Intent(CamaraMenuActivity.this, PlayerActivity.class);
                goToCam.putExtra("path",path_cam2);
                goToCam.putExtra("title", "Cámara 2");
                startActivity(goToCam);
            }
        });
        btn_camara3 = (Button)findViewById(R.id.button_camara3);
        btn_camara3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToCam = new Intent(CamaraMenuActivity.this, PlayerActivity.class);
                goToCam.putExtra("path",path_cam3);
                goToCam.putExtra("title", "Cámara 3");
                startActivity(goToCam);
            }
        });
    }
}
