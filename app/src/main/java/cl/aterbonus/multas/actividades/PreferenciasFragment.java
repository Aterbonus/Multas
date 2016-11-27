package cl.aterbonus.multas.actividades;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cl.aterbonus.multas.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferenciasFragment extends PreferenceFragment {


    public PreferenciasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }



}
