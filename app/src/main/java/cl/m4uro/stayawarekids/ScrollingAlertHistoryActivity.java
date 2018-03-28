package cl.m4uro.stayawarekids;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ScrollingAlertHistoryActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayList<String> historial = new ArrayList<String>();
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_alert_history);

        lv = (ListView) findViewById(R.id.lstAlertHistory);
        adapter = new ArrayAdapter(this, R.layout.alert_items, historial);
        lv.setAdapter(adapter);

        llenaHistorial();


    }

    private void llenaHistorial(){
        AdminSQLite admin = new AdminSQLite(this, "efficienthome", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String sql = "select fecha, descripcion from historialnotif order by codigonotificacion DESC";

        Cursor fila = bd.rawQuery(sql, null);
        if (fila.moveToFirst()){
            String fecha = fila.getString(0).trim();
            String descripcion = fila.getString(1).trim();

            historial.add(fecha + " - " + descripcion);

            while (fila.moveToNext()){
                fecha = fila.getString(0).trim();
                descripcion = fila.getString(1).trim();

                historial.add(fecha + " - " + descripcion);
            }
            bd.close();

        }else{
            bd.close();
        }

    }
}
