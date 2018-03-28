package cl.m4uro.stayawarekids;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import cl.m4uro.stayawarekids.Adapter.ChildrenAdapter;
import cl.m4uro.stayawarekids.Model.ChildrenModel;

public class VerNinoActivity extends AppCompatActivity {

    String url= dataSet.web_service_url+"consulta_niño.php";

    ImageView imageView_avatar;
    ImageButton imageButton_back, imageButton_gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_nino);

        final String idadultomayor = (String) getIntent().getExtras().get("id_adultomayor");

        imageView_avatar = (ImageView)findViewById(R.id.imageView_avatar);
        imageButton_back = (ImageButton)findViewById(R.id.imageButton_back);
        imageButton_gallery = (ImageButton)findViewById(R.id.imageButton_gallery);

        imageButton_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToFotosNino = new Intent(VerNinoActivity.this, FotosNinoActivity.class);
                goToFotosNino.putExtra("id_adultomayor", idadultomayor);
                startActivity(goToFotosNino);
            }
        });

        imageButton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        httpquery(idadultomayor);



    }

    public void httpquery(final String id_adultomayor) {
        class UserLoginClass extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(VerNinoActivity.this, "Sincronizando con Satelites", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONArray obj = new JSONArray(s);
                    for(int i=0;i < obj.length();i++){
                        String nombre = obj.getJSONObject(i).getString("nombreAdultoMayor");
                        String apellido = obj.getJSONObject(i).getString("apellidoAdultoMayor");
                        String imagen = obj.getJSONObject(i).getString("imagen");

                        Bitmap bitmap = null;
                        setTitle(nombre + " "+apellido);
                        try ( InputStream is = new URL(imagen).openStream() ) {
                            bitmap = BitmapFactory.decodeStream( is );
                            imageView_avatar.setImageBitmap(bitmap);
                        } catch (MalformedURLException e) {
                            imageView_avatar.setImageResource(R.mipmap.avatar);
                        } catch (IOException e) {
                            imageView_avatar.setImageResource(R.mipmap.avatar);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<>();
                data.put("id_AdultoMayor", params[0]);

                HttpQuerys ruc = new HttpQuerys();

                String result = ruc.sendPostRequest(url, data);

                return result;
            }
        }
        UserLoginClass ulc = new UserLoginClass();
        ulc.execute(id_adultomayor);

    }
}
