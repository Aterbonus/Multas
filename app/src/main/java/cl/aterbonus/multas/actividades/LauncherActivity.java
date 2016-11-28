package cl.aterbonus.multas.actividades;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cl.aterbonus.multas.R;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i;
        if(PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.api_token), "").length() > 0) {
            i = new Intent(this, HomeActivity.class);
        } else {
            i = new Intent(this, LoginActivity.class);
        }
        finish();
        startActivity(i);
    }
}
