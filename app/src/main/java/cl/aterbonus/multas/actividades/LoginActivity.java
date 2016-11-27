package cl.aterbonus.multas.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cl.aterbonus.multas.utilidades.DBHelper;
import cl.aterbonus.multas.R;

public class LoginActivity extends AppCompatActivity {

    private EditText usuarioEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioEditText = (EditText) findViewById(R.id.usuarioEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);

        helper = new DBHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void login() {
        String usuario = usuarioEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.query(
                    "Usuario",
                    new String[]{"id"},
                    "usuario = ? AND password = ?",
                    new String[]{usuario, password},
                    null,
                    null,
                    null);
        if(c.getCount() != 0) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean(getString(R.string.logueado), true);
            editor.commit();
            c.close();
            db.close();
            finish();
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            Toast.makeText(this, "Usuario y/o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
        c.close();
        db.close();
    }

}
