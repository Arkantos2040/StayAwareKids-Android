package cl.m4uro.stayawarekids;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {
    TextView cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;

    ImageButton btn_ajustes;
    Button btn_ver_ninos, btn_videovigilancia, btn_ver_alertas,btn_th;
    private FirebaseAuth mAuth;
    public static Activity menu = null;

    Typeface weatherFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        menu = this;
        mAuth = FirebaseAuth.getInstance();

        btn_ajustes = (ImageButton) findViewById(R.id.imageButton_ajustes);
        btn_ver_ninos = (Button) findViewById(R.id.button_ver_ninos);
        btn_videovigilancia = (Button)findViewById(R.id.button_videovigilancia);
        btn_ver_alertas = (Button)findViewById(R.id.button_ver_alertas);
        btn_th =(Button)findViewById(R.id.button_ver_sensor) ;

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(dataSet.sharedPreferences,MODE_PRIVATE);
        Boolean isDocente = sharedPreferences.getBoolean(dataSet.shared_docente,false);

        if(!isDocente){
            btn_videovigilancia.setVisibility(View.INVISIBLE);
            btn_ver_alertas.setVisibility(View.INVISIBLE);
        }

        btn_videovigilancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToCamaraMenu = new Intent(MenuActivity.this, CamaraMenuActivity.class);
                startActivity(goToCamaraMenu);
            }
        });
        btn_th.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToCamaraMenu = new Intent(MenuActivity.this, Main.class);
                startActivity(goToCamaraMenu);
            }
        });

        btn_ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAjustes = new Intent(MenuActivity.this, AjustesActivity.class);
                startActivity(goToAjustes);
            }
        });

        btn_ver_ninos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToVerNinos = new Intent(MenuActivity.this, VerNinosActivity.class);
                startActivity(goToVerNinos);
            }
        });
        btn_ver_alertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAlertas = new Intent(MenuActivity.this, ScrollingAlertHistoryActivity.class);
                startActivity(goToAlertas);
            }
        });

        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");

        cityField = (TextView)findViewById(R.id.city_field);
        updatedField = (TextView)findViewById(R.id.updated_field);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
        humidity_field = (TextView)findViewById(R.id.humidity_field);
        pressure_field = (TextView)findViewById(R.id.pressure_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

        Function.placeIdTask asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

                cityField.setText(weather_city);
                updatedField.setText(weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humedad: "+weather_humidity);
                pressure_field.setText("Presión: "+weather_pressure);
                weatherIcon.setText(Html.fromHtml(weather_iconText));


            }
        });
        asyncTask.execute("-33.013351", "-71.541385"); //  asyncTask.execute("Latitude", "Longitude")


    }
}
