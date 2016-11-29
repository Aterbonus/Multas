package cl.aterbonus.multas.actividades;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cl.aterbonus.multas.api.MultasRestClient;
import cl.aterbonus.multas.utilidades.DBHelper;
import cl.aterbonus.multas.R;
import cl.aterbonus.multas.modelos.Multa;
import cl.aterbonus.multas.utilidades.DialogoValidacion;
import cl.aterbonus.multas.utilidades.MultaTypeAdapter;
import cl.aterbonus.multas.utilidades.MultasListAdapter;
import cz.msebera.android.httpclient.Header;

import static cl.aterbonus.multas.utilidades.Helper.toast;

public class ListadoActivity extends AppCompatActivity {

    private ListView multasListView;
    private DBHelper helper;
    private MultasListAdapter multasListAdapter;
    private Gson gsonMultas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        multasListView = (ListView) findViewById(R.id.multasListView);
        registerForContextMenu(multasListView);

        helper = new DBHelper(this);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Multa.class, new MultaTypeAdapter());
        gsonMultas = builder.create();

        mostrarMultas();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_upload_multas:
                if(multasListAdapter.getCount() > 0) {
                    subirMultas();
                } else {
                    toast(this, "No hay multas que subir al Servidor");
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void subirMultas() {

        int length = multasListAdapter.getCount();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        final List<RequestHandle> requestHandlers = new ArrayList<>();
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Subiendo multas al WebService...");
        progressDialog.setMax(length);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(0);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.i("ApiRest", "Cancelando peticiones...");
                for(RequestHandle handler : requestHandlers) {
                    if(!handler.isFinished()) {
                        handler.cancel(true);
                    }
                }
            }
        });
        for(int i = 0; i < length; ++i) {
                final Multa multa = multasListAdapter.getItem(i);
                try {
                    requestHandlers.add(MultasRestClient.post(this, "multas", gsonMultas.toJson(multa), new JsonHttpResponseHandler() {
                        private boolean exitoso = false;
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            exitoso = true;
                            try {
                                multa.delete();
                                multasListAdapter.eliminarItem(multa);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            if(statusCode == 422) {
                                try {
                                    Log.e("ApiRest", throwable.getMessage() + ":\n" + errorResponse.toString(4));
                                    DialogoValidacion.mostrarDialogoErrorValidacion(errorResponse, getFragmentManager());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    Log.e("ApiRest", throwable.getMessage() +
                                            "\nStatus Code: " + statusCode +
                                            "\nHeaders: " + Arrays.toString(headers) +
                                            "\nRespuesta:\n" + (errorResponse != null ? errorResponse.toString(4) : null));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.e("ApiRest", throwable.getMessage() +
                                    "\nStatus Code: " + statusCode +
                                    "\nHeaders: " + Arrays.toString(headers) +
                                    "\nResponse String: " + responseString);
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            if(exitoso) {
                                progressDialog.incrementProgressBy(1);
                            }
                            progressDialog.incrementSecondaryProgressBy(1);
                            if(progressDialog.getSecondaryProgress() >= progressDialog.getMax()) {
                                progressDialog.setMessage("Se han logrado subir " + progressDialog.getProgress()
                                        + " multas");
                            }
                        }
                    }));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
        }
        progressDialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId() == R.id.multasListView) {
            MenuInflater menuInflater = new MenuInflater(this);
            Multa multa = multasListAdapter.getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
            menu.setHeaderTitle(multa.getId() + " - " + multa.getTipoMulta().getNombre());
            menuInflater.inflate(R.menu.menu_contexto_listado_multas, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Multa multaSeleccionada =  multasListAdapter.getItem(info.position);

        switch (item.getItemId()) {
            case R.id.action_eliminar_multa:
                try {
                    multaSeleccionada.delete();
                    multasListAdapter.eliminarItem(info.position);
                    toast(this, "Multa Eliminada");
                } catch (SQLException e) {
                    toast(this, "Error al eliminar la Multa");
                }
                return true;
            case R.id.action_upload_multa:
                subirMulta(multaSeleccionada);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void subirMulta(final Multa multa) {
        try {
            MultasRestClient.post(this, "multas", gsonMultas.toJson(multa), new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    toast(ListadoActivity.this, "Multa subida al servidor.");
                    try {
                        multa.delete();
                        multasListAdapter.eliminarItem(multa);
                    } catch (SQLException ex) {
                        toast(ListadoActivity.this, "Hubo un error al eliminar la multa de forma local.");
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if(statusCode == 422) {
                        try {
                            Log.e("ApiRest", throwable.getMessage() + ":\n" + errorResponse.toString(4));
                            DialogoValidacion.mostrarDialogoErrorValidacion(errorResponse, getFragmentManager());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mostrarSnackbarReintentar(multa);
                            Log.e("ApiRest", throwable.getMessage() +
                                    "\nStatus Code: " + statusCode +
                                    "\nHeaders: " + Arrays.toString(headers) +
                                    "\nRespuesta:\n" + (errorResponse != null ? errorResponse.toString(4) : null));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("ApiRest", throwable.getMessage() +
                            "\nStatus Code: " + statusCode +
                            "\nHeaders: " + Arrays.toString(headers) +
                            "\nResponse String: " + responseString);
                    mostrarSnackbarReintentar(multa);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void mostrarSnackbarReintentar(final Multa multa) {
        Snackbar.make(
                getWindow().getDecorView().getRootView(),
                "Hubo un error al subir la multa al servidor",
                Snackbar.LENGTH_LONG).setAction("Reintentar", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        subirMulta(multa);
                    }
                }
        ).show();
    }

    private void mostrarMultas() {
        try {
            multasListAdapter = new MultasListAdapter(this, obtenerMultas());
            multasListView.setAdapter(multasListAdapter);
        } catch (SQLException e) {
            toast(this, "Se ha producido un error al obtener las Multas");
        }
    }

    private List<Multa> obtenerMultas() throws SQLException {
        Dao<Multa, Integer> multaDao = helper.getMultaDao();
        List<Multa> multas = multaDao.queryBuilder().selectColumns(
                "id",
                "marca_id",
                "color_id",
                "tipoMulta_id",
                "modelo",
                "patente",
                "esVehiculoEstatal",
                "fecha",
                "direccion",
                "coorLatitud",
                "coorLongitud"
        ).query();
        for (Multa multa : multas) {
            multa.getColor().refresh();
            multa.getMarca().refresh();
            multa.getTipoMulta().refresh();
        }
        return multas;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.evitar_suspension), false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

}
