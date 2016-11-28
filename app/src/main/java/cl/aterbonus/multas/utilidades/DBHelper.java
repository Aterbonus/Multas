package cl.aterbonus.multas.utilidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import cl.aterbonus.multas.modelos.Color;
import cl.aterbonus.multas.modelos.Marca;
import cl.aterbonus.multas.modelos.Multa;
import cl.aterbonus.multas.modelos.TipoMulta;
import cl.aterbonus.multas.modelos.Usuario;

/**
 * Created by root on 10-10-16.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME = "multas.db";
    private static final int DATABASE_VERSION = 7;

    private Dao<Color, Integer> colorDao;
    private Dao<Marca, Integer> marcaDao;
    private Dao<TipoMulta, Integer> tipoMultaDao;
    private Dao<Multa, Integer> multaDao;
    private Dao<Usuario, Integer> usuarioDao;

    private final String[] colores = {
            "Rojo",
            "Azul",
            "Amarillo",
            "Gris",
            "Verde",
            "Purpura",
            "Naranjo",
            "Negro",
            "Blanco",
            "Café"
    };
    private final String[] marcas = {
            "Alfa Romeo",
            "Audi",
            "Baic",
            "BMW",
            "Brilliance",
            "BYD",
            "Changan",
            "Changhe",
            "Chery",
            "Chevrolet",
            "Chrysler",
            "Citroen",
            "Daihatsu",
            "DFM",
            "DFSK",
            "Dodge",
            "Ford",
            "Foton",
            "Gac Gonow",
            "Geely",
            "Iveco",
            "JAC",
            "Jaguar",
            "Jeep",
            "Junbei",
            "KIA",
            "Land Rover",
            "Landwind",
            "Lexus",
            "Mahindra",
            "Maserati",
            "Maxus",
            "Mazda",
            "Mercedes Benz",
            "MG",
            "Mini",
            "Mitsubishi Motors",
            "Nissan",
            "Opel",
            "Peugeot",
            "Porsche",
            "RAM",
            "Renault",
            "Skoda",
            "Ssangyong",
            "Subaru",
            "Suzuki",
            "Tata",
            "Toyota",
            "Volkswagen",
            "Volvo",
            "ZNA",
            "ZX Auto"
    };
    private final String[] tiposMulta = {
            "Estacionado cerca de Grifo",
            "Estacionado frente a Salida Vehículos",
            "Estacionado en lugar restringido",
            "Estacionado sobre vereda"
    };


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Color.class);
            /*Dao<Color, Integer> colorDao = getColorDao();
            for(String color : colores) {
                Color c = new Color();
                c.setNombre(color);
                colorDao.create(c);
            }*/

            TableUtils.createTable(connectionSource, Marca.class);
            /*Dao<Marca, Integer> marcaDao = getMarcaDao();
            for(String marca : marcas) {
                Marca m = new Marca();
                m.setNombre(marca);
                marcaDao.create(m);
            }*/

            TableUtils.createTable(connectionSource, TipoMulta.class);
            /*Dao<TipoMulta, Integer> tipoMultaDao = getTipoMultaDao();
            for(String tipoMulta : tiposMulta) {
                TipoMulta tm = new TipoMulta();
                tm.setNombre(tipoMulta);
                tipoMultaDao.create(tm);
            }*/

            TableUtils.createTable(connectionSource, Multa.class);

            TableUtils.createTable(connectionSource, Usuario.class);
            /*Dao<Usuario, Integer> usuarioDao = getUsuarioDao();
            Usuario usuario = new Usuario();
            usuario.setUsuario("usuario");
            usuario.setPassword("password");
            usuarioDao.create(usuario);*/
        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "Imposible crear la base de datos.", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {
            TableUtils.dropTable(connectionSource, Color.class, true);
            TableUtils.dropTable(connectionSource, Marca.class, true);
            TableUtils.dropTable(connectionSource, TipoMulta.class, true);
            TableUtils.dropTable(connectionSource, Multa.class, true);
            TableUtils.dropTable(connectionSource, Usuario.class, true);
            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "Imposible actualizar la base de datos desde la versión  " + oldVer
                    + " a la " + newVer, e);
        }
    }

    public Dao<Color, Integer> getColorDao() throws SQLException {
        if (colorDao == null) {
            colorDao = getDao(Color.class);
        }
        return colorDao;
    }

    public Dao<Marca, Integer> getMarcaDao() throws SQLException {
        if (marcaDao == null) {
            marcaDao = getDao(Marca.class);
        }
        return marcaDao;
    }

    public Dao<TipoMulta, Integer> getTipoMultaDao() throws SQLException {
        if (tipoMultaDao == null) {
            tipoMultaDao = getDao(TipoMulta.class);
        }
        return tipoMultaDao;
    }

    public Dao<Multa, Integer> getMultaDao() throws SQLException {
        if (multaDao == null) {
            multaDao = getDao(Multa.class);
        }
        return multaDao;
    }

    public Dao<Usuario, Integer> getUsuarioDao() throws SQLException {
        if (usuarioDao == null) {
            usuarioDao = getDao(Usuario.class);
        }
        return usuarioDao;
    }


}
