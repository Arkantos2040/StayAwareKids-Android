package cl.m4uro.stayawarekids;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import cl.m4uro.stayawarekids.Adapter.ChildrenAdapter;
import cl.m4uro.stayawarekids.Model.ChildrenModel;

public class VerNinosActivity extends AppCompatActivity {

    String url=dataSet.web_service_url+"VerAdultos.php";

    private ListView listView;
    private ArrayList<ChildrenModel> models;
    private ChildrenAdapter childrenAdapter;

    ImageButton btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_ninos);

        listView = findViewById(R.id.listView_ninos);

        btn_back = (ImageButton)findViewById(R.id.imageButton_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        models = new ArrayList<>();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(dataSet.sharedPreferences,MODE_PRIVATE);

        userLogin(sharedPreferences.getString(dataSet.shared_id_red_apoyo,"0"));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChildrenModel childrenModel = models.get(i);

                String id_adultomayor = ""+childrenModel.getChildrenID();
                Intent goToVerNino = new Intent(VerNinosActivity.this, VerNinoActivity.class);
                goToVerNino.putExtra("id_adultomayor", id_adultomayor);
                startActivity(goToVerNino);
            }
        });

    }

    public void userLogin(final String idred) {
        class UserLoginClass extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(VerNinosActivity.this, "Sincronizando con Satelites", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONArray obj = new JSONArray(s);
                    for(int i=0;i < obj.length();i++){
                        int id = Integer.parseInt(obj.getJSONObject(i).getString("id_AdultoMayor"));
                        String nombre = obj.getJSONObject(i).getString("nombreAdultoMayor");
                        String apellido = obj.getJSONObject(i).getString("apellidoAdultoMayor");
                        String rut = obj.getJSONObject(i).getString("rutAdultoMayor");
                        String imagen = obj.getJSONObject(i).getString("imagen");
                        models.add(new ChildrenModel(id,imagen, nombre + " " + apellido, rut));
                        childrenAdapter = new ChildrenAdapter(VerNinosActivity.this, models);

                        listView.setAdapter(childrenAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<>();
                data.put("idred", params[0]);

                HttpQuerys ruc = new HttpQuerys();

                String result = ruc.sendPostRequest(url, data);

                return result;
            }
        }
        UserLoginClass ulc = new UserLoginClass();
        ulc.execute(idred);

    }
}
