package cl.aterbonus.multas.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cl.aterbonus.multas.api.MultasRestClient;
import cl.aterbonus.multas.utilidades.DBHelper;
import cl.aterbonus.multas.R;
import cl.aterbonus.multas.utilidades.DialogoValidacion;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private EditText usuarioEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioEditText = (EditText) findViewById(R.id.usuarioEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void login() {

        loginButton.setEnabled(false);
        String usuario = usuarioEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        JsonObject params = new JsonObject();
        try {
            params.addProperty("user", usuario);
            params.addProperty("password", password);
            MultasRestClient.post(this, "login", params.toString(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if(response != null) {
                        try {
                            if(LoginActivity.this.databaseList().length > 0
                                    && !LoginActivity.this.deleteDatabase(DBHelper.DATABASE_NAME)) {
                                Toast.makeText(LoginActivity.this, "Error al borrar la base de datos", Toast.LENGTH_SHORT).show();
                            }
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                            editor.putString(getString(R.string.api_token), response.getString("api_token"));
                            editor.commit();
                            finish();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    loginButton.setEnabled(true);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if(statusCode == 401) {
                        Toast.makeText(LoginActivity.this, "El usuario y/o contraseña no coinciden", Toast.LENGTH_SHORT).show();
                    } else if (statusCode == 422){
                        DialogoValidacion.mostrarDialogoErrorValidacion(errorResponse, getFragmentManager());
                    } else {
                        Toast.makeText(LoginActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        throwable.printStackTrace();
                    }
                    loginButton.setEnabled(true);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
