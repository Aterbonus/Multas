package cl.aterbonus.multas.actividades;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import cl.aterbonus.multas.R;
import cl.aterbonus.multas.api.MultasRestClient;
import cl.aterbonus.multas.modelos.Multa;
import cl.aterbonus.multas.utilidades.MultaTypeAdapter;
import cl.aterbonus.multas.utilidades.MultasListAdapter;
import cz.msebera.android.httpclient.Header;

import static cl.aterbonus.multas.utilidades.Helper.toast;

public class ListadoWebServiceActivity extends AppCompatActivity {

    private MultasListAdapter multasListAdapter;
    private Gson gsonMultas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_webservice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView multasListView = (ListView) findViewById(R.id.multasListView);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Multa.class, new MultaTypeAdapter());
        gsonMultas = builder.create();

        multasListAdapter = new MultasListAdapter(this, new ArrayList<Multa>());
        multasListView.setAdapter(multasListAdapter);

        obtenerMultas(0);

    }

    public void obtenerMultas(int page) {
        MultasRestClient.get("multas?page=" + page, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Multa[] multas = gsonMultas.fromJson(response.toString(), Multa[].class);
                    multasListAdapter.addItems(Arrays.asList(multas));
                } catch (JsonSyntaxException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Multa[] multas = gsonMultas.fromJson(response.getJSONArray("data").toString(), Multa[].class);
                    multasListAdapter.addItems(Arrays.asList(multas));
                } catch (JSONException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                toast(ListadoWebServiceActivity.this, "Hubo un problema al obtener los datos desde el WebService");
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                toast(ListadoWebServiceActivity.this, "Hubo un problema al obtener los datos desde el WebService");
                throwable.printStackTrace();
            }
        });
    }
}
