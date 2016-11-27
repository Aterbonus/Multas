package cl.aterbonus.multas.modelos;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import cl.aterbonus.multas.utilidades.Item;
import cl.aterbonus.multas.utilidades.Itemizable;

/**
 * Created by root on 12-10-16.
 */

@DatabaseTable
public class TipoMulta extends BaseDaoEnabled implements Serializable, Itemizable<Integer, TipoMulta> {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String nombre;

    public TipoMulta() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Item<Integer, TipoMulta> getItem() {
        return new Item<>(id, this);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
