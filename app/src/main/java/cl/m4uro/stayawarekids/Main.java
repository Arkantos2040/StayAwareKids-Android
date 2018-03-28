package cl.m4uro.stayawarekids;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Main extends AppCompatActivity implements AdapterView.OnItemClickListener {
//Este es el centro de efficient home donde toma las demas clases para mostrar los estados y alertas "esta es la parte visible"
    private ArrayList<String> lstSensores = new ArrayList<String>();
    private ArrayList<String> menues = new ArrayList<String>();
    private List<String> ubicaciones = new ArrayList<String>();
    private List<String> consejos = new ArrayList<String>();
    private ArrayAdapter adapter, adaptersensores;
    private ListView lvSensor;
    private int numconsejo;
    private TextView consejo, alerta;
    private SharedPreferences prefs;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        lvSensor = (ListView)findViewById(R.id.lstSensores);

        consejo = (TextView) findViewById(R.id.txtConsejo);

        menues.add("Historial Alertas");
        menues.add("Historial Ventilación");
        menues.add("Consumo Eléctrico");
        menues.add("Notificaciones");
        menues.add("Configuración");

        adapter = new ArrayAdapter(this, R.layout.menu_items, menues);

        lstSensores.clear();
        try {
            adaptersensores.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }

        prefs = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);
        ip = prefs.getString("IPRaspberry", "");

        CargaConsejos();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {      //CONTROLA LOS CLICKS EN EL MENÚ
        if (menues.get(position).equals("Historial Alertas")) {
            Intent intent = new Intent(this, ScrollingAlertHistoryActivity.class);
            startActivity(intent);
        } else if (menues.get(position).equals("Historial Ventilación")) {
            Intent intent = new Intent(this, ScrollingVentHistoryActivity.class);
            startActivity(intent);
        } else if (menues.get(position).equals("Consumo Eléctrico")) {
            Intent intent = new Intent(this, ScrollingConsumptionActivity.class);
            startActivity(intent);
        } else if (menues.get(position).equals("Notificaciones")) {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        } else if (menues.get(position).equals("Configuración")) {
            Intent intent = new Intent(this, ScrollingConfigurationActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, menues.get(position), Toast.LENGTH_SHORT).show();
        }
    }

    public void CargaSensores(){        //MUESTRA LA TEMPERATURA DEL SENSOR EN PANTALLA PRINCIPAL
        if (ip.equals("")){
            Toast.makeText(this, "No existe una dirección de Raspberry PI registrada.", Toast.LENGTH_LONG).show();
        } else {
            ubicaciones.clear();
            CargaUbicaciones(); //Carga las ubicaciones
            if (ubicaciones.size() > 0){    //Si hay busca datos de arduino
                CondensationData sensordata = new CondensationData(this, ip, "main");
                sensordata.obtenerDatosSensores();
            }
        }
    }

    public void recibirDatosSensores(String resultado){
        lvSensor.setAdapter(null);
        //Instanciar adaptador
        final SensorAdapter adaptersensores = new SensorAdapter(lstSensores, Main.this);
        runOnUiThread(new Runnable() {
            public void run() {adaptersensores.notifyDataSetChanged();
            }
        });
        lvSensor.setAdapter(adaptersensores);

        lstSensores.removeAll(lstSensores);
        adaptersensores.notifyDataSetChanged();

        String[] datosresultados = resultado.split(";");
        for (String temporal : datosresultados) {
            String[] dividirDatos = temporal.split(",");


            for (int x = 0; x < ubicaciones.size(); x++){
                String separaubicacion[] = ubicaciones.get(x).split("-");
                if (dividirDatos[0].equals(separaubicacion[0].trim())){

                    String strTemperatura;
                    String strHumedad;
                    Integer intTemperatura = 0;
                    Integer intHumedad = 0;
//                    Boolean existesensor = false;
                    if (dividirDatos[1].equals("-99")){
                        strTemperatura = "-99";
                    } else {
                        strTemperatura = dividirDatos[1] +"ºC";
                        intTemperatura = Integer.parseInt(dividirDatos[1]);
                    }

                    if (dividirDatos[2].equals("-99")){
                        strHumedad = "-99";
                    } else {
                        strHumedad = dividirDatos[2] +"%";
                        intHumedad = Integer.parseInt(dividirDatos[2]);
                    }

                    if (separaubicacion[2].trim().equals("NO")){
                        strTemperatura = "NO";
                        strHumedad = "NO";
                    }

//                    for (int y = 0; y < lstSensores.size(); y++){
//                        String sensores[] = lstSensores.get(y).split("-");
//
//                        if (sensores[0].trim().equals(dividirDatos[0].trim())){
//                            existesensor = true;
//                            lstSensores.remove(y);
//                            lstSensores.add(y, separaubicacion[0]+ " - " + separaubicacion[1] + " - T: " + strTemperatura + " - H: " + strHumedad);
//                            if (strTemperatura != "-99" && strTemperatura != "NO" && strHumedad != "-99" && strHumedad != "NO"){
//                                if (separaubicacion[4].equals("")) {  //SI TIENE CONFIG GUARDADA
//                                    //ID, Nombre, TempMin, TempMax, HumMin, HumMax, intTemperatura, intHumedad
//                                    ComparaLimites(separaubicacion[0].trim(), separaubicacion[1].trim(), Integer.parseInt(separaubicacion[4].trim()), Integer.parseInt(separaubicacion[5].trim()), Integer.parseInt(separaubicacion[6].trim()), Integer.parseInt(separaubicacion[7].trim()), intTemperatura, intHumedad);
//                                }
//                                if (separaubicacion[9].equals("")) {  // SI TIENE CONFIG GUARDADA
//                                    //ID, Nombre, OperacionTemp, TempCond, OperacionCond, OperacionHum, HumCond, intTemperatura, intHumedad
//                                    ComparaCondensacion(separaubicacion[0].trim(), separaubicacion[1].trim(), separaubicacion[8].trim(), Integer.parseInt(separaubicacion[9].trim()), separaubicacion[10].trim(), separaubicacion[11].trim(), Integer.parseInt(separaubicacion[12].trim()), intTemperatura, intHumedad);
//                                }
//                            }
//                        }
//                    }
//                    if (!existesensor) {
                    lstSensores.add(separaubicacion[0] + " - " + separaubicacion[1] + " - T: " + strTemperatura + " - H: " + strHumedad);
                    if (strTemperatura != "-99" && strTemperatura != "NO" && strHumedad != "-99" && strHumedad != "NO"){
                        if (!separaubicacion[4].trim().equals("")) {  //SI TIENE CONFIG GUARDADA
                            //ID, Nombre, TempMin, TempMax, HumMin, HumMax, intTemperatura, intHumedad
                            ComparaLimites(separaubicacion[0].trim(), separaubicacion[1].trim(), Integer.parseInt(separaubicacion[4].trim()), Integer.parseInt(separaubicacion[5].trim()), Integer.parseInt(separaubicacion[6].trim()), Integer.parseInt(separaubicacion[7].trim()), intTemperatura, intHumedad);
                        }
                        if (!separaubicacion[9].trim().equals("")) {  // SI TIENE CONFIG GUARDADA
                            //ID, Nombre, OperacionTemp, TempCond, OperacionCond, OperacionHum, HumCond, intTemperatura, intHumedad
                            ComparaCondensacion(separaubicacion[0].trim(), separaubicacion[1].trim(), separaubicacion[8].trim(), Integer.parseInt(separaubicacion[9].trim()), separaubicacion[10].trim(), separaubicacion[11].trim(), Integer.parseInt(separaubicacion[12].trim()), intTemperatura, intHumedad);
                        }
                    }
//                    }
                }
            }
        }
        adaptersensores.notifyDataSetChanged();
    }

    //Realiza las comparaciones de las ubicaciones con los sensores para saber si se superaron los límites
    private void ComparaLimites(String id, String nombre, int tempMin, int tempMax, int humMin, int humMax, int sensorTemp, int sensorHum){
        String mensaje ="";
        if (sensorTemp < tempMin){
            mensaje = "Temperatura menor que el mínimo.";
        }
        if (sensorTemp > tempMax){
            if (mensaje.trim() == ""){
                mensaje = "Temperatura mayor que el máximo.";
            } else {
                mensaje = " Temperatura mayor que el máximo.";
            }
        }
        if (sensorHum < humMin){
            if (mensaje.trim() == ""){
                mensaje = "Humedad menor que el mínimo.";
            } else {
                mensaje = " Humedad menor que el mínimo.";
            }
        }
        if (sensorHum > humMax){
            if (mensaje.trim() == ""){
                mensaje = "Humedad mayor que el máximo.";
            } else {
                mensaje = " Humedad mayor que el máximo.";
            }
        }

        if (mensaje.trim() != ""){
            mensaje = "Para el sensor " + id + " (" + nombre + "), se han superado los valores límites. " + mensaje;
            GuardaHistorialNotificacion(mensaje);
        }
    }

    //Realiza la comparación de las ubicaciones con los sensores para saber si se cumple la condición configurada de posible condensación
    private void ComparaCondensacion(String id, String nombre, String operacionTemp, int tempCond, String operacionCond, String operacionHum, int humCond, int sensorTemp, int sensorHum){
        String mensaje = "";

        if (operacionCond.equals("and")){
            switch (operacionTemp) {
                case "<":
                    if (sensorTemp < tempCond ) {
                        switch (operacionHum) {
                            case "<":
                                if (sensorHum < humCond){
                                    mensaje = "temperatura menor a " + String.valueOf(sensorTemp) + " y humedad menor a " + String.valueOf(sensorHum);
                                }
                                break;
                            case ">":
                                if (sensorHum > humCond){
                                    mensaje = "temperatura menor a " + String.valueOf(sensorTemp) + " y humedad mayor a " + String.valueOf(sensorHum);
                                }
                                break;
                            case "=":
                                if (sensorHum == humCond){
                                    mensaje = "temperatura menor a " + String.valueOf(sensorTemp) + " y humedad igual a " + String.valueOf(sensorHum);
                                }
                                break;
                        }
                    }
                    break;
                case ">":
                    if (sensorTemp > tempCond){
                        switch (operacionHum) {
                            case "<":
                                if (sensorHum < humCond){
                                    mensaje = "temperatura mayor a " + String.valueOf(sensorTemp) + " y humedad menor a " + String.valueOf(sensorHum);
                                }
                                break;
                            case ">":
                                if (sensorHum > humCond){
                                    mensaje = "temperatura mayor a " + String.valueOf(sensorTemp) + " y humedad mayor a " + String.valueOf(sensorHum);
                                }
                                break;
                            case "=":
                                if (sensorHum == humCond){
                                    mensaje = "temperatura mayor a " + String.valueOf(sensorTemp) + " y humedad igual a " + String.valueOf(sensorHum);
                                }
                                break;
                        }
                    }
                    break;
                case "=":
                    if (sensorTemp == tempCond ) {
                        switch (operacionHum) {
                            case "<":
                                if (sensorHum < humCond){
                                    mensaje = "temperatura igual a " + String.valueOf(sensorTemp) + " y humedad menor a " + String.valueOf(sensorHum);
                                }
                                break;
                            case ">":
                                if (sensorHum > humCond){
                                    mensaje = "temperatura igual a " + String.valueOf(sensorTemp) + " y humedad mayor a " + String.valueOf(sensorHum);
                                }
                                break;
                            case "=":
                                if (sensorHum == humCond){
                                    mensaje = "temperatura igual a " + String.valueOf(sensorTemp) + " y humedad igual a " + String.valueOf(sensorHum);
                                }
                                break;
                        }
                    }
                    break;
            }
        } else if (operacionCond.equals("or")){
            Boolean Cumple = Boolean.FALSE;
            switch (operacionTemp) {
                case "<":
                    if (sensorTemp < tempCond){
                        mensaje = "temperatura menor a " + String.valueOf(sensorTemp);
                        Cumple = Boolean.TRUE;
                    }
                    break;
                case ">":
                    if (sensorTemp > tempCond){
                        mensaje = "temperatura mayor a " + String.valueOf(sensorTemp);
                        Cumple = Boolean.TRUE;
                    }
                    break;
                case "=":
                    if (sensorTemp == tempCond){
                        mensaje = "temperatura igual a " + String.valueOf(sensorTemp);
                        Cumple = Boolean.TRUE;
                    }
                    break;
            }

            if (!Cumple){
                switch (operacionHum) {
                    case "<":
                        if (sensorHum < humCond){
                            mensaje = "humedad menor a " + String.valueOf(sensorHum);
                        }
                        break;
                    case ">":
                        if (sensorHum > humCond){
                            mensaje = "humedad mayor a " + String.valueOf(sensorHum);
                        }
                        break;
                    case "=":
                        if (sensorHum == humCond){
                            mensaje = "humedad igual a " + String.valueOf(sensorHum);
                        }
                        break;
                }
            }

        }

        if (mensaje.trim() != ""){
            mensaje = "Para el sensor " + id + " (" + nombre + "), se ha cumplido la condición de condensación configurada: " + mensaje + ". Es posible que se pueda producir condensación.";
            GuardaHistorialNotificacion(mensaje);
        }
    }

    private void GuardaHistorialNotificacion(String mensaje){
        AdminSQLite admin = new AdminSQLite(this, "efficienthome", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila=bd.rawQuery("select * from historialnotif where descripcion='" + mensaje.trim() + "' and fecha ='" + getDateTime() + "'", null);

        if(!fila.moveToFirst()){
            ContentValues datos=new ContentValues();
            datos.put("fecha", getDateTime());
            datos.put("descripcion", mensaje);

            bd.insert("historialnotif",null,datos);
            bd.close();
        } else {
            bd.close();
        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void CargaUbicaciones(){
        AdminSQLite admin = new AdminSQLite(this, "efficienthome", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String sql = "select codigoubicacion, nombreubicacion, existeth, existev, mintemp, maxtemp, minhum, maxhum, operaciontemp, tempcond, operacioncond, operacionhum, humcond from ubicacion order by codigoubicacion";
        Cursor fila = bd.rawQuery(sql, null);
        if (fila.moveToFirst()){
            String codigoubicacion = fila.getString(0).trim();
            String nombreubicacion = fila.getString(1).trim();
            String existeth = fila.getString(2).trim();
            String existev = fila.getString(3).trim();
            String mintemp = "";
            String maxtemp = "";
            String minhum = "";
            String maxhum = "";
            String operaciontemp = "";
            String tempcond = "";
            String operacioncond = "";
            String operacionhum = "";
            String humcond = "";
            if (!fila.isNull(4)){
                mintemp = fila.getString(4).trim();
            }
            if (!fila.isNull(5)){
                maxtemp = fila.getString(5).trim();
            }
            if (!fila.isNull(6)){
                minhum = fila.getString(6).trim();
            }
            if (!fila.isNull(7)){
                maxhum  = fila.getString(7).trim();
            }
            if (!fila.isNull(8)){
                operaciontemp = fila.getString(8).trim();
            }
            if (!fila.isNull(9)){
                tempcond = fila.getString(9).trim();
            }
            if (!fila.isNull(10)){
                operacioncond = fila.getString(10).trim();
            }
            if (!fila.isNull(11)){
                operacionhum = fila.getString(11).trim();
            }
            if (!fila.isNull(12)){
                humcond = fila.getString(12).trim();
            }

            ubicaciones.add(codigoubicacion + " - " + nombreubicacion + " - " + existeth + " - " + existev + " - " + mintemp + " - " + maxtemp + " - " + minhum + " - " + maxhum + " - " + operaciontemp + " - " + tempcond + " - " + operacioncond + " - " + operacionhum + " - " + humcond);

            while (fila.moveToNext()){
                codigoubicacion = fila.getString(0).trim();
                nombreubicacion = fila.getString(1).trim();
                existeth = fila.getString(2).trim();
                existev = fila.getString(3).trim();
                mintemp = "";
                maxtemp = "";
                minhum = "";
                maxhum = "";
                operaciontemp = "";
                tempcond = "";
                operacioncond = "";
                operacionhum = "";
                humcond = "";

                if (!fila.isNull(4)){
                    mintemp = fila.getString(4).trim();
                }
                if (!fila.isNull(5)){
                    maxtemp = fila.getString(5).trim();
                }
                if (!fila.isNull(6)){
                    minhum = fila.getString(6).trim();
                }
                if (!fila.isNull(7)){
                    maxhum  = fila.getString(7).trim();
                }
                if (!fila.isNull(8)){
                    operaciontemp = fila.getString(8).trim();
                }
                if (!fila.isNull(9)){
                    tempcond = fila.getString(9).trim();
                }
                if (!fila.isNull(10)){
                    operacioncond = fila.getString(10).trim();
                }
                if (!fila.isNull(11)){
                    operacionhum = fila.getString(11).trim();
                }
                if (!fila.isNull(12)){
                    humcond = fila.getString(12).trim();
                }

                ubicaciones.add(codigoubicacion + " - " + nombreubicacion + " - " + existeth + " - " + existev + " - " + mintemp + " - " + maxtemp + " - " + minhum + " - " + maxhum + " - " + operaciontemp + " - " + tempcond + " - " + operacioncond + " - " + operacionhum + " - " + humcond);
            }
            bd.close();

        }else{
            bd.close();
        }

    }

    public void CargaAlerta(){
        AdminSQLite admin = new AdminSQLite(this, "efficienthome", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String sql = "select fecha, descripcion from historialnotif order by codigonotificacion DESC limit 2";
        String retorno = "";

        Cursor fila = bd.rawQuery(sql, null);
        if (fila.moveToFirst()){
            String fecha = fila.getString(0).trim();
            String descripcion = fila.getString(1).trim();

            retorno = fecha + " - " + descripcion;
            while (fila.moveToNext()){
                fecha = fila.getString(0).trim();
                descripcion = fila.getString(1).trim();

                retorno = retorno + "\n" + fecha + " - " + descripcion;
            }
            bd.close();
        }
    }

    public void CargaConsejos(){
        consejos.add(getResources().getString(R.string.tip1));
        consejos.add(getResources().getString(R.string.tip2));
        consejos.add(getResources().getString(R.string.tip3));
        consejos.add(getResources().getString(R.string.tip4));
        consejos.add(getResources().getString(R.string.tip5));
        consejos.add(getResources().getString(R.string.tip6));
        consejos.add(getResources().getString(R.string.tip7));
        consejos.add(getResources().getString(R.string.tip8));
        consejos.add(getResources().getString(R.string.tip9));
        consejos.add(getResources().getString(R.string.tip10));
        consejos.add(getResources().getString(R.string.tip11));
        consejos.add(getResources().getString(R.string.tip12));
        consejos.add(getResources().getString(R.string.tip13));
        consejos.add(getResources().getString(R.string.tip14));
        consejos.add(getResources().getString(R.string.tip15));
        consejos.add(getResources().getString(R.string.tip16));
        consejos.add(getResources().getString(R.string.tip17));
        consejos.add(getResources().getString(R.string.tip18));
        consejos.add(getResources().getString(R.string.tip19));
        consejos.add(getResources().getString(R.string.tip20));
        consejos.add(getResources().getString(R.string.tip21));
        consejos.add(getResources().getString(R.string.tip22));

        NuevoConsejo();
    }

    public void NuevoConsejo(){
        int min = 0;
        int max = 21;

        Random r = new Random();
        numconsejo = r.nextInt(max - min + 1) + min;

        consejo.setText(consejos.get(numconsejo));
    }

    @Override
    protected void onResume() {
        super.onResume();
        NuevoConsejo();
        CargaSensores();
        CargaAlerta();
    }
}

