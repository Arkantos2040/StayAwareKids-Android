package cl.m4uro.stayawarekids;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

//Esta clase es para el historial de la ventilacion misma
public class ScrollingVentHistoryActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayList<String> historial = new ArrayList<String>();
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrolling_vent_history);

        lv = (ListView) findViewById(R.id.lstVentHistory);
        adapter = new ArrayAdapter(this, R.layout.alert_items, historial);
        lv.setAdapter(adapter);

        llenaHistorial();

    }

    private void llenaHistorial(){
        AdminSQLite admin = new AdminSQLite(this, "efficienthome", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String sql = "select fecha, minutosabierta, minutoscerrada from historialvent order by codigoventilacion";

        Cursor fila = bd.rawQuery(sql, null);
        if (fila.moveToFirst()){
            String fecha = fila.getString(0).trim();
            String minutosabierta = fila.getString(1).trim();
            String minutoscerrada = fila.getString(2).trim();

            historial.add(fecha + " - Min. abiertas: " + minutosabierta + " - Min. cerradas: " + minutoscerrada);

            while (fila.moveToNext()){
                fecha = fila.getString(0).trim();
                minutosabierta = fila.getString(1).trim();
                minutoscerrada = fila.getString(2).trim();

                historial.add(fecha + " - Min. abiertas: " + minutosabierta + " - Min. cerradas: " + minutoscerrada);
            }
            bd.close();

        }else{
            bd.close();
        }

    }
}
