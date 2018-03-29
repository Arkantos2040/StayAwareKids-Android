package cl.m4uro.stayawarekids;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import cl.m4uro.stayawarekids.Adapter.ChildrenAdapter;
import cl.m4uro.stayawarekids.Model.ChildrenModel;

public class FotosNinoActivity extends AppCompatActivity {

    String url= dataSet.web_service_url+"consulta_niño.php";

    ImageButton imageButton_back;
    ImageView image1, image2, image3, image4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos_nino);

        final String idadultomayor = (String) getIntent().getExtras().get("id_adultomayor");

        image1 = (ImageView)findViewById(R.id.imageView_foto1);
        image2 = (ImageView)findViewById(R.id.imageView_foto2);
        image3 = (ImageView)findViewById(R.id.imageView_foto3);
        image4 = (ImageView)findViewById(R.id.imageView_foto4);


        imageButton_back = (ImageButton)findViewById(R.id.imageButton_back);
        imageButton_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cargaImagenes(idadultomayor);

    }

    public void cargaImagenes(final String idred) {
        class UserLoginClass extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FotosNinoActivity.this, "Sincronizando con Satelites", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                String nombre = null;
                String apellido = null;
                URL imagenUrl=null;
                HttpURLConnection conn = null;
                try {
                    JSONArray obj = new JSONArray(s);
                    for(int i=0;i < obj.length();i++){
                         nombre = obj.getJSONObject(i).getString("nombreAdultoMayor");
                         apellido = obj.getJSONObject(i).getString("apellidoAdultoMayor");
                    }
                        imagenUrl= new URL("http://cesar.awaresystems.cl/fotos/"+nombre+"%20"+ apellido+"/"+nombre+"%20"+apellido+"_foto1.jpg");
                        conn = (HttpURLConnection) imagenUrl.openConnection();
                        conn.connect();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize=2;
                        Bitmap imagen = BitmapFactory.decodeStream(conn.getInputStream(),new Rect(0,0,0,0),options);
                        image1.setImageBitmap(imagen);
                    imagenUrl= new URL("http://cesar.awaresystems.cl/fotos/"+nombre+"%20"+ apellido+"/"+nombre+"%20"+apellido+"_foto2.jpg");
                    conn = (HttpURLConnection) imagenUrl.openConnection();
                    conn.connect();
                    BitmapFactory.Options options1 = new BitmapFactory.Options();
                    options1.inSampleSize=2;
                    Bitmap imagen1 = BitmapFactory.decodeStream(conn.getInputStream(),new Rect(0,0,0,0),options1);
                    image2.setImageBitmap(imagen1);
                    imagenUrl= new URL("http://cesar.awaresystems.cl/fotos/"+nombre+"%20"+ apellido+"/"+nombre+"%20"+apellido+"_foto3.jpg");
                    conn = (HttpURLConnection) imagenUrl.openConnection();
                    conn.connect();
                    BitmapFactory.Options options2 = new BitmapFactory.Options();
                    options2.inSampleSize=2;
                    Bitmap imagen2 = BitmapFactory.decodeStream(conn.getInputStream(),new Rect(0,0,0,0),options2);
                    image3.setImageBitmap(imagen2);
                    imagenUrl= new URL("http://cesar.awaresystems.cl/fotos/"+nombre+"%20"+ apellido+"/"+nombre+"%20"+apellido+"_foto4.jpg");
                    conn = (HttpURLConnection) imagenUrl.openConnection();
                    conn.connect();
                    BitmapFactory.Options options3 = new BitmapFactory.Options();
                    options3.inSampleSize=2;
                    Bitmap imagen3 = BitmapFactory.decodeStream(conn.getInputStream(),new Rect(0,0,0,0),options3);
                    image4.setImageBitmap(imagen3);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
        ulc.execute(idred);

    }
}
