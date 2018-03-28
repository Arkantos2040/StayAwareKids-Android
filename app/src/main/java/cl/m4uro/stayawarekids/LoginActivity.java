package cl.m4uro.stayawarekids;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button ingresar, registrar, olvideContrasena;
    private static final String TAG = "LoginActivity";
    private static final String LOGIN_URL = dataSet.web_service_url+"loginRed.php";
    private static final String REGISTER_URL =dataSet.web_service_url+"registerR.php";

    EditText mEmailField;
    EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);



        mEmailField = (EditText)findViewById(R.id.editText_email);
        mPasswordField = (EditText)findViewById(R.id.editText_password);

        ingresar = (Button)findViewById(R.id.button_ingresar);
        registrar = (Button)findViewById(R.id.button_registro);


        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                ingresar.setVisibility(View.INVISIBLE);
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                registrar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        //SE EJECUTA CUANDO EL USUARIO YA ESTÁ LOGUEADO.
        if (user != null) {
            String email = user.getEmail();
            userLogin(email);
        } else {
            Log.w(TAG, "Entre al else de updateUI");
        }
    }

    private void createAccount(final String email,final String password){

        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Iniciar sesión con éxito, actualizar la IU con la información del usuario que inició sesión
                            Log.d(TAG, "createUserWithEmail:success");
                            MyFirebaseInstanceIDService token = new MyFirebaseInstanceIDService();
                            registrar(email,password,token.sendRegistrationToServer());
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // Si la autentificación falla, se envía un mensaje al usuario.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Email ya existe.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    private void signIn(final String email, final String password){

        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            MyFirebaseInstanceIDService token = new MyFirebaseInstanceIDService();
                            registrar(email,password,token.sendRegistrationToServer());
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Email y/o contraseña incorrectos.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Email es requerido.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Contraseña es requerido.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    // login por parte de stayaware donde lo unico que hace es comparar en la base de datos awaresystems
    private void userLogin(final String username) {
        class UserLoginClass extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Espere por favor", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    Boolean isDocente = false;
                    JSONObject obj = new JSONObject(s);
                    String idred = obj.getString("id_RedApoyo");
                    String docente = obj.getString("Docente");
                    if(docente.equalsIgnoreCase("1")) {
                        isDocente = true;
                    }
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(dataSet.sharedPreferences,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(dataSet.shared_id_red_apoyo, idred);
                    editor.putBoolean(dataSet.shared_docente, isDocente);
                    editor.commit();
                    Intent goToMenu = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(goToMenu);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<>();
                data.put("Usuario", params[0]);

                HttpQuerys ruc = new HttpQuerys();

                String result = ruc.sendPostRequest(LOGIN_URL, data);

                return result;
            }
        }
        UserLoginClass ulc = new UserLoginClass();
        ulc.execute(username);
    }

    //Registro (padres,tios) de stayaware lo unico que hace es almacenar en la base de datos awaresystems
    private void registrar(final String correo, final String clave, final String token){
        class UserLoginClass extends AsyncTask<String,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this,"Espere por favor",null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("username",params[0]);
                data.put("password",params[1]);
                data.put("token",params[2]);
                HttpQuerys ruc = new HttpQuerys();

                String result = ruc.sendPostRequest(REGISTER_URL,data);

                return result;
            }
        }
        UserLoginClass ulc = new UserLoginClass();
        ulc.execute(correo,clave,token);
    }
}