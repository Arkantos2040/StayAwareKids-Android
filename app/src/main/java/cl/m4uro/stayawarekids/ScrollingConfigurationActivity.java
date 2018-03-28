package cl.m4uro.stayawarekids;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScrollingConfigurationActivity extends AppCompatActivity {
    //La configuracion de Efficient home donde se conecta a la raspberry, mongodb y a la vez se pueden agregar o eliminar sensores de T/H y sensores de puerta y ventanas
    private EditText IPR, API, HoraInicio, HoraTermino, Porcentaje, TiempoRefresco;
    private SharedPreferences prefs;
    private ArrayList<String> lstUbica = new ArrayList<String>();
    private ListView lv;
    private LocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_configuration);

        prefs = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);

        //IP

        IPR= (EditText) findViewById(R.id.txtIPR);
        IPR.setText(prefs.getString("IPRaspberry", ""));

        //IP

        API= (EditText) findViewById(R.id.txtApi);
        API.setText(prefs.getString("APIKEY", ""));

        //Ventilación

        HoraInicio = (EditText) findViewById(R.id.txtHoraInicio);
        HoraInicio.setText(prefs.getString("HoraInicio",null));

        HoraTermino = (EditText) findViewById(R.id.txtHoraTermino);
        HoraTermino.setText(prefs.getString("HoraTermino",null));

        Porcentaje = (EditText) findViewById(R.id.txtPorcentaje);
        Porcentaje.setText(prefs.getString("PAceptacion",null));

        TiempoRefresco = (EditText) findViewById(R.id.txtResfresco);
        TiempoRefresco.setText(prefs.getString("TiempoRefresco",null));

        //Asigna adaptador
        lv = (ListView)findViewById(R.id.lstUbicaciones);
        //Instanciar adaptador
        adapter = new LocationAdapter(lstUbica, ScrollingConfigurationActivity.this);
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
        lv.setAdapter(adapter);

        llenaUbicacion();

        if (!IPR.getText().toString().equals("")){
            ConsumptionValues valor = new ConsumptionValues(this, IPR.getText().toString(), "configuracion");
            valor.obtenerValores();
        }

    }

    public void recibirValores(String resultado) {
        if (resultado.equals("") || resultado.equals("Error de conexión")){
            Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show();
        } else {
            String[] precios = resultado.split(","); //lo recibido por consulta
            System.out.println(precios[0] + " " + precios[1] + " " + precios[2]);
            ConsumptionValues valores = new ConsumptionValues(Double.parseDouble(precios[0]),
                    Double.parseDouble(precios[1]), Integer.parseInt(precios[2]));
        }
    }


    public void GuardarConfIP(View view){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("IPRaspberry", IPR.getText().toString());
        editor.commit();
        Toast.makeText(this, "IP guardada", Toast.LENGTH_SHORT).show();
    }

    public void GuardarAPI(View view){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("APIKEY", API.getText().toString());
        editor.commit();
        Toast.makeText(this, "API KEY guardada", Toast.LENGTH_SHORT).show();
    }

    public void GuardarConfHorarios(View view){
        CancelarServicioAnterior();
        Boolean vaciohi = false, vacioht = false, ActivarAlarma = false, vaciov = false, vacior = false;
        String[] separador;
        SharedPreferences.Editor editor = prefs.edit();

        if (HoraInicio.getText().length()==0) {   //Si está vacío no hay alarma de ventilación
            editor.remove("HoraInicio");
            editor.commit();
            editor.putString("HoraInicio", "");
            vaciohi = true;
        } else {
            separador = HoraInicio.getText().toString().split(":");
            if (Integer.parseInt(separador[0]) < 0 || Integer.parseInt(separador[0]) > 24) {    //Valida hora
                HoraInicio.setError("Debe ingresar una hora válida");
                vaciohi = true;
            } else {
                if (Integer.parseInt(separador[1]) < 0 || Integer.parseInt(separador[1]) > 59){     //Valida minutos
                    HoraInicio.setError("Debe ingresar minutos válidos");
                    vaciohi = true;
                } else {
                    editor.remove("HoraInicio");
                    editor.commit();
                    editor.putString("HoraInicio", HoraInicio.getText().toString());
                }
            }
        }

        if (HoraTermino.getText().length()==0) {   //Si está vacío no hay alarma de ventilación
            editor.remove("HoraTermino");
            editor.commit();
            editor.putString("HoraTermino", "");
            vacioht = true;
        } else {
            separador = HoraTermino.getText().toString().split(":");
            if (Integer.parseInt(separador[0]) < 0 || Integer.parseInt(separador[0]) > 24) {    //Valida hora
                HoraTermino.setError("Debe ingresar una hora válida");
                vacioht = true;
            } else {
                if (Integer.parseInt(separador[1]) < 0 || Integer.parseInt(separador[1]) > 59){     //Valida minutos
                    HoraTermino.setError("Debe ingresar minutos válidos");
                    vacioht = true;
                } else {
                    editor.remove("HoraTermino");
                    editor.commit();
                    editor.putString("HoraTermino", HoraTermino.getText().toString());
                }
            }
        }

        if (TiempoRefresco.getText().length()==0) {
            editor.remove("TiempoRefresco");
            editor.commit();
            editor.putString("TiempoRefresco", "");
            vacior = true;
        } else {
            editor.remove("TiempoRefresco");
            editor.commit();
            editor.putString("TiempoRefresco", TiempoRefresco.getText().toString());
        }

        if (vaciohi && vacioht) {
            editor.remove("PAceptacion");
            editor.commit();
            editor.putString("PAceptacion", "");
        } else {
            if (vaciohi || vacioht){
                Toast.makeText(this, "Deben estar ambas horas correctamente ingresadas", Toast.LENGTH_LONG).show();
            } else {
                if (vaciov){
                    Toast.makeText(this, "Debe ingresar cantidad de sensores", Toast.LENGTH_LONG).show();
                } else {
                    if (vacior){
                        Toast.makeText(this, "Debe ingresar tiempo de refresco", Toast.LENGTH_LONG).show();
                    } else {
                        Integer PAceptacion = Integer.parseInt(Porcentaje.getText().toString());
                        if (Porcentaje.getText().length() == 0) {
                            Porcentaje.setError("Debe ingresar porcentaje de aceptación");
                        } else {
                            if (PAceptacion < 0 || PAceptacion > 100) {
                                Porcentaje.setError("El porcentaje debe estar entre 0 y 100");
                            } else {
                                editor.remove("PAceptacion");
                                editor.commit();
                                editor.putString("PAceptacion", Porcentaje.getText().toString());
                                ActivarAlarma = true;
                            }
                        }
                    }
                }
            }
        }

        if (ActivarAlarma){
            editor.commit();
            CrearAlarma();
            Toast.makeText(this, "Notificación de ventilación activada", Toast.LENGTH_SHORT).show();
        }
    }

    public void recibirUpdate(String resultado){
        Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();

        if (!IPR.getText().toString().equals("")){
            ConsumptionValues valor = new ConsumptionValues(this, IPR.getText().toString(), "configuracion");
            valor.obtenerValores();
        }
    }

    public void llenaUbicacion(){
        AdminSQLite admin = new AdminSQLite(this, "efficienthome", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String sql = "select codigoubicacion, nombreubicacion, existeth, existev from ubicacion order by codigoubicacion";

        Cursor fila = bd.rawQuery(sql, null);
        if (fila.moveToFirst()){
            String codigoubicacion = fila.getString(0).trim();
            String nombreubicacion = fila.getString(1).trim();
            String existeth = fila.getString(2).trim();
            String existev = fila.getString(3).trim();

            lstUbica.add(codigoubicacion + " - " + nombreubicacion + " - " + existeth + " - " + existev);

            while (fila.moveToNext()){
                codigoubicacion = fila.getString(0).trim();
                nombreubicacion = fila.getString(1).trim();
                existeth = fila.getString(2).trim();
                existev = fila.getString(3).trim();

                lstUbica.add(codigoubicacion + " - " + nombreubicacion + " - " + existeth + " - " + existev);
            }
            bd.close();

        }else{
            bd.close();
        }

    }

    public void CrearAlarma(){
        startService(new Intent(getBaseContext(), ServicePushVentilation.class));
    }

    public void CancelarServicioAnterior(){
        try {
            ServicePushVentilation.StopNotifications = true;
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void btnAgregaUbicacion(View view){
        Intent intent = new Intent(this, ScrollingLocationActivity.class);
        intent.putExtra("opcion",1);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lstUbica.removeAll(lstUbica);
        adapter.notifyDataSetChanged();
        llenaUbicacion();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
