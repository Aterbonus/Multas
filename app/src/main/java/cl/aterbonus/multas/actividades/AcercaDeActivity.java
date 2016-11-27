package cl.aterbonus.multas.actividades;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cl.aterbonus.multas.R;

public class AcercaDeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView link = (TextView) findViewById(R.id.linkEditText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            link.setText(Html.fromHtml(getString(R.string.github_link), Html.FROM_HTML_MODE_COMPACT));
        } else {
            link.setText(Html.fromHtml(getString(R.string.github_link)));
        }
        link.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
