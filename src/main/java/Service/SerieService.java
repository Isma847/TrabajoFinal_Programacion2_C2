package Service;

import DAO.SerieDao;
import Modelo.Serie;

import java.sql.SQLException;

public class SerieService extends Service<Serie> {

    public SerieService() throws SQLException {
        super(new SerieDao());
    }

    @Override
    protected void validar(Serie s) {
        if(s.getGenero() == null) {
            throw new IllegalArgumentException("El género es obligatorio");
        }
    }
}