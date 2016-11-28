package cl.aterbonus.multas.actividades;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cl.aterbonus.multas.api.MultasRestClient;
import cl.aterbonus.multas.utilidades.DBHelper;
import cl.aterbonus.multas.R;
import cl.aterbonus.multas.modelos.Color;
import cl.aterbonus.multas.modelos.Marca;
import cl.aterbonus.multas.modelos.Multa;
import cl.aterbonus.multas.modelos.TipoMulta;
import cl.aterbonus.multas.utilidades.DialogoValidacion;
import cl.aterbonus.multas.utilidades.Identificable;
import cl.aterbonus.multas.utilidades.MultaTypeAdapter;
import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private final int PETICION_PERMISO_LOCALIZACION = 1;
    private DBHelper helper;
    private Spinner colorSpinner;
    private Spinner marcaSpinner;
    private Spinner tipoMultaSpinner;
    private Switch esVehiculoEstatalSwitch;
    private EditText fechaEditText;
    private EditText modeloEditText;
    private EditText patenteEditText;
    private EditText direccionEditText;
    private SharedPreferences sharedPreferences;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private Multa ultimaMulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        colorSpinner = (Spinner) findViewById(R.id.colorSpinner);
        marcaSpinner = (Spinner) findViewById(R.id.marcaSpinner);
        tipoMultaSpinner = (Spinner) findViewById(R.id.tipoMultaSpinner);
        esVehiculoEstatalSwitch = (Switch) findViewById(R.id.esVehiculoEstatalSwitch);
        fechaEditText = (EditText) findViewById(R.id.fechaEditText);
        modeloEditText = (EditText) findViewById(R.id.modeloEditText);
        patenteEditText = (EditText) findViewById(R.id.patenteEditText);
        direccionEditText = (EditText) findViewById(R.id.direccionEditText);
        final Context context = this;
        helper = new DBHelper(context);

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anio, int mes, int dia) {
                actualizarFechaEditText(dia, mes, anio);
            }
        };

        fechaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendario = Calendar.getInstance();

                DatePickerDialog dialog = new DatePickerDialog(
                        context,
                        dateSetListener,
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)
                );
                DatePicker datePicker = dialog.getDatePicker();
                datePicker.setMinDate(calendario.getTimeInMillis() - 1000);
                dialog.show();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        MultasRestClient.setToken(sharedPreferences.getString(getString(R.string.api_token), ""));

        inicializarFecha();
        popularColorSpinner();
        popularMarcaSpinner();
        popularTipoMultaSpinner();


        if (sharedPreferences.getBoolean(getString(R.string.cargar_multa), false)) {
            cargarMulta();
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();


    }

    private void inicializarFecha() {
        Calendar calendario = Calendar.getInstance();
        actualizarFechaEditText(calendario.get(Calendar.DAY_OF_MONTH), calendario.get(Calendar.MONTH), calendario.get(Calendar.YEAR));
    }


    private void actualizarFechaEditText(int dia, int mes, int anio) {
        fechaEditText.setText(String.format("%d/%d/%d", dia, mes + 1, anio));
    }

    private void popularColorSpinner() {
        try {
            final Dao<Color, Integer> colorDao = helper.getColorDao();

            if (colorDao.countOf() <= 0) {
                popularSpinnerDesdeWebService("colores", colorSpinner, colorDao, Color[].class);
            } else {
                popularSpinner(colorSpinner, colorDao, new String[]{"id", "nombre"});
            }
        } catch (SQLException e) {
            Toast.makeText(this, "Error al cargar los Colores.", Toast.LENGTH_SHORT).show();
        }
    }

    private <V> void popularSpinner(Spinner spinner, Dao<V, ?> dao, String[] columns) throws SQLException {
        spinner.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        dao.queryBuilder().selectColumns(columns).query()
                )
        );
    }

    private <V> void popularSpinner(Spinner spinner, List<V> items) {
        spinner.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        items
                )
        );
    }

    private <V> void popularSpinnerDesdeWebService(String url, final Spinner spinner, final Dao<V, ?> dao, final Class<V[]> classArray) {
        MultasRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new Gson();
                    List<V> objetos = Arrays.asList(gson.fromJson(response.toString(), classArray));
                    try {
                        dao.create(objetos);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, "Error al guardar los datos provenientes del WebService", Toast.LENGTH_SHORT).show();
                    }
                    popularSpinner(spinner, objetos);
                } catch (JsonSyntaxException ex) {
                    Toast.makeText(HomeActivity.this, "Error al leer los datos provenientes del WebService", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(HomeActivity.this, "Error al cargar los Datos desde el WebService.", Toast.LENGTH_SHORT).show();
                try {
                    Log.e("ApiRest", throwable.getMessage() + ":\n" + (errorResponse != null ? errorResponse.toString(4) : null));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(HomeActivity.this, "Error al cargar los Datos desde el WebService.", Toast.LENGTH_SHORT).show();
                Log.e("ApiRest", throwable.getMessage() +
                        "\nStatus Code: " + statusCode +
                        "\nHeaders: " + headers +
                        "\nResponse String: " + responseString);
            }
        });
    }

    private void popularMarcaSpinner() {
        try {
            Dao<Marca, Integer> marcaDao = helper.getMarcaDao();

            if (marcaDao.countOf() <= 0) {
                popularSpinnerDesdeWebService("marcas", marcaSpinner, marcaDao, Marca[].class);
            } else {
                popularSpinner(marcaSpinner, marcaDao, new String[]{"id", "nombre"});
            }
        } catch (SQLException ex) {
            Toast.makeText(this, "Error al cargar las Marcas.", Toast.LENGTH_SHORT).show();
        }
    }

    private void popularTipoMultaSpinner() {
        try {
            Dao<TipoMulta, Integer> tipoMultaDao = helper.getTipoMultaDao();

            if (tipoMultaDao.countOf() <= 0) {
                popularSpinnerDesdeWebService("tiposMulta", tipoMultaSpinner, tipoMultaDao, TipoMulta[].class);
            } else {
                popularSpinner(tipoMultaSpinner, tipoMultaDao, new String[]{"id", "nombre"});
            }
        } catch (SQLException ex) {
            Toast.makeText(this, "Error al cargar los Tipos de Multa.", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarMulta() {
        try {
            Multa multa = helper.getMultaDao().queryBuilder().orderBy("id", false).queryForFirst();
            if (multa != null) {
                Color color = multa.getColor();
                color.refresh();
                TipoMulta tipoMulta = multa.getTipoMulta();
                tipoMulta.refresh();
                Marca marca = multa.getMarca();
                marca.refresh();
                setSelected(colorSpinner, multa.getColor());
                setSelected(marcaSpinner, multa.getMarca());
                setSelected(tipoMultaSpinner, multa.getTipoMulta());
                esVehiculoEstatalSwitch.setChecked(multa.isEsVehiculoEstatal());
                modeloEditText.setText(multa.getModelo());
                patenteEditText.setText(multa.getPatente());
                direccionEditText.setText(multa.getDireccion());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(multa.getFecha());
                actualizarFechaEditText(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void setSelected(Spinner spinner, Identificable item) {
        ArrayAdapter<Identificable> arrayAdapter = (ArrayAdapter<Identificable>) spinner.getAdapter();
        int length = arrayAdapter.getCount();
        for (int i = 0; i < length; i++) {
            if (arrayAdapter.getItem(i).getId() == item.getId()) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void guardarMulta() {
        String modelo = modeloEditText.getText().toString();
        String patente = patenteEditText.getText().toString();
        String direccion = direccionEditText.getText().toString();
        if (modelo.isEmpty() || patente.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "No se permiten campos vacíos. No se ha guardo la multa.", Toast.LENGTH_SHORT).show();
            return;
        }
        Multa multa = new Multa();
        multa.setColor((Color) colorSpinner.getSelectedItem());
        multa.setMarca((Marca) marcaSpinner.getSelectedItem());
        multa.setTipoMulta((TipoMulta) tipoMultaSpinner.getSelectedItem());
        multa.setEsVehiculoEstatal(esVehiculoEstatalSwitch.isChecked());
        multa.setModelo(modelo);
        multa.setPatente(patente);
        multa.setDireccion(direccion);
        try {
            multa.setFecha(new SimpleDateFormat("dd/MM/yyyy").parse(fechaEditText.getText().toString()));
        } catch (ParseException e) {
            Toast.makeText(this, "ERROR FECHA", Toast.LENGTH_SHORT).show();
            return;
        }
        if(checkearPermisoGps()) {
            lastLocation = obtenerLastLocation();
        }
        if (lastLocation != null) {
            String longitud = String.valueOf(lastLocation.getLongitude());
            String latitud = String.valueOf(lastLocation.getLatitude());
            multa.setCoorLongitud(longitud.substring(0, Math.min(14, longitud.length())));
            multa.setCoorLatitud(latitud.substring(0, Math.min(14, latitud.length())));
        } else {
            multa.setCoorLongitud("Desconocida");
            multa.setCoorLatitud("Desconocida");
        }
        ultimaMulta = multa;
        persistirMulta(multa);
    }

    private void persistirMulta(final Multa multa){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Multa.class, new MultaTypeAdapter());
        Gson gson = builder.create();
        try {
            MultasRestClient.post(this, "multas", gson.toJson(multa), new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Toast.makeText(HomeActivity.this, "Multa subida al servidor.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
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
                                    "\nHeaders: " + headers +
                                    "\nRespuesta:\n" + (errorResponse != null ? errorResponse.toString(4) : null));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        persistirMultaLocal(multa);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.e("ApiRest", throwable.getMessage() +
                            "\nStatus Code: " + statusCode +
                            "\nHeaders: " + headers +
                            "\nResponse String: " + responseString);
                    persistirMultaLocal(multa);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void persistirMultaLocal(Multa multa) {
        try {
            if (helper.getMultaDao().create(multa) > 0) {
                Toast.makeText(this, "Multa creada con éxito", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al crear la Multa", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException ex) {
            Toast.makeText(this, "Error al crear la Multa", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.evitar_suspension), false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_guardar:
                guardarMulta();
                return true;
            case R.id.action_limpiar:
                limpiar();
                return true;
            case R.id.action_configuracion:
                startActivity(new Intent(this, PreferenciasActivity.class));
                return true;
            case R.id.action_listado:
                startActivity(new Intent(this, ListadoActivity.class));
                return true;
            case R.id.action_acerca:
                startActivity(new Intent(this, AcercaDeActivity.class));
                return true;
            case R.id.action_desloguear:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.api_token), "");
                editor.commit();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void limpiar() {
        colorSpinner.setSelection(0);
        marcaSpinner.setSelection(0);
        tipoMultaSpinner.setSelection(0);
        esVehiculoEstatalSwitch.setChecked(false);
        inicializarFecha();
        modeloEditText.setText("");
        patenteEditText.setText("");
        direccionEditText.setText("");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Conexión con Google Services fallida.", Toast.LENGTH_SHORT).show();
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected(Bundle bundle) {
        if (!checkearPermisoGps()) {
            solicitarPermisoGps();
            return;
        }
        lastLocation = obtenerLastLocation();
    }

    public boolean checkearPermisoGps() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void solicitarPermisoGps() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PETICION_PERMISO_LOCALIZACION
        );
    }

    @SuppressWarnings("MissingPermission")
    private Location obtenerLastLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                lastLocation = obtenerLastLocation();

            } else {
                Snackbar.make(
                        getWindow().getDecorView().getRootView(),
                        "No me diste permiso para usar el GPS :(",
                        Snackbar.LENGTH_SHORT).setAction("Dar permiso", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                solicitarPermisoGps();
                            }
                        }
                ).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Conexión con Google Services se ha suspendido.", Toast.LENGTH_SHORT).show();
    }
}
