package cl.aterbonus.multas.modelos;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import cl.aterbonus.multas.utilidades.Item;
import cl.aterbonus.multas.utilidades.Itemizable;

/**
 * Created by root on 10-10-16.
 */

@DatabaseTable
public class Marca extends BaseDaoEnabled implements Serializable, Itemizable<Integer, Marca> {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String nombre;

    public Marca() {

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
    public Item<Integer, Marca> getItem() {
        return new Item<>(id, this);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
