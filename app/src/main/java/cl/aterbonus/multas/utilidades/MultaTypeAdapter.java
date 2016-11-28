package cl.aterbonus.multas.utilidades;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;

import cl.aterbonus.multas.api.MultasRestClient;
import cl.aterbonus.multas.modelos.Color;
import cl.aterbonus.multas.modelos.Marca;
import cl.aterbonus.multas.modelos.Multa;
import cl.aterbonus.multas.modelos.TipoMulta;

/**
 * Created by aterbonus on 27-11-16.
 */

public class MultaTypeAdapter extends TypeAdapter<Multa> {
    @Override
    public void write(JsonWriter out, Multa value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        out.name("id").value(value.getId());
        out.name("color").value(value.getColor().getNombre());
        out.name("marca").value(value.getMarca().getNombre());
        out.name("tipoMulta").value(value.getTipoMulta().getNombre());
        out.name("direccion").value(value.getDireccion());
        out.name("modelo").value(value.getModelo());
        out.name("patente").value(value.getPatente());
        out.name("fecha").value(MultasRestClient.dateFormat.format(value.getFecha()));
        out.name("esVehiculoEstatal").value(value.isEsVehiculoEstatal());
        out.name("coorLatitud").value(value.getCoorLatitud());
        out.name("coorLongitud").value(value.getCoorLongitud());
        out.endObject();
    }

    @Override
    public Multa read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        final Multa multa = new Multa();
        in.beginObject();
        while(in.hasNext()) {
            switch(in.nextName()) {
                case "id":
                    multa.setId(in.nextInt());
                    break;
                case "color":
                    Color color = new Color();
                    color.setNombre(in.nextString());
                    multa.setColor(color);
                    break;
                case "marca":
                    Marca marca = new Marca();
                    marca.setNombre(in.nextString());
                    multa.setMarca(marca);
                    break;
                case "tipoMulta":
                    TipoMulta tipoMulta = new TipoMulta();
                    tipoMulta.setNombre(in.nextString());
                    multa.setTipoMulta(tipoMulta);
                    break;
                case "direccion":
                    multa.setDireccion(in.nextString());
                    break;
                case "modelo":
                    multa.setModelo(in.nextString());
                    break;
                case "patente":
                    multa.setPatente(in.nextString());
                    break;
                case "fecha":
                    try {
                        multa.setFecha(MultasRestClient.dateFormat.parse(in.nextString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case "esVehiculoEstatal":
                    multa.setEsVehiculoEstatal(in.nextBoolean());
                    break;
                case "coorLatitud":
                    multa.setCoorLatitud(in.nextString());
                    break;
                case "coorLongitud":
                    multa.setCoorLongitud(in.nextString());
                    break;
            }
        }
        in.endObject();
        return multa;

    }
}
