package cl.aterbonus.multas.modelos;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by root on 13-10-16.
 */
@DatabaseTable
public class Usuario extends BaseDaoEnabled implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(canBeNull = false, uniqueIndex = true)
    private String usuario;

    @DatabaseField(canBeNull = false)
    private String password;

    public Usuario() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
