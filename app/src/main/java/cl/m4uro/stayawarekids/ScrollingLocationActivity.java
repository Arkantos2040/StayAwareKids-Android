package cl.m4uro.stayawarekids;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ScrollingLocationActivity extends AppCompatActivity {
    private EditText Nombre, ID, MinTemp, MaxTemp, MinHum, MaxHum, TempCond, HumCond;
    private CheckBox ChkTH, ChkV;
    private String ip;
    private Integer opcion, codigo;
    private TableLayout MaxMin, TblCond;
    private TextView TxtLimites, TxtGlosa,TxTCond;
    private Button BtnGuardarArriba, BtnGuardarAbajo;
    private String[] Condiciones = { "<", ">", "="};
    private Spinner SPTemp, SPHum;
    private Boolean Limites, Condensacion, Continua;
    private RadioButton RbAnd, RbOr;
    private ArrayAdapter adapterTemp, adapterHum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrolling_location);

        Limites = Boolean.FALSE;
        Condensacion = Boolean.FALSE;
        Continua = Boolean.FALSE;

        ID = (EditText) findViewById(R.id.ID);
        Nombre = (EditText) findViewById(R.id.txtNombre);
        ChkTH = (CheckBox) findViewById(R.id.ChkTH);
        ChkV = (CheckBox) findViewById(R.id.ChkV);
        MinTemp = (EditText) findViewById(R.id.mintemp);
        MaxTemp = (EditText) findViewById(R.id.maxtemp);
        MinHum = (EditText) findViewById(R.id.minhum);
        MaxHum = (EditText) findViewById(R.id.maxhum);
        MaxMin = (TableLayout) findViewById(R.id.tblMinMax);
        TblCond = (TableLayout) findViewById(R.id.tblCondensacion);
        TxtLimites = (TextView) findViewById(R.id.txtLimites);
        TxtGlosa = (TextView) findViewById(R.id.txtGlosa);
        TxTCond = (TextView) findViewById(R.id.txtCond);
        TempCond = (EditText) findViewById(R.id.txtTempCond);
        HumCond = (EditText) findViewById(R.id.txtHumCond);
        BtnGuardarArriba = (Button) findViewById(R.id.btnGuardarArriba);
        BtnGuardarAbajo = (Button) findViewById(R.id.btnGuardarAbajo);
        RbAnd = (RadioButton) findViewById(R.id.rbAnd);
        RbOr = (RadioButton) findViewById(R.id.rbOr);
        adapterTemp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Condiciones);
        SPTemp = (Spinner)findViewById(R.id.spinnerTemp);
        SPTemp.setAdapter(adapterTemp);

        adapterHum = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Condiciones);
        SPHum = (Spinner)findViewById(R.id.spinnerHumedad);
        SPHum.setAdapter(adapterHum);

        MaxMin.setVisibility(View.INVISIBLE);
        TxtLimites.setVisibility(View.INVISIBLE);
        TxtGlosa.setVisibility(View.INVISIBLE);
        BtnGuardarArriba.setVisibility(View.VISIBLE);
        BtnGuardarAbajo.setVisibility(View.INVISIBLE);
        TxTCond.setVisibility(View.INVISIBLE);
        TblCond.setVisibility(View.INVISIBLE);
        RbAnd.setChecked(true);

        ChkTH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ChkTH.isChecked()) {
                    MaxMin.setVisibility(View.VISIBLE);
                    TxtLimites.setVisibility(View.VISIBLE);
                    TxtGlosa.setVisibility(View.VISIBLE);
                    TxTCond.setVisibility(View.VISIBLE);
                    TblCond.setVisibility(View.VISIBLE);
                    BtnGuardarArriba.setVisibility(View.INVISIBLE);
                    BtnGuardarAbajo.setVisibility(View.VISIBLE);
                    BtnGuardarArriba.setHeight(0);
                } else {
                    MaxMin.setVisibility(View.INVISIBLE);
                    TxtLimites.setVisibility(View.INVISIBLE);
                    TxtGlosa.setVisibility(View.INVISIBLE);
                    TxTCond.setVisibility(View.INVISIBLE);
                    TblCond.setVisibility(View.INVISIBLE);
                    BtnGuardarArriba.setVisibility(View.VISIBLE);
                    BtnGuardarAbajo.setVisibility(View.INVISIBLE);
                    MinTemp.setText("");
                    MaxTemp.setText("");
                    MinHum.setText("");
                    MaxHum.setText("");
                    TempCond.setText("");
                    HumCond.setText("");
                    BtnGuardarArriba.setHeight(BtnGuardarAbajo.getHeight());
                }
            }
        });

        SharedPreferences prefs = getSharedPreferences("Configuraciones", Context.MODE_PRIVATE);
        ip = prefs.getString("IPRaspberry", "");

        opcion = getIntent().getExtras().getInt("opcion");

        if (opcion == 2){
            codigo = getIntent().getExtras().getInt("codigo");
            ID.setEnabled(false);
            CargarUbicacion(codigo);
        }

    }

    public void CargarUbicacion(int Codigo){
        AdminSQLite admin = new AdminSQLite(this,"efficienthome", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila = bd.rawQuery("select * from ubicacion where codigoubicacion =" + Codigo, null);
        if (fila.moveToFirst()){
            ID.setText(fila.getString(0));
            Nombre.setText(fila.getString(1));
            if (fila.getString(2).equals("SI")){
                ChkTH.setChecked(true);
            } else {
                ChkTH.setChecked(false);
            }

            if (fila.getString(3).equals("SI")){
                ChkV.setChecked(true);
            } else {
                ChkV.setChecked(false);
            }
            if (!fila.isNull(4)){
                if (!fila.getString(4).equals("")){
                    MinTemp.setText(fila.getString(4));
                }
            }
            if (!fila.isNull(5)){
                if (!fila.getString(5).equals("")){
                    MaxTemp.setText(fila.getString(5));
                }
            }
            if (!fila.isNull(6)){
                if (!fila.getString(6).equals("")){
                    MinHum.setText(fila.getString(6));
                }
            }
            if (!fila.isNull(7)){
                if (!fila.getString(7).equals("")){
                    MaxHum.setText(fila.getString(7));
                }
            }
            if (!fila.isNull(8)){
                if (!fila.getString(8).equals("")){
                    SPTemp.setSelection(adapterTemp.getPosition(fila.getString(8)));
                }
            }
            if (!fila.isNull(9)){
                if (!fila.getString(9).equals("")){
                    TempCond.setText(fila.getString(9));
                }
            }
            if (!fila.isNull(10)){
                if (fila.getString(10).equals("and")){
                    RbAnd.setChecked(true);
                } else if (fila.getString(10).equals("or")){
                    RbOr.setChecked(true);
                } else {
                    RbAnd.setChecked(true);
                }
            }
            if (!fila.isNull(11)){
                if (!fila.getString(11).equals("")){
                    SPHum.setSelection(adapterHum.getPosition(fila.getString(11)));
                }
            }
            if (!fila.isNull(12)){
                if (!fila.getString(12).equals("")){
                    HumCond.setText(fila.getString(12));
                }
            }
            bd.close();
        }else{
            Toast.makeText(this,"La ubicación no existe", Toast.LENGTH_SHORT).show();
            bd.close();
        }
    }



    public void btnGuardaUbicacion(View view){
        if(ID.getText().length()==0){ //Revision de los datos si estan vacíos
            ID.setError("Debe ingresar identificador");
        }
        if(Nombre.getText().length()==0){
            Nombre.setError("Debe ingresar nombre");
        }
        if(ChkTH.isChecked() == false && ChkV.isChecked() == false){    //Verifica que se seleccione un tipo de sensor
            ChkTH.setError("Debe existir al menos un tipo de sensor");
            ChkV.setError("Debe existir al menos un tipo de sensor");
        } else{
            if (ip.equals("")){                 //Verifica conexión
                Toast.makeText(this, "No existe una dirección de Raspberry PI registrada.", Toast.LENGTH_LONG).show();
            } else {
                if (ChkTH.isChecked() == true) {    //Si es sensor de T y H.
                    // LIMITES
                    if (MinTemp.getText().length() == 0 && MaxTemp.getText().length() == 0 && MinHum.getText().length() == 0 && MaxHum.getText().length() == 0) {    //Si no se ingresan datos de límites
                        Limites = Boolean.FALSE;
                        Continua = Boolean.TRUE;
                    } else {       //Si ingresó al menos uno
                        if (MinTemp.getText().length() != 0 && MaxTemp.getText().length() != 0 && MinHum.getText().length() != 0 && MaxHum.getText().length() != 0) {    //Si ingresó todos
                            Limites = Boolean.TRUE;
                            Continua = Boolean.TRUE;
                        } else {
                            Limites = Boolean.FALSE;
                            Continua = Boolean.FALSE;
                            if (MinTemp.getText().length() == 0) { //Revision de los datos si estan vacíos
                                MinTemp.setError("Debe ingresar temperatura mínima");
                            }
                            if (MaxTemp.getText().length() == 0) {
                                MaxTemp.setError("Debe ingresar temperatura máxima");
                            }
                            if (MinHum.getText().length() == 0) {
                                MinHum.setError("Debe ingresar humedad mínima");
                            }
                            if (MaxHum.getText().length() == 0) {
                                MaxHum.setError("Debe ingresar humedad máxima");
                            }
                        }
                    }
                    // CONDENSACION
                    if (Continua){
                        if (TempCond.getText().length() == 0 && HumCond.getText().length() == 0) {    //Si no se ingresan datos de condensacion
                            Condensacion = Boolean.FALSE;
                            Continua = Boolean.TRUE;
                        } else {       //Si ingresó al menos uno
                            if (TempCond.getText().length() != 0 && HumCond.getText().length() != 0) {    //Si ingresó todos
                                Condensacion = Boolean.TRUE;
                                Continua = Boolean.TRUE;
                            } else {
                                Condensacion = Boolean.FALSE;
                                Continua = Boolean.FALSE;
                                if (TempCond.getText().length() == 0) { //Revision de los datos si estan vacíos
                                    TempCond.setError("Debe ingresar temperatura mínima");
                                }
                                if (HumCond.getText().length() == 0) {
                                    HumCond.setError("Debe ingresar temperatura máxima");
                                }
                            }
                        }
                    }
                }else{
                    Limites = Boolean.FALSE;
                    Condensacion = Boolean.FALSE;
                    Continua = Boolean.TRUE;
                }
                if (Continua){
                    CondensationData sensordata = new CondensationData(this, ip, "location");
                    sensordata.obtenerDatosSensores();
                }
            }
        }
    }

    public void recibirDatosSensores(String resultado){
        Boolean existe = false;
        String[] datosresultados = resultado.split(";");
        for (String temporal : datosresultados) {
            String[] dividirDatos = temporal.split(",");
            if (dividirDatos[0].equals(ID.getText().toString())){
                existe = true;
            }
        }
        if (existe){
            Continua = Boolean.TRUE;
            if (Limites){
                //Valida máximos y mínimos.
                if (Integer.parseInt(MinTemp.getText().toString()) >= Integer.parseInt(MaxTemp.getText().toString())){
                    MinTemp.setError("La temperatura mínima debe ser menor a la temperatura máxima ");
                    Continua = Boolean.FALSE;
                }
                if (Integer.parseInt(MaxTemp.getText().toString()) <= Integer.parseInt(MinTemp.getText().toString())){
                    MaxTemp.setError("La temperatura máxima debe ser mayor a la temperatura mínima ");
                    Continua = Boolean.FALSE;
                }
                if (Integer.parseInt(MinHum.getText().toString()) >= Integer.parseInt(MaxHum.getText().toString())){
                    MinHum.setError("La humedad mínima debe ser menor a la humedad máxima ");
                    Continua = Boolean.FALSE;
                }
                if (Integer.parseInt(MaxHum.getText().toString()) <= Integer.parseInt(MinHum.getText().toString())){
                    MaxHum.setError("La humedad máxima debe ser mayor a la humedad mínima ");
                    Continua = Boolean.FALSE;
                }
            }
            if (Continua){
                if (opcion == 1){
                    GuardarUbicacion();
                } else {
                    ModificarUbicacion();
                }
            }
        } else {
            Toast.makeText(this,"No se encuentra el sensor ID "+ ID.getText().toString() + " conectado", Toast.LENGTH_LONG).show();
        }
    }

    public void GuardarUbicacion(){
        AdminSQLite admin = new AdminSQLite(this, "efficienthome", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        Integer id = Integer.parseInt(ID.getText().toString());
        String nombre = Nombre.getText().toString().toUpperCase();
        String existeth;
        String existev;

        if (ChkTH.isChecked()) {
            existeth = "SI";
        } else {
            existeth = "NO";
        }

        if (ChkV.isChecked()) {
            existev = "SI";
        } else {
            existev = "NO";
        }

        Cursor fila=bd.rawQuery("select * from ubicacion where nombreubicacion='" + nombre + "' or codigoubicacion =" + id, null);

        if(!fila.moveToFirst()){
            ContentValues datos=new ContentValues();
            datos.put("codigoubicacion", id);
            datos.put("nombreubicacion", nombre);
            datos.put("existeth", existeth);
            datos.put("existev", existev);

            Integer mintemp = 0;
            Integer maxtemp = 0;
            Integer minhum = 0;
            Integer maxhum = 0;

            if (MinTemp.getText().toString().equals("")) {
                datos.putNull("mintemp");
            } else {
                mintemp = Integer.parseInt(MinTemp.getText().toString());
                datos.put("mintemp", mintemp);
            }
            if (MaxTemp.getText().toString().equals("")){
                datos.putNull("maxtemp");
            } else {
                maxtemp = Integer.parseInt(MaxTemp.getText().toString());
                datos.put("maxtemp", maxtemp);
            }
            if (MinHum.getText().toString().equals("")){
                datos.putNull("minhum");
            } else {
                minhum = Integer.parseInt(MinHum.getText().toString());
                datos.put("minhum", minhum);
            }
            if (MaxHum.getText().toString().equals("")){
                datos.putNull("maxhum");
            } else {
                maxhum = Integer.parseInt(MaxHum.getText().toString());
                datos.put("maxhum", maxhum);
            }

            String operaciontemp = SPTemp.getSelectedItem().toString();
            datos.put("operaciontemp", operaciontemp);

            Integer tempcond = 0;
            if (TempCond.getText().toString().equals("")){
                datos.putNull("tempcond");
            } else {
                tempcond = Integer.parseInt(TempCond.getText().toString());
                datos.put("tempcond", tempcond);
            }

            String operacioncond = "";
            if (RbAnd.isChecked()){
                operacioncond = "and";
            } else if (RbOr.isChecked()){
                operacioncond = "or";
            } else {
                operacioncond = "and";
            }
            datos.put("operacioncond", operacioncond);

            String operacionhum = SPHum.getSelectedItem().toString();
            datos.put("operacionhum", operacionhum);

            Integer humcond = 0;
            if (HumCond.getText().toString().equals("")){
                datos.putNull("humcond");
            } else {
                humcond = Integer.parseInt(HumCond.getText().toString());
                datos.put("humcond", humcond);
            }

            bd.insert("ubicacion",null,datos);
            bd.close();

            Toast.makeText(this, "Ubicación agregada", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "El Identificador o el Nombre ya existe", Toast.LENGTH_LONG).show();
        }
    }

    public void ModificarUbicacion() {
        AdminSQLite admin = new AdminSQLite(this, "efficienthome", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String nombre = Nombre.getText().toString().toUpperCase();
        String existeth;
        String existev;

        if (ChkTH.isChecked()) {
            existeth = "SI";
        } else {
            existeth = "NO";
        }

        if (ChkV.isChecked()) {
            existev = "SI";
        } else {
            existev = "NO";
        }


        ContentValues datos = new ContentValues();
        datos.put("nombreubicacion", nombre);
        datos.put("existeth", existeth);
        datos.put("existev", existev);

        Integer mintemp = 0;
        Integer maxtemp = 0;
        Integer minhum = 0;
        Integer maxhum = 0;

        if (MinTemp.getText().toString().equals("")) {
            datos.putNull("mintemp");
        } else {
            mintemp = Integer.parseInt(MinTemp.getText().toString());
            datos.put("mintemp", mintemp);
        }
        if (MaxTemp.getText().toString().equals("")){
            datos.putNull("maxtemp");
        } else {
            maxtemp = Integer.parseInt(MaxTemp.getText().toString());
            datos.put("maxtemp", maxtemp);
        }
        if (MinHum.getText().toString().equals("")){
            datos.putNull("minhum");
        } else {
            minhum = Integer.parseInt(MinHum.getText().toString());
            datos.put("minhum", minhum);
        }
        if (MaxHum.getText().toString().equals("")){
            datos.putNull("maxhum");
        } else {
            maxhum = Integer.parseInt(MaxHum.getText().toString());
            datos.put("maxhum", maxhum);
        }

        String operaciontemp = SPTemp.getSelectedItem().toString();
        datos.put("operaciontemp", operaciontemp);

        Integer tempcond = 0;
        if (TempCond.getText().toString().equals("")){
            datos.putNull("tempcond");
        } else {
            tempcond = Integer.parseInt(TempCond.getText().toString());
            datos.put("tempcond", tempcond);
        }

        String operacioncond = "";
        if (RbAnd.isChecked()){
            operacioncond = "and";
        } else if (RbOr.isChecked()){
            operacioncond = "or";
        } else {
            operacioncond = "and";
        }
        datos.put("operacioncond", operacioncond);

        String operacionhum = SPHum.getSelectedItem().toString();
        datos.put("operacionhum", operacionhum);

        Integer humcond = 0;
        if (HumCond.getText().toString().equals("")){
            datos.putNull("humcond");
        } else {
            humcond = Integer.parseInt(HumCond.getText().toString());
            datos.put("humcond", humcond);
        }

        int contador = bd.update("ubicacion", datos, "codigoubicacion=" + codigo, null);

        bd.close();

        if (contador == 1 ){
            Toast.makeText(this,"La ubicación fue modificada.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"La ubicación no existe.", Toast.LENGTH_LONG).show();
        }

        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


}
