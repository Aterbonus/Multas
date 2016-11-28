package cl.aterbonus.multas.utilidades;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


/**
 * Created by aterbonus on 28-11-16.
 */

public class DialogoValidacion extends DialogFragment {

    private String mensaje;

    public static void mostrarDialogoErrorValidacion(JSONObject errores, android.app.FragmentManager manager) {
        Bundle bundle = new Bundle();
        bundle.putString("errores", errores.toString());
        DialogoValidacion dialogo =  new DialogoValidacion();
        dialogo.setArguments(bundle);
        dialogo.show(manager, "DialogoValidacio");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();
        JSONObject errores = null;
        String erroresString = "Errores de validación";
        if(bundle != null) {
            try {
                errores = new JSONObject(bundle.getString("errores", "{}"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(errores != null) {
            Iterator<String> keys = errores.keys();
            erroresString = "";
            while(keys.hasNext()) {
                try {
                    String key = keys.next();
                    erroresString += key + ":\n";
                    JSONArray arrayErrores = errores.getJSONArray(key);
                    int length = arrayErrores.length();
                    for(int i = 0; i < length; ++i) {
                        erroresString += "\t - " + arrayErrores.getString(i) + "\n";
                    }
                    erroresString += '\n';
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        builder.setMessage(erroresString)
                .setTitle("Errores de validación")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }
}
