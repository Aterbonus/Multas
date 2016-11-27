package cl.aterbonus.multas.actividades;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import cl.aterbonus.multas.utilidades.DBHelper;
import cl.aterbonus.multas.R;
import cl.aterbonus.multas.modelos.Multa;

public class ListadoActivity extends AppCompatActivity {

    private ListView marcasListView;
    private DBHelper helper;
    private MultasListAdapter multasListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        marcasListView = (ListView) findViewById(R.id.marcasListView);
        registerForContextMenu(marcasListView);

        helper = new DBHelper(this);

        mostrarMultas();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId() == R.id.marcasListView) {
            MenuInflater menuInflater = new MenuInflater(this);
            Multa multa = multasListAdapter.getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
            menu.setHeaderTitle(multa.getId() + " - " + multa.getTipoMulta().getNombre());
            menuInflater.inflate(R.menu.menu_contexto_listado_multas, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_eliminar_multa:
                AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Multa multaSeleccionada =  multasListAdapter.getItem(info.position);
                try {
                    multaSeleccionada.delete();
                    multasListAdapter.eliminarItem(info.position);
                    Toast.makeText(this, "Multa Eliminada", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    Toast.makeText(this, "Error al eliminar la Multa", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void mostrarMultas() {
        try {
            multasListAdapter = new MultasListAdapter(this, obtenerMultas());
            marcasListView.setAdapter(multasListAdapter);
        } catch (SQLException e) {
            Toast.makeText(this, "Se ha producido un error al obtener las Multas", Toast.LENGTH_SHORT).show();
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
                "fecha"
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

    private class MultasListAdapter extends BaseAdapter {

        private Context context;
        private List<Multa> multasList;
        private LayoutInflater inflater;

        public MultasListAdapter(Context context, List<Multa> multasList) {
            this.context = context;
            this.multasList = multasList;
        }

        @Override
        public int getCount() {
            return multasList.size();
        }

        @Override
        public Multa getItem(int i) {
            return multasList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public void eliminarItem(int i) {
            multasList.remove(i);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (inflater == null)
                inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (view == null)
                view = inflater.inflate(android.R.layout.simple_list_item_2, null);

            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
            TextView text2 = (TextView) view.findViewById(android.R.id.text2);
            Multa multa = getItem(i);

            Calendar calendario = Calendar.getInstance();
            calendario.setTime(multa.getFecha());
            text1.setText("Multa " + multa.getId() + " - " + multa.getTipoMulta().getNombre() + " - " +
                    calendario.get(Calendar.DAY_OF_MONTH) + "/" + (calendario.get(Calendar.MONTH) + 1) + "/" + calendario.get(Calendar.YEAR));
            text2.setText("Marca: " + multa.getMarca().getNombre() +
                    " | Modelo: " + multa.getModelo() +
                    " | Patente: " + multa.getPatente() +
                    " | Color: " + multa.getColor().getNombre() +
                    " | Vehículo Estatal: " + (multa.isEsVehiculoEstatal() ? "Sí" : "No"));

            return view;
        }
    }

}
