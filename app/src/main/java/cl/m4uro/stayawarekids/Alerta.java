package cl.m4uro.stayawarekids;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Alerta extends AppCompatActivity {
Button alerta_TH;
Button alerta_ventilacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta);
        alerta_TH = (Button) findViewById(R.id.alerta_th);
        alerta_ventilacion = (Button) findViewById(R.id.alerta_otros);
        alerta_TH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToCamaraMenu = new Intent(Alerta.this, ScrollingAlertHistoryActivity.class);
                startActivity(goToCamaraMenu);
            }
        });
        alerta_ventilacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToCamaraMenu = new Intent(Alerta.this, ScrollingVentHistoryActivity.class);
                startActivity(goToCamaraMenu);
            }
        });
    }
}
