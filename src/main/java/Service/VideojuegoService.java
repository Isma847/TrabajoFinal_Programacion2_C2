package Service;

import DAO.VideojuegoDao;
import Enumerados.GeneroVideojuegos;
import Modelo.Videojuego;

import java.sql.SQLException;
import java.util.ArrayList;

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

    public ArrayList<Videojuego> filtrarPorGenero(GeneroVideojuegos genero, boolean baja) {
        ArrayList<Videojuego> resultado = new ArrayList<>();
        for(Videojuego v : getLista(baja)) {
            if(v.getGenero() == genero) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    public ArrayList<Videojuego> filtrarPorDificultad(int dificultad, boolean baja) {
        ArrayList<Videojuego> resultado = new ArrayList<>();
        for(Videojuego v : getLista(baja)) {
            if(v.getDificultad() == dificultad) {
                resultado.add(v);
            }
        }
        return resultado;
    }
}