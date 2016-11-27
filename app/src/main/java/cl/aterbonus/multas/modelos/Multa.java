package cl.aterbonus.multas.modelos;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by root on 12-10-16.
 */

@DatabaseTable
public class Multa extends BaseDaoEnabled implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(foreign = true, columnDefinition = "INTEGER REFERENCES Marca")
    private Marca marca;

    @DatabaseField(foreign = true, columnDefinition = "INTEGER REFERENCES Color")
    private Color color;

    @DatabaseField(foreign = true, columnDefinition = "INTEGER REFERENCES TipoMulta")
    private TipoMulta tipoMulta;

    @DatabaseField
    private String modelo;

    @DatabaseField
    private String patente;

    @DatabaseField
    private boolean esVehiculoEstatal;

    @DatabaseField
    private Date fecha;

    @DatabaseField
    private String direccion;

    @DatabaseField
    private String coorLatitud;

    @DatabaseField
    private String coorLongitud;

    public Multa() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public TipoMulta getTipoMulta() {
        return tipoMulta;
    }

    public void setTipoMulta(TipoMulta tipoMulta) {
        this.tipoMulta = tipoMulta;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public boolean isEsVehiculoEstatal() {
        return esVehiculoEstatal;
    }

    public void setEsVehiculoEstatal(boolean esVehiculoEstatal) {
        this.esVehiculoEstatal = esVehiculoEstatal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getCoorLatitud() {
        return coorLatitud;
    }

    public void setCoorLatitud(String coorLatitud) {
        this.coorLatitud = coorLatitud;
    }

    public String getCoorLongitud() {
        return coorLongitud;
    }

    public void setCoorLongitud(String coorLongitud) {
        this.coorLongitud = coorLongitud;
    }
}
