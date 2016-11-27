package cl.aterbonus.multas.actividades;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cl.aterbonus.multas.utilidades.DBHelper;
import cl.aterbonus.multas.R;
import cl.aterbonus.multas.modelos.Color;
import cl.aterbonus.multas.modelos.Marca;
import cl.aterbonus.multas.modelos.Multa;
import cl.aterbonus.multas.modelos.TipoMulta;
import cl.aterbonus.multas.utilidades.Item;
import cl.aterbonus.multas.utilidades.Itemizable;

public class HomeActivity extends AppCompatActivity {

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

                DatePickerDialog dialog = new DatePickerDialog(context,
                        dateSetListener,
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH));
                DatePicker datePicker = dialog.getDatePicker();
                datePicker.setMinDate(calendario.getTimeInMillis() - 1000);
                dialog.show();
            }
        });

        inicializarFecha();
        popularColorSpinner();
        popularMarcaSpinner();
        popularTipoMultaSpinner();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean(getString(R.string.cargar_multa), false)) {
            cargarMulta();
        }

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
            colorSpinner.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item, modelListToItemList(
                        helper.getColorDao().queryBuilder().selectColumns("id", "nombre").query()
                    )
                )
            );
        } catch (SQLException e) {
            Toast.makeText(this, "Error al cargar los Colores.", Toast.LENGTH_SHORT).show();
        }
    }

    private <K, V> ArrayList<Item<K, V>> modelListToItemList(List<? extends Itemizable<K, V>> itemizablesList) {
        ArrayList<Item<K, V>> itemsArrayList = new ArrayList<>();
        for(Itemizable<K, V> itemizable : itemizablesList) {
            itemsArrayList.add(itemizable.getItem());
        }
        return itemsArrayList;
    }

    private void popularMarcaSpinner() {
        try {
            marcaSpinner.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item, modelListToItemList(
                        helper.getMarcaDao().queryBuilder().selectColumns("id", "nombre").query()
                    )
                )
            );
        } catch (SQLException ex) {
            Toast.makeText(this, "Error al cargar las Marcas.", Toast.LENGTH_SHORT).show();
        }
    }

    private void popularTipoMultaSpinner() {
        try {
            tipoMultaSpinner.setAdapter(new ArrayAdapter<>(
                    this,
                        android.R.layout.simple_spinner_dropdown_item, modelListToItemList(
                        helper.getTipoMultaDao().queryBuilder().selectColumns("id", "nombre").query()
                    )
                )
            );
        } catch (SQLException ex) {
            Toast.makeText(this, "Error al cargar las Marcas.", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarMulta() {
        try {
            Multa multa = helper.getMultaDao().queryBuilder().orderBy("id", false).queryForFirst();
            Color color = multa.getColor();
            color.refresh();
            TipoMulta tipoMulta = multa.getTipoMulta();
            tipoMulta.refresh();
            Marca marca = multa.getMarca();
            marca.refresh();
            setSelected(colorSpinner, multa.getColor().getItem());
            setSelected(marcaSpinner, multa.getMarca().getItem());
            setSelected(tipoMultaSpinner, multa.getTipoMulta().getItem());
            esVehiculoEstatalSwitch.setChecked(multa.isEsVehiculoEstatal());
            modeloEditText.setText(multa.getModelo());
            patenteEditText.setText(multa.getPatente());
            direccionEditText.setText(multa.getDireccion());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(multa.getFecha());
            actualizarFechaEditText(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void setSelected(Spinner spinner, Item item) {
        //TODO Encontrar una manera menos hack de seleccionar el item
        ArrayAdapter<Item> arrayAdapter = (ArrayAdapter<Item>) spinner.getAdapter();
        int length = arrayAdapter.getCount();
        for(int i = 0; i < length; i++) {
            if(arrayAdapter.getItem(i).hiddenValue() == item.hiddenValue()) {
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
        if(modelo.isEmpty() || patente.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "No se permiten campos vacíos. No se ha guardo la multa.", Toast.LENGTH_SHORT).show();
            return;
        }
        Multa multa = new Multa();
        multa.setColor(((Item<Integer, Color>)colorSpinner.getSelectedItem()).displayValue());
        multa.setMarca(((Item<Integer, Marca>)marcaSpinner.getSelectedItem()).displayValue());
        multa.setTipoMulta(((Item<Integer, TipoMulta>)tipoMultaSpinner.getSelectedItem()).displayValue());
        multa.setEsVehiculoEstatal(esVehiculoEstatalSwitch.isChecked());
        multa.setModelo(modelo);
        multa.setPatente(patente);
        multa.setDireccion(direccion);
        try {
            multa.setFecha(new SimpleDateFormat("dd/MM/yyyy").parse(fechaEditText.getText().toString()));
        } catch (ParseException e) {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if(helper.getMultaDao().create(multa) > 0) {
                Toast.makeText(this, "Multa creada con éxito", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al crear la Multa", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
                editor.putBoolean(getString(R.string.logueado), false);
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

}
