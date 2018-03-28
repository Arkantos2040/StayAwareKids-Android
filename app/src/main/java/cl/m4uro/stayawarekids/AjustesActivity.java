package cl.m4uro.stayawarekids;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;

public class AjustesActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ImageButton btn_back;
    Button button_cerrar_sesion;
    Button button_opciones_avanzadas;
    ToggleButton toggleButton_notificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences(dataSet.sharedPreferences, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        toggleButton_notificaciones = (ToggleButton)findViewById(R.id.toggleButton_notificaciones);
        toggleButton_notificaciones.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editor.putBoolean(dataSet.shared_notificaciones, b);
                    editor.commit();
                }else{
                    editor.putBoolean(dataSet.shared_notificaciones, b);
                    editor.commit();
                }
            }
        });



        btn_back = (ImageButton)findViewById(R.id.imageButton_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        button_cerrar_sesion = (Button)findViewById(R.id.button_cerrar_sesion);
        button_opciones_avanzadas=(Button)findViewById(R.id.opcionesavanzadas);
        button_opciones_avanzadas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent avanzadas = new Intent (AjustesActivity.this,ScrollingConfigurationActivity.class);
                startActivity(avanzadas);
            }
        });
        button_cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AjustesActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Confirmar");
                builder.setMessage("¿Está seguro de cerrar sesión?");
                builder.setPositiveButton("Sí, cerrar sesión.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                editor.clear();
                                editor.commit();
                                MenuActivity.menu.finish();
                                Intent goToLogin = new Intent(AjustesActivity.this, LoginActivity.class);
                                startActivity(goToLogin);
                                finish();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
