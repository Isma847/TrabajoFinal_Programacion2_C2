package Service;

import DAO.VideojuegoDao;
import Modelo.Videojuego;

import java.sql.SQLException;

public class VideojuegoService extends Service<Videojuego> {

    public VideojuegoService() throws SQLException {
        super(new VideojuegoDao());
    }

    @Override
    protected void validar(Videojuego v) {
        if(v.getGenero() == null) {
            throw new IllegalArgumentException("El genero es obligatorio");
        }
    }
}