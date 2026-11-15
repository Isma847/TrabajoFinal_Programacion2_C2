package Service;

import DAO.PeliculaDao;
import Modelo.Pelicula;

import java.sql.SQLException;

public class PeliculaService extends Service<Pelicula> {

    public PeliculaService() throws SQLException {
        super(new PeliculaDao());
    }

    @Override
    protected void validar(Pelicula p) {
        if(p.getGenero() == null) {
            throw new IllegalArgumentException("El género es obligatorio");
        }
        if(p.getClasificacionEdad() == null) {
            throw new IllegalArgumentException("El clasificacion es obligatorio");
        }
    }
}