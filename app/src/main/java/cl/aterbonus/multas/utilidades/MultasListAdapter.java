package cl.aterbonus.multas.utilidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import cl.aterbonus.multas.modelos.Multa;

/**
 * Created by aterbonus on 29-11-16.
 */


public class MultasListAdapter extends BaseAdapter {

    private Context context;
    private List<Multa> multasList;
    private LayoutInflater inflater;

    public MultasListAdapter(Context context, List<Multa> multasList) {
        this.context = context;
        this.multasList = multasList;
    }

    public void addItems(Collection<Multa> multas) {
        multasList.addAll(multas);
        notifyDataSetChanged();
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

    public void eliminarItem(Multa multa) {
        multasList.remove(multa);
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
